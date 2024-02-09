package BoardGame.actions;


import BoardGame.powers.WeakVulnCancel;
import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGDamagePerAttackPlayedAction extends AbstractGameAction {
    private DamageInfo info;

    public BGDamagePerAttackPlayedAction(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        this.info = info;
        setValues(target, info);
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = effect;
    }

    public BGDamagePerAttackPlayedAction(AbstractCreature target, DamageInfo info) {
        this(target, info, AbstractGameAction.AttackEffect.NONE);
    }

    public void update() {
        this.isDone = true;
        if (this.target != null && this.target.currentHealth > 0) {
            int count = 0;
            for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
                if (c.type == AbstractCard.CardType.ATTACK)
                    count++;
            }
            BGShivs relic = (BGShivs) AbstractDungeon.player.getRelic("BoardGame:BGShivs");
            if (relic != null) {
                count += relic.shivsPlayedThisTurn;
            }
            count--;
            if(count>0) {
                for (int i = 0; i < count; i++)
                    addToTop((AbstractGameAction) new DamageAction(this.target, this.info, this.attackEffect));
            }else{
                this.info.type = WeakVulnCancel.WEAKVULN_ZEROHITS;
                this.info.base=0;
                this.info.output=0;
                addToTop((AbstractGameAction)new DamageAction(this.target,this.info,AbstractGameAction.AttackEffect.NONE));
            }
        }
    }
}



