//TODO: see if we can reuse BGXCostCardAction without cloning it outright

package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGColorless.BGXCostChoice;
import BoardGame.cards.BGGreen.BGDoppelganger;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGDoppelgangerAction extends BGXCostCardAction {

    public BGDoppelgangerAction(AbstractCard card, int minEnergy, int maxEnergy, BGXCostCardAction.XCostAction action) {
        super(card, minEnergy, maxEnergy, action);
    }

    public void update() {
        if(!choicesHaveBeenSetup) {
            this.choices = new ArrayList<>();

            for (int i = minEnergy; i <= maxEnergy; i += 1) {
                for (int j = BGDoppelganger.cardsPlayedThisTurn.size() - 1; j >= 0; j -= 1) {
                    AbstractCard d = BGDoppelganger.cardsPlayedThisTurn.get(j);
                    //TODO: "are we allowed to copy this card" check
                    if (d.type == AbstractCard.CardType.ATTACK || d.type == AbstractCard.CardType.SKILL) {
                        if (d.cost >= 0) {
                            //TODO: this line almost certainly misses some edge cases
                            if (d.cost == i) {
                                XCostAction action=(e)->{
                                    addToTop((AbstractGameAction)new BGCopyAndPlayCardAction(d, d.cost));
                                };
                                BGXCostChoice c = new BGXCostChoice(d, i, action, d);
                                choices.add(c);
                                if (card instanceof AbstractBGCard) {
                                    //Logger logger = LogManager.getLogger("TEMP");
                                    //logger.info("set BGDoppelganger's copiedcard to " + ((AbstractBGCard) this.card).copiedCard);
                                    //TODO: we've completely forgotten how copiedCard works at this point, should this be c.copied or d.copied?
                                    ((AbstractBGCard) c).copiedCard = ((AbstractBGCard) this.card).copiedCard;
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
            ((BGXCostChoice)d).action.execute(d.cost);
        }else{
            //we probably got here via force-played Doppelganger uncopyable card.
            //TODO: discard Doppelganger instead of exhausting it
            this.isDone=true;
        }
        tickDuration();
        this.isDone=true;


    }


}
