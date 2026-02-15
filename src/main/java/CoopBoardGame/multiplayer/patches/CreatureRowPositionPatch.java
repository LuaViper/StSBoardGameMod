package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.rows.CombatRowManager;
import CoopBoardGame.multiplayer.rows.MultiCreature;
import CoopBoardGame.multiplayer.rows.PlayerRowManager;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * Patches to position players and enemies in their assigned combat rows.
 * This translates the row assignments (stored in MultiCreature.Field.currentRow)
 * into actual visual positions (drawX, drawY).
 */
public class CreatureRowPositionPatch {

    /**
     * Positions the local player in their assigned row.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = "update")
    public static class PlayerPositionPatch {

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            int numRows = PlayerRowManager.getRowCount();
            if (numRows <= 1) {
                return;
            }

            int row = MultiCreature.Field.currentRow.get(__instance);

            // Calculate target position
            float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
            float targetY = CombatRowManager.getRowCenterY(row, numRows) * Settings.scale;

            // Apply position
            __instance.drawX = targetX;
            __instance.drawY = targetY;

            // Update hitbox to match new position
            __instance.hb.move(__instance.drawX, __instance.drawY);
            __instance.healthHb.move(__instance.hb.cX, __instance.hb.cY - __instance.hb.height / 2f - __instance.healthHb.height / 2f);
        }
    }

    /**
     * Positions monsters (enemies) and CharacterEntities (remote players) in their assigned rows.
     * CharacterEntities are positioned like players (left side), actual monsters on the right.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "update")
    public static class MonsterPositionPatch {

        @SpirePostfixPatch
        public static void Postfix(AbstractMonster __instance) {
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            int numRows = PlayerRowManager.getRowCount();
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
            float targetY = CombatRowManager.getRowCenterY(row, numRows) * Settings.scale;

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
                return;
            }

            // Get row for this player
            int row = PlayerRowManager.getPlayerRow(playerId);

            // Calculate target position (same X as local player)
            float targetX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
            float targetY = CombatRowManager.getRowCenterY(row, numRows) * Settings.scale;

            // Apply position
            entity.drawX = targetX;
            entity.drawY = targetY;

            // Update hitbox to match new position
            entity.hb.move(entity.drawX, entity.drawY);
            entity.healthHb.move(
                    entity.hb.cX,
                    entity.hb.cY - entity.hb.height / 2f - entity.healthHb.height / 2f
            );
        }
    }
}
