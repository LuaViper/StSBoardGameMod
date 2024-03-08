
package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;


public class BGFearNoEvilAction extends AbstractGameAction {
    private AbstractMonster m;

    private DamageInfo info;

    public BGFearNoEvilAction(AbstractMonster m, DamageInfo info) {
        this.m = m;
        this.info = info;

    }

    public void update() {
        if (AbstractDungeon.player.stance.ID.equals("BGWrath"))
            addToTop(new ChangeStanceAction("BGCalm"));
        addToTop((AbstractGameAction)new DamageAction((AbstractCreature)this.m, this.info, AbstractGameAction.AttackEffect.SLASH_HEAVY));
        this.isDone = true;
    }
}
