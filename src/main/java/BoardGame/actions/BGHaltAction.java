
package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGHaltAction
        extends AbstractGameAction {
    int additionalAmt;

    public BGHaltAction(AbstractCreature target, int block, int additional) {
        this.target = target;
        this.amount = block;
        this.additionalAmt = additional;
    }

    public void update() {
        if (AbstractDungeon.player.stance.ID.equals("BGWrath")) {
            addToTop((AbstractGameAction)new GainBlockAction(this.target, this.additionalAmt));
        }
        addToTop((AbstractGameAction)new GainBlockAction(this.target, this.amount));
        this.isDone = true;
    }
}