package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.dungeons.AbstractBGDungeon;
import CoopBoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class UseCardPatch {

    public static void before(AbstractCard c) {
        if (!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
        ContextPatches.pushTargetContext(CardTargetingPatches.CardField.lastHoveredTarget.get(c));
        //REMINDER: copied cards (from Foreign Influence and Doppelganger) must manually set owner before playing
        if (CardCrawlGame.chosenCharacter != MultiCharacter.Enums.BG_MULTICHARACTER) return;
        ContextPatches.pushPlayerContext(CardPatches.Field.owner.get(c));
    }

    public static void after(AbstractCard c) {
        if (!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
        ContextPatches.popTargetContext();
        if (CardCrawlGame.chosenCharacter != MultiCharacter.Enums.BG_MULTICHARACTER) return;
        ContextPatches.popPlayerContext();
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class Foo {

        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (
                        m.getClassName().equals(AbstractCard.class.getName()) &&
                        m.getMethodName().equals("use")
                    ) {
                        m.replace(
                            "{ " +
                                UseCardPatch.class.getName() +
                                ".before(c); $_ = $proceed($$); " +
                                UseCardPatch.class.getName() +
                                ".after(c); }"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = CardRewardScreen.class, method = "update")
    public static class Foo2 {

        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (
                        m.getClassName().equals(AbstractCard.class.getName()) &&
                        m.getMethodName().equals("onChoseThisOption")
                    ) {
                        m.replace(
                            "{ " +
                                UseCardPatch.class.getName() +
                                ".before(this.touchCard); $_ = $proceed($$); " +
                                UseCardPatch.class.getName() +
                                ".after(this.touchCard); }"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = CardRewardScreen.class, method = "cardSelectUpdate")
    public static class Foo3 {

        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (
                        m.getClassName().equals(AbstractCard.class.getName()) &&
                        m.getMethodName().equals("onChoseThisOption")
                    ) {
                        m.replace(
                            "{ " +
                                UseCardPatch.class.getName() +
                                ".before(hoveredCard); $_ = $proceed($$); " +
                                UseCardPatch.class.getName() +
                                ".after(hoveredCard); }"
                        );
                    }
                }
            };
        }
    }
}
