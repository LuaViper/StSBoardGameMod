package CoopBoardGame.patches;

import CoopBoardGame.ui.RelicDragManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/**
 * Patches for relic drag-and-drop functionality.
 * Hooks into AbstractRelic.update() to detect when user starts dragging a relic.
 */
public class RelicDragPatches {

    /**
     * Patch AbstractRelic.update() to detect drag initiation.
     */
    @SpirePatch2(clz = AbstractRelic.class, method = "update")
    public static class RelicUpdateDragPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic __instance) {
            // Only handle relics in player's relic list (not shop/reward relics)
            if (AbstractDungeon.player == null ||
                !AbstractDungeon.player.relics.contains(__instance)) {
                return;
            }

            // Check if dragging is allowed in current game state
            if (!RelicDragManager.canDragRelics()) return;

            // Delegate to manager for drag detection
            int index = AbstractDungeon.player.relics.indexOf(__instance);
            RelicDragManager.checkDragStart(__instance, index);
        }
    }
}
