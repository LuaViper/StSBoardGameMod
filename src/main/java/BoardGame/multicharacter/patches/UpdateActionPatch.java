package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import javax.naming.Context;

public class UpdateActionPatch {
    public static void before(AbstractGameAction a){
        if(CardCrawlGame.chosenCharacter!= BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
        if(ContextPatches.originalBGMultiCharacter==null)ContextPatches.originalBGMultiCharacter=AbstractDungeon.player;
        ContextPatches.pushContext(ActionPatches.Field.owner.get(a));
    }
    public static void after(AbstractGameAction a){
        if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
        ContextPatches.popContext();

    }
    @SpirePatch2(clz = GameActionManager.class, method = "update")
    public static class Foo {
        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractGameAction.class.getName()) && m.getMethodName().equals("update")) {
                        m.replace("{ "+UpdateActionPatch.class.getName()+".before(this.currentAction); $_ = $proceed($$); "+UpdateActionPatch.class.getName()+".after(this.currentAction); }");
                    }
                }
            };
        }
    }
}
