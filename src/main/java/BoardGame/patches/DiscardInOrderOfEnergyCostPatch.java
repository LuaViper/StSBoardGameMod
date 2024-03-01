package BoardGame.patches;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.powers.BGAfterImagePower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Comparator;


//TODO: this is NOT compatible with GamblingChipAction or similar effects -- should it be? (that's a lot of work but right now we are manually re-sorting in each BG card that doesn't use DiscardAction)
//TODO: determine whether we want to apply this to randomly-chosen cards too (does that even happen in BG?)

public class DiscardInOrderOfEnergyCostPatch {

    public static class CustomCostComparatorAscending implements Comparator<AbstractCard> {
        public int compare(AbstractCard card1, AbstractCard card2) {
            int cost1=card1.costForTurn;
            int cost2=card2.costForTurn;
            if(cost1<0)cost1=10-cost1;
            if(cost2<0)cost2=10-cost2;
            return cost1-cost2;
        }
    }
    public static class CustomCostComparatorDescending implements Comparator<AbstractCard> {
        public int compare(AbstractCard card1, AbstractCard card2) {
            int cost1=card1.costForTurn;
            int cost2=card2.costForTurn;
            if(cost1<0)cost1=10-cost1;
            if(cost2<0)cost2=10-cost2;
            return cost2-cost1;
        }
    }

    public static void sortByCost(CardGroup cards, boolean ascending){
        if(ascending)
            cards.group.sort(new CustomCostComparatorAscending());
        else
            cards.group.sort(new CustomCostComparatorDescending());
    }

    @SpirePatch2(clz= DiscardAction.class,method="update",paramtypez={})
    public static class DiscardActionUpdatePatch {
        @SpireInsertPatch(
                locator = DiscardActionUpdatePatch.Locator.class,
                localvars = {}
        )
        //Case 1: hand size is less than or equal to amount to be discarded (discard entire hand)
        public static void Insert(AbstractPlayer ___p) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
                return;
            }
            DiscardInOrderOfEnergyCostPatch.sortByCost(___p.hand,true);
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(DiscardAction.class,"amount");
                return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[1]};
            }
        }
    }

    @SpirePatch2(clz= DiscardAction.class,method="update",paramtypez={})
    public static class DiscardActionUpdatePatch2 {
        @SpireInsertPatch(
                locator = DiscardActionUpdatePatch2.Locator.class,
                localvars = {}
        )
        //Case 2: player selects cards to be discarded via handCardSelectScreen
        public static void Insert(AbstractPlayer ___p) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
                return;
            }
            //due to the way in which individual cards are discarded, sort in opposite order
            DiscardInOrderOfEnergyCostPatch.sortByCost(AbstractDungeon.handCardSelectScreen.selectedCards,false);
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(HandCardSelectScreen.class,"selectedCards");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}
