package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.rows.MultiCreature;
import CoopBoardGame.multiplayer.rows.PlayerRowManager;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Patches to implement row-based targeting for multiplayer board game mode.
 * Enemies only attack players in their exact row - they cannot target players in other rows.
 * This enforces the board game rule where each player fights enemies in their own row.
 */
public class RowBasedTargetingPatch {

    private static final Logger logger = LogManager.getLogger(RowBasedTargetingPatch.class.getName());

    /**
     * Checks if a monster can target the local player based on row matching.
     * Returns true if:
     * - We're not in multiplayer board game mode (standard targeting)
     * - The monster is in the same row as the local player
     * - The source is not a monster (e.g., player self-damage, card effects)
     *
     * @param source The creature dealing damage or applying effects
     * @return true if the local player should be affected, false to skip
     */
    public static boolean canMonsterTargetLocalPlayer(AbstractCreature source) {
        // Not in multiplayer board game mode - allow all targeting
        if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
            return true;
        }

        // Only filter monster attacks
        if (!(source instanceof AbstractMonster)) {
            return true;
        }

        // Skip CharacterEntities (remote players) - they shouldn't be sources of enemy damage
        if (TogetherInSpireHelper.isCharacterEntity(source)) {
            return true;
        }

        // Get the monster's row
        int monsterRow = MultiCreature.Field.currentRow.get(source);

        // Get the local player's row
        int playerRow = PlayerRowManager.getLocalPlayerRow();

        // Only allow targeting if in the same row
        boolean sameRow = monsterRow == playerRow;

        if (!sameRow) {
            logger.debug("Row mismatch - blocking attack from " + source.name +
                    " (row " + monsterRow + ") against local player (row " + playerRow + ")");
        }

        return sameRow;
    }

    /**
     * Patches AbstractPlayer.damage() to check row targeting before applying damage.
     * When a monster attacks the player, the DamageInfo.owner contains the source.
     * If the monster is not in the same row as the local player, skip the damage.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = "damage")
    public static class PlayerDamagePatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, DamageInfo info) {
            // Only intercept during combat
            if (AbstractDungeon.getCurrRoom() == null ||
                AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                return SpireReturn.Continue();
            }

            // Check if we have an owner (source of damage)
            if (info == null || info.owner == null) {
                return SpireReturn.Continue();
            }

            // Check if the monster can target this player
            if (!canMonsterTargetLocalPlayer(info.owner)) {
                // Skip this damage - monster is in a different row
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    /**
     * Patches ApplyPowerAction to check row targeting before applying debuffs.
     * Monsters applying Vulnerable, Weak, Poison, etc. should only affect players in their row.
     */
    @SpirePatch2(clz = ApplyPowerAction.class, method = "update")
    public static class ApplyPowerPatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ApplyPowerAction __instance,
                                                AbstractCreature ___target,
                                                AbstractCreature ___source) {
            // Only intercept during combat
            if (AbstractDungeon.getCurrRoom() == null ||
                AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                return SpireReturn.Continue();
            }

            // Only intercept powers being applied to the local player
            if (___target != AbstractDungeon.player) {
                return SpireReturn.Continue();
            }

            // Check if source is null
            if (___source == null) {
                return SpireReturn.Continue();
            }

            // Check if the monster can target this player
            if (!canMonsterTargetLocalPlayer(___source)) {
                // Skip this power application - monster is in a different row
                __instance.isDone = true;
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    /**
     * Patches ReducePowerAction to check row targeting before reducing player powers.
     * Used by enemies like Lagavulin to reduce Strength/Dexterity.
     */
    @SpirePatch2(clz = ReducePowerAction.class, method = "update")
    public static class ReducePowerPatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ReducePowerAction __instance,
                                                AbstractCreature ___target,
                                                AbstractCreature ___source) {
            // Only intercept during combat
            if (AbstractDungeon.getCurrRoom() == null ||
                AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                return SpireReturn.Continue();
            }

            // Only intercept powers being reduced on the local player
            if (___target != AbstractDungeon.player) {
                return SpireReturn.Continue();
            }

            // Check if source is null
            if (___source == null) {
                return SpireReturn.Continue();
            }

            // Check if the monster can target this player
            if (!canMonsterTargetLocalPlayer(___source)) {
                // Skip this power reduction - monster is in a different row
                __instance.isDone = true;
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

}
