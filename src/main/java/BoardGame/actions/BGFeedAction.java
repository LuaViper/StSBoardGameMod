package BoardGame.actions;


import BoardGame.monsters.bgbeyond.BGAwakenedOne;
import BoardGame.monsters.bgbeyond.BGDarkling;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;





public class BGFeedAction
        extends AbstractGameAction
{
    private int magicNumber;
    private DamageInfo info;
    private static final float DURATION = 0.1F;

    public BGFeedAction(AbstractCreature target, DamageInfo info, int strAmount) {
        this.info = info;
        setValues(target, info);
        this.magicNumber = strAmount;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.duration = 0.1F;
    }


    public void update() {
        if (this.duration == 0.1F &&
                this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AbstractGameAction.AttackEffect.NONE));
            this.target.damage(this.info);

            //TODO: SlimeBoss should use halfdead state instead of disappearing until the next turn
            boolean halfDeadCheckPassed=(!this.target.halfDead || this.target instanceof BGDarkling || this.target instanceof BGAwakenedOne);
            if ((((AbstractMonster)this.target).isDying || this.target.currentHealth <= 0) && halfDeadCheckPassed &&
                    !this.target.hasPower("Minion")) {
                //AbstractDungeon.player.increaseMaxHp(this.increaseHpAmount, false);
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new StrengthPower((AbstractCreature)AbstractDungeon.player, this.magicNumber), this.magicNumber));


//                if (this.target instanceof com.megacrit.cardcrawl.monsters.beyond.Donu) {
//                    UnlockTracker.unlockAchievement("DONUT");
//                }
            }

            if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }

            this.isDone=true;
        }


        tickDuration();
    }
}


