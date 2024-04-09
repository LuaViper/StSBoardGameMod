package BoardGame.actions;

import BoardGame.powers.BGPoisonPower;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.PotionBounceEffect;

public class BGBouncingFlaskAction extends AbstractGameAction {
    private static final float DURATION = 0.01F;

    private static final float POST_ATTACK_WAIT_DUR = 0.1F;

    private int numTimes;

    private int amount;

    public BGBouncingFlaskAction(AbstractCreature target, int amount, int numTimes) {
        this.target = target;
        this.actionType = AbstractGameAction.ActionType.DEBUFF;
        this.duration = 0.01F;
        this.numTimes = numTimes;
        this.amount = amount;
    }

    public void update() {
        if (this.target == null) {
            this.isDone = true;
            return;
        }
        if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.clearPostCombatActions();
            this.isDone = true;
            return;
        }
        if (this.numTimes > 1 && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.numTimes--;
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                if(target!=null) {
                    addToTop(new BGBouncingFlaskAction((AbstractCreature)target, this.amount, this.numTimes));
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new PotionBounceEffect(this.target.hb.cX, this.target.hb.cY, target.hb.cX, target.hb.cY), 0.4F));
                }
            };
            addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose the next target for Bouncing Flask."));
        }
        if (this.target.currentHealth > 0) {
            addToTop((AbstractGameAction)new ApplyPowerAction(this.target, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGPoisonPower(this.target, (AbstractCreature)AbstractDungeon.player, this.amount), this.amount, true, AbstractGameAction.AttackEffect.POISON));
            addToTop((AbstractGameAction)new WaitAction(0.1F));
        }
        this.isDone = true;
    }
}


