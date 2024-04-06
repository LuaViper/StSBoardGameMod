
package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGJudgmentAction
        extends AbstractGameAction {
    private int cutoff;

    public BGJudgmentAction(AbstractCreature target, int cutoff) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.source = AbstractDungeon.player;
        this.target = target;
        this.cutoff = cutoff;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST &&
                this.target.currentHealth <= this.cutoff && this.target instanceof com.megacrit.cardcrawl.monsters.AbstractMonster) {
            //addToBot((AbstractGameAction)new RemoveAllBlockAction((AbstractCreature)target, (AbstractCreature)target));
            addToBot((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo(source, target.currentHealth, DamageInfo.DamageType.HP_LOSS), AttackEffect.NONE));
        }
        this.isDone = true;
    }
}



