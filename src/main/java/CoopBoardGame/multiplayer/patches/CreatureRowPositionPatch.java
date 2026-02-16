package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.rows.CombatRowManager;
import CoopBoardGame.multiplayer.rows.MultiCreature;
import CoopBoardGame.multiplayer.rows.PlayerRowManager;
import CoopBoardGame.multiplayer.rows.RowNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
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
 * Uses PREFIX patches on render() methods instead of update() to ensure our
 * positioning happens AFTER TogetherInSpire's update-time positioning but
 * BEFORE the actual rendering, so our positions take visual precedence.
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
     * Uses render() prefix to run after TogetherInSpire's update() positioning.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class PlayerPositionPatch {

        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, SpriteBatch sb) {
            // Only position during combat
            if (AbstractDungeon.getCurrRoom() == null ||
                AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
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

            int row = MultiCreature.Field.currentRow.get(__instance);

            // Calculate target position
            float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
            float targetY = (CombatRowManager.getRowCenterY(row, numRows) - ROW_Y_OFFSET) * Settings.scale;

            // Apply position
            __instance.drawX = targetX;
            __instance.drawY = targetY;

            // Update hitbox to match new position
            __instance.hb.move(__instance.drawX, __instance.drawY);
            __instance.healthHb.move(__instance.hb.cX, __instance.hb.cY - __instance.hb.height / 2f);
        }
    }

    /**
     * Positions monsters (enemies) and CharacterEntities (remote players) in their assigned rows.
     * CharacterEntities are positioned like players (left side), actual monsters on the right.
     * Uses render() prefix to run after TogetherInSpire's update() positioning.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "render")
    public static class MonsterPositionPatch {

        @SpirePrefixPatch
        public static void Prefix(AbstractMonster __instance, SpriteBatch sb) {
            // Only position during combat
            if (AbstractDungeon.getCurrRoom() == null ||
                AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
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
            entity.hb.move(entity.drawX, entity.drawY);
            entity.healthHb.move(entity.hb.cX, entity.hb.cY - entity.hb.height / 2f);
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
