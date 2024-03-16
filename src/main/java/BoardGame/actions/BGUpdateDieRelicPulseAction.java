package BoardGame.actions;

import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.relics.BGCharonsAshes;
import BoardGame.relics.DieControlledRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGUpdateDieRelicPulseAction extends AbstractGameAction {

    public BGUpdateDieRelicPulseAction() {
        this.target=AbstractDungeon.player;
    }


    public void update() {
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGActivateDieAbilityAction: update");
        if (shouldCancelAction()) {
            this.isDone = true;

            return;
        }

        AbstractPower any=(AbstractDungeon.player.getPower("BGTriggerAnyDiePower"));
        AbstractPower two=(AbstractDungeon.player.getPower("BGTrigger2DiePower"));
        for(AbstractRelic relic : AbstractDungeon.player.relics){
            if(relic instanceof DieControlledRelic){
                if(any!=null && any.amount>0){
                    relic.beginLongPulse();
                }else{
                    relic.stopPulse();
                }
            }
            if(relic instanceof NilrysCodexCompatible){
                if(two!=null && two.amount>0){
                    relic.beginLongPulse();
                }else{
                    relic.stopPulse();
                }
            }

        }
        this.isDone = true;


    }
}
