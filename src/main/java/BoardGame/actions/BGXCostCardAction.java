//TODO: make sure this still interacts correctly with Weak/Vuln

package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGColorless.BGXCostChoice;
import BoardGame.powers.BGCorruptionPower;
import BoardGame.powers.BGFreeAttackPower;
import BoardGame.powers.BGFreeCardPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGXCostCardAction extends AbstractGameAction {
    protected AbstractCard card;
    protected ArrayList<AbstractCard> choices;
    protected boolean choicesHaveBeenSetup=false;


    public static class XCostInfo{
        public int minEnergy=0;
        public int maxEnergy=0;
        public int exactEnergyCost=-99;
        public boolean dontExpendResources=false;
    }

    public static XCostInfo preProcessCard(AbstractBGCard c){
        XCostInfo info=new XCostInfo();
        info.maxEnergy=c.energyOnUse;
        if(c.isCostModifiedForTurn){
            info.minEnergy=c.costForTurn;
            c.energyOnUse=c.costForTurn;
            info.exactEnergyCost=info.minEnergy;
        }
        //if(this.freeToPlay()){    //can't do this because of built-in mod shenanigans (freeToPlayOnce is true when player has 0 energy)
        if(c.isInAutoplay || BGFreeCardPower.isActive()
        || (BGFreeAttackPower.isActive() && c.type==AbstractCard.CardType.ATTACK)
        || (BGCorruptionPower.isActive() && c.type==AbstractCard.CardType.SKILL)){
            c.energyOnUse=0;
            info.exactEnergyCost=0;
            info.dontExpendResources=true;
        }
        if(c.ignoreEnergyOnUse){
            c.energyOnUse=0;
            info.exactEnergyCost=0;
            info.dontExpendResources=true;
        }
        if(c.copiedCardEnergyOnUse!=-99){
            c.energyOnUse=c.copiedCardEnergyOnUse;
            info.exactEnergyCost=c.copiedCardEnergyOnUse;
            info.dontExpendResources=true;
        }
        return info;
    }

    XCostInfo info;

    public interface XCostAction{
        void execute(int energySpent, boolean dontExpendResources);
    }

    protected XCostAction action;

    public BGXCostCardAction(AbstractCard card, XCostInfo info, XCostAction action){
        this.info=info;
        //we have to check for confusion now -- by the time we get to action.update, it wears off
        //TODO: is confusion check still necessary after changes to costForTurn and minEnergy?
        AbstractPower p=AbstractDungeon.player.getPower("BGConfusion");
        if(p!=null && p.amount>-1){
            //exactEnergyCost=maxEnergy;
            info.exactEnergyCost=p.amount;
            info.minEnergy=info.exactEnergyCost;
            info.dontExpendResources=false;
        }
        if(card instanceof AbstractBGCard){
            //TODO: there might be an edge case we haven't thought of where we're forced to play a copied card for free
            if(((AbstractBGCard)card).copiedCardEnergyOnUse!=-99) {
                info.exactEnergyCost=((AbstractBGCard) card).copiedCardEnergyOnUse;
                info.minEnergy = info.exactEnergyCost;
                info.maxEnergy=info.exactEnergyCost;
                info.dontExpendResources=true;
            }
        }
        this.duration = Settings.ACTION_DUR_XFAST;
        this.card=card;

        this.action=action;
    }

    public void update() {
        if(!choicesHaveBeenSetup){
            this.choices=new ArrayList<>();

            int effectiveMaxEnergy=info.maxEnergy;
            if(info.exactEnergyCost<0){
                AbstractRelic relic=AbstractDungeon.player.getRelic("BoardGame:BGMiracles");
                if(relic!=null){
                    effectiveMaxEnergy+=relic.counter;
                }
            }else{
                info.minEnergy=info.exactEnergyCost;
                info.maxEnergy=info.exactEnergyCost;
                effectiveMaxEnergy=info.exactEnergyCost;
            }
            for(int i=info.minEnergy;i<=effectiveMaxEnergy;i+=1){
                BGXCostChoice c=new BGXCostChoice(this.card,i,info.dontExpendResources,this.action);
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
//            ModalChoiceBuilder mcb = new ModalChoiceBuilder();
//            for(AbstractCard c : choices) {
//                    //mcb.addOption((BGXCostChoice)c);
//                mcb.addOption(c);
//            }
//            ModalChoice modal=mcb.create();
//            modal.open();


            this.isDone=true;
            return;
        }else{
            action.execute(info.minEnergy, info.dontExpendResources);
        }
        tickDuration();
        this.isDone=true;


    }


}
