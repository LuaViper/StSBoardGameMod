//TODO: make sure this still interacts correctly with Weak/Vuln

package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGColorless.BGXCostChoice;
import BoardGame.cards.BGRed.BGWhirlwind;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGXCostCardAction extends AbstractGameAction {
    protected AbstractCard card;
    protected ArrayList<AbstractCard> choices;
    protected boolean choicesHaveBeenSetup=false;

    protected int minEnergy;
    protected int maxEnergy;
    public interface XCostAction{
        void execute(int energySpent);
    }

    protected XCostAction action;

    public BGXCostCardAction(AbstractCard card, int minEnergy, int maxEnergy, XCostAction action){
        this.minEnergy=minEnergy;
        //we have to check for confusion now -- by the time we get to action.update, it wears off
        //TODO: is confusion check still necessary after changes to costForTurn and minEnergy?
        AbstractPower p=AbstractDungeon.player.getPower("BGConfusion");
        if(p!=null && p.amount>-1){
            this.minEnergy=maxEnergy;
        }
        if(card instanceof AbstractBGCard){
            //TODO: there might be an edge case we haven't thought of where we're forced to play a copied card for free
            if(((AbstractBGCard)card).copiedCardEnergyOnUse!=-99)
                this.minEnergy=((AbstractBGCard)card).copiedCardEnergyOnUse;
        }
        this.duration = Settings.ACTION_DUR_XFAST;
        this.card=card;

        this.maxEnergy=maxEnergy;
        this.action=action;
    }

    public void update() {
        if(!choicesHaveBeenSetup){
            this.choices=new ArrayList<>();

            for(int i=minEnergy;i<=maxEnergy;i+=1){
                BGXCostChoice c=new BGXCostChoice(this.card,i,this.action);
                choices.add(c);
                if(card instanceof AbstractBGCard){
                    Logger logger = LogManager.getLogger("TEMP");
                    logger.info("set BGXCostChoice's copiedcard to "+((AbstractBGCard)this.card).copiedCard);
                    ((AbstractBGCard)c).copiedCard=((AbstractBGCard)this.card).copiedCard;
                }
            }
            choicesHaveBeenSetup=true;
        }


        if(this.choices.size()>1) {
            //TODO: if energy is high enough, player has to scroll to the right for more options.  does one of the BaseMod classes solve this?
            AbstractDungeon.cardRewardScreen.chooseOneOpen(this.choices);
            this.isDone=true;
            return;
        }else{
            //minenergy == card[0].cost == maxenergy
//            if(card instanceof AbstractBGCard){
//                if(((AbstractBGCard)card).copiedCard != null){
//                    ((AbstractBGCard)card).copiedCard.copiedCardEnergy=minEnergy;
//                }
//            }
            action.execute(minEnergy);
        }
        tickDuration();
        this.isDone=true;


    }


}
