package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Patches to prevent TogetherInSpire from scaling monster health in board game mode.
 *
 * In standard TogetherInSpire multiplayer, monster health is multiplied by the number
 * of players to increase difficulty. However, in board game mode, we spawn separate
 * enemy groups for each player row, so the health scaling is not needed.
 *
 * This patch stores the original maxHealth value before init() and restores it
 * after init() if we're in board game multiplayer mode.
 */
public class MonsterHealthScalingPatch {

    private static final Logger logger = LogManager.getLogger(MonsterHealthScalingPatch.class.getName());

    // Thread-local storage for original health values during monster init
    // This is needed because init() can be called recursively for minions
    private static final ThreadLocal<Integer> originalMaxHealth = new ThreadLocal<>();

    /**
     * Capture the original maxHealth before init() is called.
     * TogetherInSpire's health scaling happens during or after init().
     */
    @SpirePatch2(
        clz = AbstractMonster.class,
        method = "init"
    )
    public static class PreInitPatch {

        @SpirePrefixPatch
        public static void Prefix(AbstractMonster __instance) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                // Store the original maxHealth before any scaling
                originalMaxHealth.set(__instance.maxHealth);
            }
        }
    }

    /**
     * Restore the original maxHealth after init() completes.
     * This effectively undoes any health scaling that TogetherInSpire applied.
     */
    @SpirePatch2(
        clz = AbstractMonster.class,
        method = "init"
    )
    public static class PostInitPatch {

        @SpirePostfixPatch
        public static void Postfix(AbstractMonster __instance) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                Integer original = originalMaxHealth.get();
                if (original != null && __instance.maxHealth != original) {
                    logger.debug("Reverting health scaling for " + __instance.name +
                        ": " + __instance.maxHealth + " -> " + original);
                    __instance.maxHealth = original;
                    __instance.currentHealth = Math.min(__instance.currentHealth, original);
                }
                originalMaxHealth.remove();
            }
        }
    }

}
