package BoardGame.actions;

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
    private AbstractCard card;
    private ArrayList<AbstractCard> choices;
    private boolean choicesHaveBeenSetup=false;

    private int minEnergy;
    private int maxEnergy;
    public interface XCostAction{
        void execute(int energySpent);
    }

    private XCostAction action;

    public BGXCostCardAction(AbstractCard card, int maxEnergy, XCostAction action){
        this.minEnergy=0;
        //we have to check for confusion now -- by the time we get to action.update, it wears off
        AbstractPower p=AbstractDungeon.player.getPower("BGConfusion");
        if(p!=null && p.amount>-1){
            this.minEnergy=maxEnergy;
        }
        this.duration = Settings.ACTION_DUR_XFAST;
        this.card=card;

        this.maxEnergy=maxEnergy;
        this.action=action;
    }

    public void update() {
        if(!choicesHaveBeenSetup){
            this.choices=new ArrayList<>();
            //Logger logger = LogManager.getLogger("TEMP");
            //logger.info("BGXCostCardAction.update");

            for(int i=minEnergy;i<=maxEnergy;i+=1){
                BGXCostChoice card=new BGXCostChoice(this.card,i,this.action);
                choices.add(card);
            }
            choicesHaveBeenSetup=true;
        }


        if(this.choices.size()>1) {
            //TODO: if energy is high enough, player has to scroll to the right for more options.  does one of the BaseMod classes solve this?
            AbstractDungeon.cardRewardScreen.chooseOneOpen(this.choices);
            this.isDone=true;
            return;
        }else{
            action.execute(minEnergy);      //minenergy == card[0].cost == maxenergy
        }
        tickDuration();
        this.isDone=true;


    }


}
