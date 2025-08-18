package BoardGame.patches;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static BoardGame.characters.BGCurse.Enums.BG_CURSE;


// cards in animation appear to be chosen by AbstractDungeon.returnTrulyRandomCardFromAvailable(this.upgradePreviewCard).makeCopy();
public class TransformPatch {


    public static CardGroup getTransformableCards(){
        CardGroup purgeable = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                .getPurgeableCards());
        CardGroup retVal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        for (AbstractCard c : purgeable.group) {
            if (c.color!=BG_CURSE)
                retVal.group.add(c);
        }
        return retVal;
    }




    @SpirePatch2(clz=CardGroup.class,method="getPurgeableCards")
    public static class BGAscendersBanePurgePatch {
        @SpirePostfixPatch
        public static CardGroup Postfix(CardGroup __result){
            CardGroup retVal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : __result.group) {
                //other nonpurgeable cards have already been filtered
                if (!c.cardID.equals("BGAscendersBane"))
                    retVal.group.add(c);
            }
            return retVal;
        }
    }


    //note that returnTrulyRandomCardFromAvailable is also used to determine the actual result of a vanilla transform,
    // but our patched transform (in AbstractBGDungeon) uses DrawFromRewardDeck instead.
    @SpirePatch2(clz = AbstractDungeon.class, method = "returnTrulyRandomCardFromAvailable",
            paramtypez = {AbstractCard.class, Random.class})
    public static class TransformAnimationPatch {
        @SpirePrefixPatch
        private static SpireReturn<AbstractCard> Foo(AbstractCard prohibited, Random rng) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                ArrayList<AbstractCard> list = new ArrayList();
                Iterator var3;
                AbstractCard c;
                var3 = AbstractBGDungeon.rewardDeck.group.iterator();
                while(var3.hasNext()) {
                    c = (AbstractCard)var3.next();
                    if (!Objects.equals(c.cardID, prohibited.cardID)) {
                        list.add(c);
                    }
                }
                var3 = AbstractBGDungeon.rareRewardDeck.group.iterator();
                while(var3.hasNext()) {
                    c = (AbstractCard)var3.next();
                    if (!Objects.equals(c.cardID, prohibited.cardID)) {
                        list.add(c);
                    }
                }
                return SpireReturn.Return((list.get(rng.random(list.size() - 1))).makeCopy());
            }
            return SpireReturn.Continue();
        }
    }

}
