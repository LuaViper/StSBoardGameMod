package BoardGame.multicharacter.patches;

import BoardGame.BoardGame;
import BoardGame.cards.AbstractBGCard;
import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import javax.naming.Context;

public class ActionConstructorPatch {
    @SpirePatch2(clz = AbstractGameAction.class,method = SpirePatch.CONSTRUCTOR)
    public static class Patch1 {
        @SpirePrefixPatch
        public static void Prefix(AbstractGameAction __instance) {
            if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
            if(ContextPatches.currentGlobalContext==null)return;
            ActionPatches.Field.owner.set(__instance,ContextPatches.currentGlobalContext);
        }
    }
}
