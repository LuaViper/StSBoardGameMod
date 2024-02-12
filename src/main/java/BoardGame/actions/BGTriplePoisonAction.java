package BoardGame.actions;

import BoardGame.powers.BGPoisonPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGTriplePoisonAction extends AbstractGameAction {
    public BGTriplePoisonAction(AbstractCreature target, AbstractCreature source) {
        this.target = target;
        this.source = source;
        this.actionType = ActionType.DEBUFF;
        this.attackEffect = AttackEffect.FIRE;
    }

    public void update() {
        if (this.target != null && this.target.hasPower("BGPoison"))
            addToTop((AbstractGameAction) new ApplyPowerAction(this.target, this.source, (AbstractPower) new BGPoisonPower(this.target, this.source,

                    (this.target.getPower("BGPoison")).amount*2),
                    (this.target.getPower("BGPoison")).amount*2));
        this.isDone = true;
    }
}