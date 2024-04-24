//TODO: see if we can reuse BGXCostCardAction without cloning it outright

//TODO: BGDoppelgangerAction does not yet support Miracles

package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGColorless.BGXCostChoice;
import BoardGame.cards.BGGreen.BGDoppelganger;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

//TODO: when Doppelganger is copied via Burst, and a targeted card is chosen, the targeting screen pops up for both cards before the first one resolves

public class BGDoppelgangerAction extends BGXCostCardAction {

    XCostInfo info;
    public BGDoppelgangerAction(AbstractCard card, XCostInfo info, BGXCostCardAction.XCostAction action) {
        super(card, info, action);
        this.info=info;
    }

    public void update() {
        if(!choicesHaveBeenSetup) {
            this.choices = new ArrayList<>();

            int effectiveMaxEnergy=info.maxEnergy;
            if(info.exactEnergyCost<0){
                AbstractRelic relic=AbstractDungeon.player.getRelic("BoardGame:BGMiracles");
                if(relic!=null){
                    effectiveMaxEnergy+=relic.counter;
                }
            }else{
                info.minEnergy=info.exactEnergyCost;
                effectiveMaxEnergy=info.exactEnergyCost;
            }
            for (int i = info.minEnergy; i <= effectiveMaxEnergy; i += 1) {
                for (int j = BGDoppelganger.cardsPlayedThisTurn.size() - 1; j >= 0; j -= 1) {
                    AbstractCard d = BGDoppelganger.cardsPlayedThisTurn.get(j);
                    //TODO: "are we allowed to copy this card" check - reminder: copies can't be copied
                    if (d.type == AbstractCard.CardType.ATTACK || d.type == AbstractCard.CardType.SKILL) {
                        if (d.cost >= 0) {
                            //TODO: this line almost certainly misses some edge cases
                            if (d.cost == i) {
                                XCostAction action=(e,dont)->{
                                    addToTop((AbstractGameAction)new BGCopyAndPlayCardAction(d, d.cost, dont));
                                };
                                BGXCostChoice c = new BGXCostChoice(originalXCostCard, d.cost, info.dontExpendResources, action,d);
                                choices.add(c);
                                if (originalXCostCard instanceof AbstractBGCard) {
                                    //Logger logger = LogManager.getLogger("TEMP");
                                    //logger.info("set BGDoppelganger's copiedcard to " + ((AbstractBGCard) this.card).copiedCard);
                                    //TODO: we've completely forgotten how copiedCard works at this point, should this be c.copied or d.copied?
                                    ((AbstractBGCard) c).copiedCard = ((AbstractBGCard) this.originalXCostCard).copiedCard;
                                    break;
                                }
                            }
                        }
                    }
                }
                choicesHaveBeenSetup = true;
            }
        }

        if(this.choices.size()>1) {
            //TODO: if energy is high enough, player has to scroll to the right for more options.  does one of the BaseMod classes solve this?
            AbstractDungeon.cardRewardScreen.chooseOneOpen(this.choices);
            this.isDone=true;
            return;
        }else if(this.choices.size()==1) {
            AbstractCard d=this.choices.get(0);
            ((BGXCostChoice)d).action.execute(d.cost, info.dontExpendResources);
        }else{
            //we probably got here via force-played Doppelganger uncopyable card.
            //TODO: Doppelganger was Unplayable, so discard it instead of exhausting it!  (unless Havoc is involved)
            this.isDone=true;
        }
        tickDuration();
        this.isDone=true;


    }


}
