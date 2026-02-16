package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.rows.CombatRowManager;
import CoopBoardGame.multiplayer.rows.MultiCreature;
import CoopBoardGame.multiplayer.rows.PlayerRowManager;
import CoopBoardGame.multiplayer.rows.RowNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
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

import java.util.List;

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

    private static boolean loggedPlayerInCombat = false;
    private static boolean loggedMonsterInCombat = false;
    private static boolean loggedCharacterEntity = false;
    private static boolean appliedPendingRowAssignments = false;

    /**
     * Resets the logging flags so we get fresh logs for each combat.
     * Called from MultiCombatEncounterPatches when combat starts.
     */
    public static void resetLogging() {
        loggedPlayerInCombat = false;
        loggedMonsterInCombat = false;
        loggedCharacterEntity = false;
        appliedPendingRowAssignments = false;
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
            applyLocalPlayerRowPosition(__instance);
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
            applyLocalPlayerRowPosition(__instance);
        }
    }

    /**
     * Applies row positioning for the local player in combat.
     */
    private static void applyLocalPlayerRowPosition(AbstractPlayer __instance) {
        // Only position during combat
        if (!isInCombatRoom()) {
            return;
        }

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

        // Keep local player row aligned with authoritative row assignments.
        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        if (PlayerRowManager.hasPlayerRow(localPlayerId)) {
            int assignedRow = PlayerRowManager.getPlayerRow(localPlayerId);
            if (MultiCreature.Field.currentRow.get(__instance) != assignedRow) {
                MultiCreature.Field.currentRow.set(__instance, assignedRow);
            }
        }

        int row = MultiCreature.Field.currentRow.get(__instance);

        // Calculate target position
        float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
        float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;

        // Apply position
        __instance.drawX = targetX;
        __instance.drawY = targetY;

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

            // Apply position
            entity.drawX = targetX;
            entity.drawY = targetY;

            // Update hitbox to match new position
            // drawY is the "feet" position; center hitbox on character body (above drawY)
            entity.hb.move(entity.drawX, entity.drawY + entity.hb.height / 2f);
            // Position health bar at bottom of hitbox (hb.y is bottom edge)
            entity.healthHb.move(entity.hb.cX, entity.hb.y - entity.healthHb.height / 2f);
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

        boolean isBGMode = TogetherInSpireHelper.isMultiplayerBoardGameMode();
        int numRows = getEffectiveRowCount();
        if (!(isBGMode || numRows > 1) || numRows <= 1) {
            return SpireReturn.Continue();
        }

        AbstractMonster entity = (AbstractMonster)__instance;
        int playerId = TogetherInSpireHelper.getCharacterEntityPlayerId(__instance);
        if (playerId < 0) {
            return SpireReturn.Continue();
        }

        int row = PlayerRowManager.getPlayerRow(playerId);
        float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
        float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;

        entity.drawX = targetX;
        entity.drawY = targetY;

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
     * On host, this also backfills missing row assignments from the live BG roster.
     */
    private static int getEffectiveRowCount() {
        int numRows = PlayerRowManager.getRowCount();

        if (!TogetherInSpireHelper.isHost()) {
            return numRows;
        }

        List<Integer> bgPlayerIds = TogetherInSpireHelper.getBoardGamePlayerIds();
        if (bgPlayerIds.size() > numRows) {
            PlayerRowManager.ensurePlayerRowsForIds(bgPlayerIds);
            numRows = PlayerRowManager.getRowCount();
        }

        return numRows;
    }
}
