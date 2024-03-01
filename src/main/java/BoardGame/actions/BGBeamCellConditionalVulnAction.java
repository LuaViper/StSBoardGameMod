package BoardGame.actions;

import BoardGame.powers.BGVulnerablePower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGBeamCellConditionalVulnAction extends AbstractGameAction {

    private AbstractCreature p;
    private AbstractCreature m;
    public BGBeamCellConditionalVulnAction(AbstractCreature p, AbstractCreature m) {
        this.p=p;
        this.m=m;
        this.actionType = ActionType.SPECIAL;
    }



    public void update() {
        if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2 || TheDie.monsterRoll==3)
            addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGVulnerablePower((AbstractCreature)m, 1, false), 1));

        this.isDone = true;
    }
}
