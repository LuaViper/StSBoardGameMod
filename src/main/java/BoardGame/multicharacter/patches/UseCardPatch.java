package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class UseCardPatch {
    public static void before(AbstractCard c){
        if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
        ContextPatches.originalBGMultiCharacter=AbstractDungeon.player;
        //REMINDER: copied cards (from Foreign Influence and Doppelganger) must manually set owner before playing
        if(CardPatches.Field.owner.get(c)==null)return;
        AbstractDungeon.player=CardPatches.Field.owner.get(c);
        ContextPatches.currentGlobalContext=CardPatches.Field.owner.get(c);
    }
    public static void after(AbstractCard c){
        if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
        AbstractDungeon.player=ContextPatches.originalBGMultiCharacter;
        ContextPatches.currentGlobalContext=null;
    }
    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class Foo {
        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace("{ "+UseCardPatch.class.getName()+".before(c); $_ = $proceed($$); "+UseCardPatch.class.getName()+".after(c); }");
                    }
                }
            };
        }
    }
}


