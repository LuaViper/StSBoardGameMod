package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.rows.CombatRowManager;
import CoopBoardGame.multiplayer.rows.MultiCreature;
import CoopBoardGame.multiplayer.rows.PlayerRowManager;
import CoopBoardGame.multiplayer.rows.RowNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patches to position players and enemies in their assigned combat rows.
 * This translates the row assignments (stored in MultiCreature.Field.currentRow)
 * into actual visual positions (drawX, drawY).
 *
 * Uses INSERT patches (rloc=0) on render() methods to ensure our positioning
 * happens AFTER all Prefix patches (including TogetherInSpire's positioning)
 * but BEFORE the actual rendering code executes.
 */
public class CreatureRowPositionPatch {

    private static final Logger logger = LogManager.getLogger(CreatureRowPositionPatch.class.getName());

    // Vertical offset to lower creatures within their rows (in unscaled pixels)
    private static final float ROW_Y_OFFSET = 75f;
    private static final boolean ENABLE_ROW_RENDER_TRACE =
            Boolean.parseBoolean(System.getProperty("coopbg.rowtrace.enabled", "true"));
    private static final boolean DISABLE_COMBAT_ROW_AUTOFILL =
            Boolean.parseBoolean(System.getProperty("coopbg.rowtrace.disableAutofill", "true"));
    private static final int ROW_TRACE_MAX_COMBAT_FRAMES =
            Integer.parseInt(System.getProperty("coopbg.rowtrace.maxFrames", "60"));
    private static final int ROW_TRACE_MAX_LOGS_PER_FRAME =
            Integer.parseInt(System.getProperty("coopbg.rowtrace.maxLogsPerFrame", "120"));

    private static boolean loggedPlayerInCombat = false;
    private static boolean loggedMonsterInCombat = false;
    private static boolean loggedCharacterEntity = false;
    private static boolean appliedPendingRowAssignments = false;
    private static int renderingCharacterPlayerId = -1;
    private static boolean loggedTiSSignatures = false;
    private static long traceFirstFrameId = -1L;
    private static long traceLastSeenFrameId = -1L;
    private static int traceCombatFrameIndex = -1;
    private static int traceLogsThisFrame = 0;
    private static final Map<Integer, float[]> characterEntityRenderPrePosition = new HashMap<>();
    private static final Map<Integer, float[]> characterEntityUpdatePrePosition = new HashMap<>();

    /**
     * Resets the logging flags so we get fresh logs for each combat.
     * Called from MultiCombatEncounterPatches when combat starts.
     */
    public static void resetLogging() {
        loggedPlayerInCombat = false;
        loggedMonsterInCombat = false;
        loggedCharacterEntity = false;
        appliedPendingRowAssignments = false;
        renderingCharacterPlayerId = -1;
        loggedTiSSignatures = false;
        traceFirstFrameId = -1L;
        traceLastSeenFrameId = -1L;
        traceCombatFrameIndex = -1;
        traceLogsThisFrame = 0;
        characterEntityRenderPrePosition.clear();
        characterEntityUpdatePrePosition.clear();
    }

    /**
     * Positions the local player in their assigned row.
     * Uses Insert with rloc=0 to run after all Prefix patches (including TogetherInSpire's)
     * but before the render method body executes.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class PlayerPositionPatch {

        @SpireInsertPatch(rloc = 0)
        public static void Insert(AbstractPlayer __instance, SpriteBatch sb) {
            applyLocalPlayerRowPosition(__instance, "AbstractPlayer.render.insert");
        }
    }

    /**
     * Late render hook for local player positioning.
     * This runs right before player image drawing and acts as a safety net when
     * other mods overwrite draw position later inside AbstractPlayer.render().
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = "renderPlayerImage")
    public static class PlayerRenderImagePositionPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, SpriteBatch sb) {
            applyLocalPlayerRowPosition(__instance, "AbstractPlayer.renderPlayerImage.prefix");
        }
    }

    /**
     * Applies row positioning for player images during combat.
     * In TiS RenderCharacter context, use the currently-rendered remote player ID.
     * Otherwise, only position the true local player instance.
     */
    private static void applyLocalPlayerRowPosition(AbstractPlayer __instance, String sourceTag) {
        // Only position during combat
        if (!isInCombatRoom()) {
            return;
        }

        logTiSSignaturesOnce();

        boolean isBGMode = TogetherInSpireHelper.isMultiplayerBoardGameMode();
        int numRows = getEffectiveRowCount();
        boolean shouldApplyRowPositioning = isBGMode || numRows > 1;

        // Log once per combat for debugging
        if (!loggedPlayerInCombat && shouldApplyRowPositioning) {
            int row = MultiCreature.Field.currentRow.get(__instance);
            logger.info("PlayerPositionPatch RENDER: isBGMode=" + isBGMode +
                    ", numRows=" + numRows + ", playerRow=" + row);
            loggedPlayerInCombat = true;
        }

        if (!shouldApplyRowPositioning) {
            return;
        }

        if (numRows <= 1) {
            return;
        }

        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        boolean inP2PRenderContext = renderingCharacterPlayerId >= 0;
        boolean isLocalPlayerInstance = __instance == AbstractDungeon.player;

        // TiS can render a remote character via AbstractPlayer.renderPlayerImage.
        // Do not force that instance to local row; use active P2P render context instead.
        Integer targetPlayerId = null;
        if (inP2PRenderContext) {
            targetPlayerId = renderingCharacterPlayerId;
        } else if (isLocalPlayerInstance) {
            targetPlayerId = localPlayerId;
        } else {
            traceDiagnostic("SKIP AbstractPlayer row write (non-local player outside P2P context)",
                    __instance, -1, renderingCharacterPlayerId, -1);
            return;
        }

        if (DISABLE_COMBAT_ROW_AUTOFILL && !PlayerRowManager.hasPlayerRow(targetPlayerId)) {
            traceDiagnostic("SKIP AbstractPlayer row write (no authoritative row)",
                    __instance, targetPlayerId, renderingCharacterPlayerId, -1);
            return;
        }

        // Keep local player row aligned with authoritative row assignments.
        if (isLocalPlayerInstance && targetPlayerId == localPlayerId && PlayerRowManager.hasLocalPlayerRow()) {
            int assignedRow = PlayerRowManager.getLocalPlayerRow();
            if (MultiCreature.Field.currentRow.get(__instance) != assignedRow) {
                MultiCreature.Field.currentRow.set(__instance, assignedRow);
            }
        }

        int row = targetPlayerId == localPlayerId && isLocalPlayerInstance
                ? MultiCreature.Field.currentRow.get(__instance)
                : PlayerRowManager.getPlayerRow(targetPlayerId);

        // Calculate target position
        float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
        float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;
        float beforeX = __instance.drawX;
        float beforeY = __instance.drawY;

        // Apply position
        __instance.drawX = targetX;
        __instance.drawY = targetY;
        traceWrite(sourceTag, __instance, targetPlayerId, row, beforeX, beforeY, targetX, targetY);

        // Update hitbox to match new position
        // drawY is the "feet" position; center hitbox on character body (above drawY)
        if (__instance.hb != null) {
            __instance.hb.move(__instance.drawX, __instance.drawY + __instance.hb.height / 2f);
        }
        // Position health bar at bottom of hitbox (hb.y is bottom edge)
        if (__instance.healthHb != null && __instance.hb != null) {
            __instance.healthHb.move(__instance.hb.cX, __instance.hb.y - __instance.healthHb.height / 2f);
        }
    }

    /**
     * Positions monsters (enemies) and CharacterEntities (remote players) in their assigned rows.
     * CharacterEntities are positioned like players (left side), actual monsters on the right.
     * Uses Insert with rloc=0 to run after all Prefix patches (including TogetherInSpire's)
     * but before the render method body executes.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "render")
    public static class MonsterPositionPatch {

        @SpireInsertPatch(rloc = 0)
        public static void Insert(AbstractMonster __instance, SpriteBatch sb) {
            // Only position during combat
            if (!isInCombatRoom()) {
                return;
            }

            logTiSSignaturesOnce();

            boolean isBGMode = TogetherInSpireHelper.isMultiplayerBoardGameMode();
            int numRows = getEffectiveRowCount();
            boolean shouldApplyRowPositioning = isBGMode || numRows > 1;

            // Apply any pending row assignments from network (timing issue workaround)
            // Row assignments may arrive before TogetherInSpire syncs monsters to the client
            if (shouldApplyRowPositioning && RowNetworkHelper.hasPendingMonsterRowAssignments()) {
                if (!appliedPendingRowAssignments) {
                    logger.info("Attempting to apply pending monster row assignments (delayed sync)");
                }
                RowNetworkHelper.applyPendingMonsterRowAssignments();
                // Only mark as applied once pending assignments are actually cleared
                if (!RowNetworkHelper.hasPendingMonsterRowAssignments()) {
                    logger.info("Successfully applied pending monster row assignments");
                    appliedPendingRowAssignments = true;
                }
            }

            // Log once per combat for debugging
            if (!loggedMonsterInCombat && shouldApplyRowPositioning) {
                int row = MultiCreature.Field.currentRow.get(__instance);
                logger.info("MonsterPositionPatch RENDER: isBGMode=" + isBGMode + ", numRows=" + numRows +
                        ", monster=" + __instance.name + ", row=" + row);
                loggedMonsterInCombat = true;
            }

            if (!shouldApplyRowPositioning) {
                return;
            }

            if (numRows <= 1) {
                return;
            }

            // Check if this is a CharacterEntity (remote player)
            if (TogetherInSpireHelper.isCharacterEntity(__instance)) {
                positionCharacterEntity(__instance, numRows);
                return;
            }

            // Position as a regular monster
            int row = MultiCreature.Field.currentRow.get(__instance);

            // Calculate index within row for horizontal spacing
            int indexInRow = CombatRowManager.getMonsterIndexInRow(__instance, row);

            // Calculate target position
            float targetX = Settings.WIDTH * CombatRowManager.ENEMY_START_X_FRACTION
                    + (indexInRow * Settings.WIDTH * CombatRowManager.ENEMY_SPACING_FRACTION);
            float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;

            // Apply position
            __instance.drawX = targetX;
            __instance.drawY = targetY;

            // Update hitbox to match new position
            // Monster hitboxes use hb_x, hb_y as offsets from drawX, drawY
            __instance.hb.move(
                    __instance.drawX + __instance.hb_x,
                    __instance.drawY + __instance.hb_y + __instance.hb_h / 2f
            );
            __instance.healthHb.move(
                    __instance.hb.cX,
                    __instance.hb.y - __instance.healthHb.height / 2f
            );
        }

        /**
         * Positions a CharacterEntity (remote player) in their assigned row.
         */
        private static void positionCharacterEntity(AbstractMonster entity, int numRows) {
            // Get player ID from CharacterEntity
            int playerId = TogetherInSpireHelper.getCharacterEntityPlayerId(entity);
            if (playerId < 0) {
                if (!loggedCharacterEntity) {
                    logger.warn("CharacterEntity has invalid playerId: " + playerId);
                }
                return;
            }

            if (DISABLE_COMBAT_ROW_AUTOFILL && !PlayerRowManager.hasPlayerRow(playerId)) {
                traceDiagnostic("SKIP CharacterEntity position write (no host row)", entity, playerId, renderingCharacterPlayerId, -1);
                return;
            }

            // Get row for this player
            int row = PlayerRowManager.getPlayerRow(playerId);

            // Log once per combat for debugging
            if (!loggedCharacterEntity) {
                logger.info("CharacterEntity positioning: playerId=" + playerId +
                        ", row=" + row + ", hasAssignments=" + PlayerRowManager.hasAssignments() +
                        ", rowCount=" + PlayerRowManager.getRowCount());
                loggedCharacterEntity = true;
            }

            // Calculate target position (same X as local player)
            float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
            float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;
            float beforeX = entity.drawX;
            float beforeY = entity.drawY;

            // Apply position
            entity.drawX = targetX;
            entity.drawY = targetY;
            traceWrite("AbstractMonster.render.insert(CharacterEntity)", entity, playerId, row, beforeX, beforeY, targetX, targetY);

            // Update hitbox to match new position
            // drawY is the "feet" position; center hitbox on character body (above drawY)
            if (entity.hb != null) {
                entity.hb.move(entity.drawX, entity.drawY + entity.hb.height / 2f);
            }
            // Position health bar at bottom of hitbox (hb.y is bottom edge)
            if (entity.healthHb != null && entity.hb != null) {
                entity.healthHb.move(entity.hb.cX, entity.hb.y - entity.healthHb.height / 2f);
            }
        }
    }

    /**
     * Track which P2PPlayer is currently rendering a CharacterEntity so we can
     * resolve row assignment by authoritative player ID.
     */
    @SpirePatch(
        cls = "spireTogether.network.P2P.P2PPlayer",
        method = "RenderCharacter",
        paramtypez = {SpriteBatch.class, Integer.class},
        requiredModId = "spireTogether",
        optional = true
    )
    public static class P2PPlayerRenderCharacterTrackingPatch {
        @SpirePrefixPatch
        public static void Prefix(Object __instance, SpriteBatch sb, Integer index) {
            beginP2PRenderCharacterTracking(__instance, index == null ? -1 : index.intValue(), "Integer");
        }

        @SpirePostfixPatch
        public static void Postfix(Object __instance, SpriteBatch sb, Integer index) {
            endP2PRenderCharacterTracking(__instance, index == null ? -1 : index.intValue(), "Integer");
        }
    }

    /**
     * TogetherInSpire CharacterEntity has its own positioning flow that can overwrite
     * row placement on host. Override its SetDrawPosition during BG multiplayer combat
     * so remote players stay in their assigned rows.
     */
    @SpirePatch(
        cls = "spireTogether.monsters.CharacterEntity",
        method = "SetDrawPosition",
        paramtypez = {float.class, float.class},
        requiredModId = "spireTogether",
        optional = true
    )
    public static class CharacterEntitySetDrawPositionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(Object __instance, float x, float y) {
            return applyCharacterEntityRowPosition(__instance);
        }
    }

    @SpirePatch(
        cls = "spireTogether.ui.CharacterEntity",
        method = "SetDrawPosition",
        paramtypez = {float.class, float.class},
        requiredModId = "spireTogether",
        optional = true
    )
    public static class CharacterEntitySetDrawPositionPatchLegacy {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(Object __instance, float x, float y) {
            return applyCharacterEntityRowPosition(__instance);
        }
    }

    private static SpireReturn<Void> applyCharacterEntityRowPosition(Object __instance) {
        if (!(__instance instanceof AbstractMonster)) {
            return SpireReturn.Continue();
        }

        if (!isInCombatRoom()) {
            return SpireReturn.Continue();
        }

        // Only trust player ID from active P2PPlayer.RenderCharacter context.
        // Entity-level IDs can be stale/inconsistent across some TiS flows.
        if (renderingCharacterPlayerId < 0) {
            return SpireReturn.Continue();
        }

        boolean isBGMode = TogetherInSpireHelper.isMultiplayerBoardGameMode();
        int numRows = getEffectiveRowCount();
        if (!(isBGMode || numRows > 1) || numRows <= 1) {
            return SpireReturn.Continue();
        }

        AbstractMonster entity = (AbstractMonster)__instance;
        int playerId = renderingCharacterPlayerId;

        // If host snapshot arrived incomplete on this client, backfill missing rows
        // from live BG roster so each CharacterEntity can still be placed.
        if (!PlayerRowManager.hasPlayerRow(playerId) && !shouldDisableCombatAutofill()) {
            List<Integer> bgPlayerIds = TogetherInSpireHelper.getBoardGamePlayerIds();
            if (!bgPlayerIds.isEmpty()) {
                PlayerRowManager.ensurePlayerRowsForIds(bgPlayerIds);
                numRows = Math.max(numRows, PlayerRowManager.getRowCount());
            }
        }

        if (shouldDisableCombatAutofill() && !PlayerRowManager.hasPlayerRow(playerId)) {
            traceDiagnostic("SetDrawPosition override skipped (no host row)", entity, playerId, renderingCharacterPlayerId, -1);
            return SpireReturn.Continue();
        }

        int row = PlayerRowManager.getPlayerRow(playerId);
        float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
        float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;
        float beforeX = entity.drawX;
        float beforeY = entity.drawY;

        entity.drawX = targetX;
        entity.drawY = targetY;
        traceWrite("CharacterEntity.SetDrawPosition.prefixOverride", entity, playerId, row, beforeX, beforeY, targetX, targetY);

        if (AbstractDungeon.player != null && entity.hb != null && AbstractDungeon.player.hb != null) {
            entity.hb.height = AbstractDungeon.player.hb.height;
        }

        // Update hitbox to match new position
        // drawY is the "feet" position; center hitbox on character body (above drawY)
        if (entity.hb != null) {
            entity.hb.move(entity.drawX, entity.drawY + entity.hb.height / 2f);
        }
        // Position health bar at bottom of hitbox (hb.y is bottom edge)
        if (entity.healthHb != null && entity.hb != null) {
            entity.healthHb.move(entity.hb.cX, entity.hb.y - entity.healthHb.height / 2f);
        }

        return SpireReturn.Return();
    }

    @SpirePatch(
        cls = "spireTogether.monsters.CharacterEntity",
        method = "render",
        paramtypez = {SpriteBatch.class},
        requiredModId = "spireTogether",
        optional = true
    )
    public static class CharacterEntityRenderProbePatch {
        @SpirePrefixPatch
        public static void Prefix(Object __instance, SpriteBatch sb) {
            recordProbePrefix(characterEntityRenderPrePosition, __instance);
        }

        @SpirePostfixPatch
        public static void Postfix(Object __instance, SpriteBatch sb) {
            recordProbePostfix("CharacterEntity.render", characterEntityRenderPrePosition, __instance);
        }
    }

    @SpirePatch(
        cls = "spireTogether.ui.CharacterEntity",
        method = "render",
        paramtypez = {SpriteBatch.class},
        requiredModId = "spireTogether",
        optional = true
    )
    public static class CharacterEntityRenderProbePatchLegacy {
        @SpirePrefixPatch
        public static void Prefix(Object __instance, SpriteBatch sb) {
            recordProbePrefix(characterEntityRenderPrePosition, __instance);
        }

        @SpirePostfixPatch
        public static void Postfix(Object __instance, SpriteBatch sb) {
            recordProbePostfix("CharacterEntity.render(legacy)", characterEntityRenderPrePosition, __instance);
        }
    }

    @SpirePatch(
        cls = "spireTogether.monsters.CharacterEntity",
        method = "update",
        requiredModId = "spireTogether",
        optional = true
    )
    public static class CharacterEntityUpdateProbePatch {
        @SpirePrefixPatch
        public static void Prefix(Object __instance) {
            recordProbePrefix(characterEntityUpdatePrePosition, __instance);
        }

        @SpirePostfixPatch
        public static void Postfix(Object __instance) {
            recordProbePostfix("CharacterEntity.update", characterEntityUpdatePrePosition, __instance);
        }
    }

    @SpirePatch(
        cls = "spireTogether.ui.CharacterEntity",
        method = "update",
        requiredModId = "spireTogether",
        optional = true
    )
    public static class CharacterEntityUpdateProbePatchLegacy {
        @SpirePrefixPatch
        public static void Prefix(Object __instance) {
            recordProbePrefix(characterEntityUpdatePrePosition, __instance);
        }

        @SpirePostfixPatch
        public static void Postfix(Object __instance) {
            recordProbePostfix("CharacterEntity.update(legacy)", characterEntityUpdatePrePosition, __instance);
        }
    }

    /**
     * Gets player ID from TogetherInSpire P2PPlayer via reflection.
     */
    private static int getP2PPlayerId(Object p2pPlayer) {
        if (p2pPlayer == null) {
            return -1;
        }

        try {
            java.lang.reflect.Method getIdMethod = p2pPlayer.getClass().getMethod("GetID");
            Object id = getIdMethod.invoke(p2pPlayer);
            if (id instanceof Integer) {
                return (Integer) id;
            }
        } catch (Exception ignored) {
            // Fall through to direct field lookup.
        }

        try {
            java.lang.reflect.Field idField = p2pPlayer.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(p2pPlayer);
            if (id instanceof Integer) {
                return (Integer) id;
            }
        } catch (Exception ignored) {
            return -1;
        }

        return -1;
    }

    private static Object getP2PPlayerEntity(Object p2pPlayer) {
        if (p2pPlayer == null) {
            return null;
        }

        try {
            java.lang.reflect.Method getEntityMethod = p2pPlayer.getClass().getMethod("GetEntity");
            return getEntityMethod.invoke(p2pPlayer);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void beginP2PRenderCharacterTracking(Object p2pPlayer, int index, String signatureTag) {
        renderingCharacterPlayerId = getP2PPlayerId(p2pPlayer);
        Object entityObj = getP2PPlayerEntity(p2pPlayer);
        if (entityObj instanceof AbstractMonster) {
            AbstractMonster entity = (AbstractMonster) entityObj;
            int resolvedEntityPlayerId = TogetherInSpireHelper.getCharacterEntityPlayerId(entity);
            int row = resolvedEntityPlayerId >= 0 && PlayerRowManager.hasPlayerRow(resolvedEntityPlayerId)
                    ? PlayerRowManager.getPlayerRow(resolvedEntityPlayerId)
                    : -1;
            traceDiagnostic("P2PPlayer.RenderCharacter.prefix(" + signatureTag + ") idx=" + index,
                    entity, resolvedEntityPlayerId, renderingCharacterPlayerId, row);
        }
    }

    private static void endP2PRenderCharacterTracking(Object p2pPlayer, int index, String signatureTag) {
        Object entityObj = getP2PPlayerEntity(p2pPlayer);
        if (entityObj instanceof AbstractMonster) {
            AbstractMonster entity = (AbstractMonster) entityObj;
            int resolvedEntityPlayerId = TogetherInSpireHelper.getCharacterEntityPlayerId(entity);
            int row = resolvedEntityPlayerId >= 0 && PlayerRowManager.hasPlayerRow(resolvedEntityPlayerId)
                    ? PlayerRowManager.getPlayerRow(resolvedEntityPlayerId)
                    : -1;
            traceDiagnostic("P2PPlayer.RenderCharacter.postfix(" + signatureTag + ") idx=" + index,
                    entity, resolvedEntityPlayerId, renderingCharacterPlayerId, row);
        }
        renderingCharacterPlayerId = -1;
    }

    private static void logTiSSignaturesOnce() {
        if (loggedTiSSignatures) {
            return;
        }

        loggedTiSSignatures = true;
        TogetherInSpireHelper.logRowPositionDiagnosticsSignaturesOnce();
    }

    /**
     * Safe combat-room check that avoids AbstractDungeon.getCurrRoom() NPEs in menus/lobby.
     */
    private static boolean isInCombatRoom() {
        AbstractRoom room = getCurrentRoomSafe();
        return room != null && room.phase == AbstractRoom.RoomPhase.COMBAT;
    }

    /**
     * Safely resolves current room. TogetherInSpire can call render-position paths in lobby,
     * before dungeon room state is initialized.
     */
    private static AbstractRoom getCurrentRoomSafe() {
        try {
            return AbstractDungeon.getCurrRoom();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    /**
     * Gets an effective row count for combat positioning.
     * Also backfills missing row assignments from the live BG roster when needed.
     */
    private static int getEffectiveRowCount() {
        int numRows = PlayerRowManager.getRowCount();
        List<Integer> bgPlayerIds = TogetherInSpireHelper.getBoardGamePlayerIds();
        if (bgPlayerIds.size() > numRows && !shouldDisableCombatAutofill()) {
            PlayerRowManager.ensurePlayerRowsForIds(bgPlayerIds);
            numRows = PlayerRowManager.getRowCount();
        }

        return numRows;
    }

    private static boolean shouldDisableCombatAutofill() {
        return DISABLE_COMBAT_ROW_AUTOFILL && isInCombatRoom();
    }

    private static int currentTraceCombatFrame() {
        if (!ENABLE_ROW_RENDER_TRACE || !isInCombatRoom()) {
            return -1;
        }

        long frameId = -1L;
        try {
            if (Gdx.graphics != null) {
                frameId = Gdx.graphics.getFrameId();
            }
        } catch (Exception ignored) {
            return -1;
        }

        if (frameId < 0L) {
            return -1;
        }

        if (traceFirstFrameId < 0L) {
            traceFirstFrameId = frameId;
        }

        if (frameId != traceLastSeenFrameId) {
            traceLastSeenFrameId = frameId;
            traceCombatFrameIndex = (int) (frameId - traceFirstFrameId);
            traceLogsThisFrame = 0;
        }

        return traceCombatFrameIndex;
    }

    private static boolean shouldTraceFrame() {
        int combatFrame = currentTraceCombatFrame();
        return combatFrame >= 0 && combatFrame < ROW_TRACE_MAX_COMBAT_FRAMES;
    }

    private static boolean allowTraceLogThisFrame() {
        if (!shouldTraceFrame()) {
            return false;
        }

        if (traceLogsThisFrame >= ROW_TRACE_MAX_LOGS_PER_FRAME) {
            return false;
        }

        traceLogsThisFrame++;
        return true;
    }

    private static void traceWrite(
            String source,
            Object entityObj,
            int resolvedPlayerId,
            int row,
            float beforeX,
            float beforeY,
            float afterX,
            float afterY) {
        if (!allowTraceLogThisFrame()) {
            return;
        }

        logger.info("[RowTrace frame=" + currentTraceCombatFrame() +
                " src=" + source +
                "] entity=" + describeEntity(entityObj) +
                ", resolvedPlayerId=" + resolvedPlayerId +
                ", renderCtxPlayerId=" + renderingCharacterPlayerId +
                ", row=" + row +
                ", pos=(" + beforeX + "," + beforeY + ")->(" + afterX + "," + afterY + ")");
    }

    private static void traceDiagnostic(
            String source,
            Object entityObj,
            int resolvedPlayerId,
            int renderContextPlayerId,
            int row) {
        if (!allowTraceLogThisFrame()) {
            return;
        }

        String pos = "";
        if (entityObj instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster) entityObj;
            pos = ", pos=(" + m.drawX + "," + m.drawY + ")";
        } else if (entityObj instanceof AbstractPlayer) {
            AbstractPlayer p = (AbstractPlayer) entityObj;
            pos = ", pos=(" + p.drawX + "," + p.drawY + ")";
        }

        logger.info("[RowTrace frame=" + currentTraceCombatFrame() +
                " src=" + source +
                "] entity=" + describeEntity(entityObj) +
                ", resolvedPlayerId=" + resolvedPlayerId +
                ", renderCtxPlayerId=" + renderContextPlayerId +
                ", row=" + row + pos);
    }

    private static void recordProbePrefix(Map<Integer, float[]> store, Object entityObj) {
        if (!shouldTraceFrame()) {
            return;
        }

        if (!(entityObj instanceof AbstractMonster)) {
            return;
        }

        AbstractMonster entity = (AbstractMonster) entityObj;
        int identity = System.identityHashCode(entity);
        store.put(identity, new float[]{entity.drawX, entity.drawY});
    }

    private static void recordProbePostfix(String source, Map<Integer, float[]> store, Object entityObj) {
        if (!(entityObj instanceof AbstractMonster)) {
            return;
        }
        if (!allowTraceLogThisFrame()) {
            return;
        }

        AbstractMonster entity = (AbstractMonster) entityObj;
        int identity = System.identityHashCode(entity);
        float[] before = store.remove(identity);
        float beforeX = before != null ? before[0] : entity.drawX;
        float beforeY = before != null ? before[1] : entity.drawY;
        float afterX = entity.drawX;
        float afterY = entity.drawY;

        int resolvedPlayerId = TogetherInSpireHelper.getCharacterEntityPlayerId(entity);
        int row = resolvedPlayerId >= 0 && PlayerRowManager.hasPlayerRow(resolvedPlayerId)
                ? PlayerRowManager.getPlayerRow(resolvedPlayerId)
                : -1;

        logger.info("[RowTrace frame=" + currentTraceCombatFrame() +
                " src=" + source +
                "] entity=" + describeEntity(entity) +
                ", resolvedPlayerId=" + resolvedPlayerId +
                ", renderCtxPlayerId=" + renderingCharacterPlayerId +
                ", row=" + row +
                ", pos=(" + beforeX + "," + beforeY + ")->(" + afterX + "," + afterY + ")");
    }

    private static String describeEntity(Object entityObj) {
        if (entityObj == null) {
            return "null";
        }

        String className = entityObj.getClass().getName();
        int identity = System.identityHashCode(entityObj);
        if (entityObj instanceof AbstractMonster) {
            AbstractMonster monster = (AbstractMonster) entityObj;
            return className + "@" + identity + "[name=" + monster.name + ",id=" + monster.id + "]";
        }
        if (entityObj instanceof AbstractPlayer) {
            AbstractPlayer player = (AbstractPlayer) entityObj;
            return className + "@" + identity + "[name=" + player.name + "]";
        }
        return className + "@" + identity;
    }
}
