package BoardGame.actions;

import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGGoForTheEyesConditionalWeakAction extends AbstractGameAction {

    private AbstractCreature p;
    private AbstractCreature m;
    public BGGoForTheEyesConditionalWeakAction(AbstractCreature p, AbstractCreature m) {
        this.p=p;
        this.m=m;
        this.actionType = ActionType.SPECIAL;
    }



    public void update() {
        if(TheDie.monsterRoll==4 || TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
            addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGWeakPower((AbstractCreature)m, 1, false), 1));

        this.isDone = true;
    }
}
