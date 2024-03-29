package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import BoardGame.multicharacter.DrawCardMultiAction;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DiscardPatch {
    @SpirePatch2(clz = DiscardAtEndOfTurnAction.class, method = "update")
    public static class DiscardCardPatch1{
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(DiscardAtEndOfTurnAction __instance) {
            if(AbstractDungeon.player instanceof BGMultiCharacter){
                if(ActionPatches.Field.owner.get(__instance)==null) {
                    //discard from bottom of screen to top (changes depending on currentHand)
                    //addToTop, so cycle backwards
                    int j=BGMultiCharacter.getSubcharacters().size()-1+BGMultiCharacter.handLayoutHelper.currentHand;
                    for(int i=j;i>=j-(BGMultiCharacter.getSubcharacters().size()-1);i-=1) {
                        AbstractPlayer p=BGMultiCharacter.getSubcharacters().get(i%BGMultiCharacter.getSubcharacters().size());
                        AbstractDungeon.actionManager.addToTop(ActionPatches.setOwnerFromConstructor(new DiscardAtEndOfTurnAction(),p));
                    }
                    __instance.isDone = true;
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

}
