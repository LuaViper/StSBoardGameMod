package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Patches to disable TogetherInSpire's enemy HP scaling in multiplayer.
 * Board game monsters should have their original HP values regardless of player count.
 *
 * TogetherInSpire scales enemy HP based on player count to increase difficulty.
 * For board game mode, we want enemies to have their exact board game HP values.
 */
public class DisableEnemyHPScalingPatch {

    private static final Logger logger = LogManager.getLogger(DisableEnemyHPScalingPatch.class.getName());

    /**
     * SpireField to store the intended HP values before any scaling.
     * We capture these in the prefix patch and restore them in the postfix patch.
     */
    @SpirePatch(clz = AbstractMonster.class, method = SpirePatch.CLASS)
    public static class IntendedHP {
        public static SpireField<Integer> intendedMaxHP = new SpireField<>(() -> -1);
        public static SpireField<Integer> intendedMinHP = new SpireField<>(() -> -1);
    }

    /**
     * Prefix patch on setHp(int, int) to capture the intended HP values before
     * TogetherInSpire can modify them.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "setHp", paramtypez = {int.class, int.class})
    public static class CaptureIntendedHPRange {

        @SpirePrefixPatch
        public static void captureHP(AbstractMonster __instance, int minHP, int maxHP) {
            // Only capture for board game monsters in multiplayer mode
            if (__instance instanceof AbstractBGMonster && TogetherInSpireHelper.isMultiplayerActive()) {
                IntendedHP.intendedMinHP.set(__instance, minHP);
                IntendedHP.intendedMaxHP.set(__instance, maxHP);
            }
        }
    }

    /**
     * Postfix patch on setHp(int, int) to restore the original HP values
     * after TogetherInSpire has scaled them.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "setHp", paramtypez = {int.class, int.class})
    public static class RestoreIntendedHPRange {

        @SpirePostfixPatch
        public static void restoreHP(AbstractMonster __instance) {
            if (!(__instance instanceof AbstractBGMonster) || !TogetherInSpireHelper.isMultiplayerActive()) {
                return;
            }

            int intendedMin = IntendedHP.intendedMinHP.get(__instance);
            int intendedMax = IntendedHP.intendedMaxHP.get(__instance);

            // Only restore if we captured values and they differ from current
            if (intendedMin > 0 && intendedMax > 0) {
                // Check if HP was scaled (current values differ from intended)
                if (__instance.maxHealth != intendedMax || __instance.currentHealth > intendedMax) {
                    // Recalculate using original range (mimics original setHp behavior)
                    int originalHP;
                    if (intendedMin == intendedMax) {
                        originalHP = intendedMin;
                    } else {
                        // Use the same random value ratio that was used for scaling
                        float ratio = (float) __instance.currentHealth / __instance.maxHealth;
                        originalHP = Math.round(intendedMin + ratio * (intendedMax - intendedMin));
                        // Clamp to valid range
                        originalHP = Math.max(intendedMin, Math.min(intendedMax, originalHP));
                    }

                    logger.debug("Restoring " + __instance.name + " HP from " + __instance.maxHealth +
                               " to original " + originalHP + " (range: " + intendedMin + "-" + intendedMax + ")");

                    __instance.maxHealth = originalHP;
                    __instance.currentHealth = originalHP;
                }

                // Clear the stored values
                IntendedHP.intendedMinHP.set(__instance, -1);
                IntendedHP.intendedMaxHP.set(__instance, -1);
            }
        }
    }

    /**
     * Prefix patch on setHp(int) (single value version) to capture the intended HP.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "setHp", paramtypez = {int.class})
    public static class CaptureIntendedHPSingle {

        @SpirePrefixPatch
        public static void captureHP(AbstractMonster __instance, int hp) {
            if (__instance instanceof AbstractBGMonster && TogetherInSpireHelper.isMultiplayerActive()) {
                IntendedHP.intendedMinHP.set(__instance, hp);
                IntendedHP.intendedMaxHP.set(__instance, hp);
            }
        }
    }

    /**
     * Postfix patch on setHp(int) to restore the original HP value.
     */
    @SpirePatch2(clz = AbstractMonster.class, method = "setHp", paramtypez = {int.class})
    public static class RestoreIntendedHPSingle {

        @SpirePostfixPatch
        public static void restoreHP(AbstractMonster __instance) {
            if (!(__instance instanceof AbstractBGMonster) || !TogetherInSpireHelper.isMultiplayerActive()) {
                return;
            }

            int intendedHP = IntendedHP.intendedMinHP.get(__instance);

            if (intendedHP > 0 && __instance.maxHealth != intendedHP) {
                logger.debug("Restoring " + __instance.name + " HP from " + __instance.maxHealth +
                           " to original " + intendedHP);

                __instance.maxHealth = intendedHP;
                __instance.currentHealth = intendedHP;

                // Clear the stored values
                IntendedHP.intendedMinHP.set(__instance, -1);
                IntendedHP.intendedMaxHP.set(__instance, -1);
            }
        }
    }
}
