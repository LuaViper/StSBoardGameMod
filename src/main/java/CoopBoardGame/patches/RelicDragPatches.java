package CoopBoardGame.patches;

import CoopBoardGame.ui.RelicDragManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

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

    /**
     * Patch TopPanel.update() to manage drag state during drag operation.
     */
    @SpirePatch2(clz = TopPanel.class, method = "update")
    public static class TopPanelDragUpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance) {
            if (!RelicDragManager.isDragging()) {
                // Call global update even when not dragging to reset click state
                RelicDragManager.globalUpdate();
                return;
            }

            // Update drag position and hover index
            RelicDragManager.updateDrag();

            // Check for arrow hover (scrolling while dragging)
            RelicDragManager.checkArrowHover(__instance);

            // Check for drop (mouse released)
            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                RelicDragManager.endDrag();
            }
        }
    }

    /**
     * Patch AbstractRelic.renderInTopPanel() to apply position offsets for gap animation
     * and skip rendering the dragged relic in its normal position.
     */
    @SpirePatch2(clz = AbstractRelic.class, method = "renderInTopPanel",
                 paramtypez = {SpriteBatch.class})
    public static class RelicRenderDragPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractRelic __instance, SpriteBatch sb) {
            if (!RelicDragManager.isDragging()) return SpireReturn.Continue();

            // Skip the dragged relic entirely (rendered separately at cursor)
            if (__instance == RelicDragManager.draggedRelic) {
                return SpireReturn.Return();
            }

            // Apply offset to other relics for gap animation
            int index = AbstractDungeon.player.relics.indexOf(__instance);
            if (index >= 0) {
                float offset = RelicDragManager.getRelicOffset(index);
                __instance.currentX += offset;
            }

            return SpireReturn.Continue();
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractRelic __instance, SpriteBatch sb) {
            if (!RelicDragManager.isDragging()) return;
            if (__instance == RelicDragManager.draggedRelic) return;

            // Restore original position after rendering
            int index = AbstractDungeon.player.relics.indexOf(__instance);
            if (index >= 0) {
                float offset = RelicDragManager.getRelicOffset(index);
                __instance.currentX -= offset;
            }
        }
    }

    /**
     * Patch TopPanel.renderRelics() to render the dragged relic at cursor position.
     */
    @SpirePatch2(clz = TopPanel.class, method = "renderRelics",
                 paramtypez = {SpriteBatch.class})
    public static class TopPanelRenderRelicsPatch {
        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            // Render dragged relic at cursor position (on top of everything)
            if (RelicDragManager.isDragging()) {
                RelicDragManager.renderDraggedRelic(sb);
            }
        }
    }
}
