package BoardGame.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

public interface CardDisappearsOnExhaust {

    @SpirePatch(clz = CardGroup.class, method = "moveToExhaustPile",
            paramtypez = {AbstractCard.class})
    public static class CardGroupExhaustPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> moveToExhaustPile(CardGroup __instance, AbstractCard c){
            if(c instanceof CardDisappearsOnExhaust){
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onExhaust(c);
                }
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    p.onExhaust(c);
                }
                c.triggerOnExhaust();
                //CardGroup.resetCardBeforeMoving(c);  private function -- recreated below
                {
                    if (AbstractDungeon.player.hoveredCard == c) {
                        AbstractDungeon.player.releaseCard();
                    }
                    AbstractDungeon.actionManager.removeFromQueue(c);
                    c.unhover();
                    c.untip();
                    c.stopGlowing();
                    __instance.group.remove(c);
                }
                AbstractDungeon.effectList.add(new ExhaustCardEffect(c));
                //AbstractDungeon.player.exhaustPile.addToTop(c);
                AbstractDungeon.player.onCardDrawOrDiscard();
                return SpireReturn.Return();
            }


            return SpireReturn.Continue();
        }

    }


}
