package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.MultiCharacter;
import CoopBoardGame.multicharacter.grid.GridBackground;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * Patches to make TopPanel show only the active character's relics and potions in grid mode.
 */
public class TopPanelPatches {

    /**
     * Helper method to get the active character in grid mode.
     * Returns null if not in grid mode or only one character.
     */
    private static AbstractPlayer getActiveCharacterForTopPanel() {
        if (!(AbstractDungeon.player instanceof MultiCharacter)) return null;
        if (!GridBackground.isGridViewActive()) return null;
        if (MultiCharacter.getSubcharacters().size() <= 1) return null;

        int activeRow = MultiCharacter.handLayoutHelper.currentHand;
        if (activeRow >= 0 && activeRow < MultiCharacter.getSubcharacters().size()) {
            return MultiCharacter.getSubcharacters().get(activeRow);
        }
        return null;
    }

    /**
     * Patch to render only active character's relics in grid mode.
     */
    @SpirePatch2(clz = TopPanel.class, method = "renderRelics", paramtypez = { SpriteBatch.class })
    public static class RenderRelicsPatch {

        @SpirePrefixPatch
        public static void Prefix(TopPanel __instance, SpriteBatch sb) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.pushPlayerContext(activeChar);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.popPlayerContext();
            }
        }
    }

    /**
     * Patch to update only active character's relics in grid mode.
     */
    @SpirePatch2(clz = TopPanel.class, method = "updateRelics")
    public static class UpdateRelicsPatch {

        @SpirePrefixPatch
        public static void Prefix(TopPanel __instance) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.pushPlayerContext(activeChar);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.popPlayerContext();
            }
        }
    }

    /**
     * Patch to render only active character's potions in grid mode.
     */
    @SpirePatch2(clz = TopPanel.class, method = "renderPotions", paramtypez = { SpriteBatch.class })
    public static class RenderPotionsPatch {

        @SpirePrefixPatch
        public static void Prefix(TopPanel __instance, SpriteBatch sb) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.pushPlayerContext(activeChar);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.popPlayerContext();
            }
        }
    }

    /**
     * Patch to update only active character's potions in grid mode.
     */
    @SpirePatch2(clz = TopPanel.class, method = "updatePotions")
    public static class UpdatePotionsPatch {

        @SpirePrefixPatch
        public static void Prefix(TopPanel __instance) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.pushPlayerContext(activeChar);
            }
        }

        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance) {
            AbstractPlayer activeChar = getActiveCharacterForTopPanel();
            if (activeChar != null) {
                ContextPatches.popPlayerContext();
            }
        }
    }
}
