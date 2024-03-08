
package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGJudgmentAction
        extends AbstractGameAction {
    private int cutoff;

    public BGJudgmentAction(AbstractCreature target, int cutoff) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.source = null;
        this.target = target;
        this.cutoff = cutoff;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST &&
                this.target.currentHealth <= this.cutoff && this.target instanceof com.megacrit.cardcrawl.monsters.AbstractMonster)

            addToBot((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo((AbstractCreature) source, target.currentHealth, DamageInfo.DamageType.THORNS), AttackEffect.NONE));
        this.isDone = true;
    }
}



