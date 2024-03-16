package BoardGame.actions;

import BoardGame.powers.BGTrigger2DieAbilityPower;
import BoardGame.powers.BGTriggerAnyDieAbilityPower;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.relics.BGCharonsAshes;
import BoardGame.relics.DieControlledRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGActivateDieAbilityAction
        extends AbstractGameAction {
    private DamageInfo info;
    private DieControlledRelic relic = null;


    public BGActivateDieAbilityAction(DieControlledRelic relic) {
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGActivateDieAbilityAction: constructor");
        this.target=AbstractDungeon.player;
        this.relic=relic;
    }




    public void update() {
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGActivateDieAbilityAction: update");
        if (shouldCancelAction()) {
            this.isDone = true;

            return;
        }

        AbstractPower any=AbstractDungeon.player.getPower("BGTriggerAnyDieAbilityPower");
        AbstractPower two=AbstractDungeon.player.getPower("BGTrigger2DieAbilityPower");
        if(two!=null){
            if(relic instanceof NilrysCodexCompatible){
                ((BGTrigger2DieAbilityPower)two).doNotActivateOnRemove=true;
                addToTop((AbstractGameAction)new RemoveSpecificPowerAction(AbstractDungeon.player,AbstractDungeon.player, "BGTrigger2DieAbilityPower"));
                ((NilrysCodexCompatible) relic).Trigger2Ability();
                this.isDone=true;
                return;
            }
        }

        if(any!=null){
            ((BGTriggerAnyDieAbilityPower)any).doNotActivateOnRemove=true;
            addToTop((AbstractGameAction)new RemoveSpecificPowerAction(AbstractDungeon.player,AbstractDungeon.player, "BGTriggerAnyDieAbilityPower"));
            relic.activateDieAbility();
        }
        this.isDone=true;




    }




}


