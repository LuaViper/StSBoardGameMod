package BoardGame.actions;

import BoardGame.powers.BGWeakPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;


public class BGSashWhipAction extends AbstractGameAction {
    private AbstractMonster m;

    private int magicNumber;


    public BGSashWhipAction(AbstractMonster monster, int weakAmount) {
        this.m = monster;
        this.magicNumber = weakAmount;
    }

    public void update() {
        if (AbstractDungeon.player.stance.ID.equals("BGCalm")) {
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.m, (AbstractCreature) AbstractDungeon.player, (AbstractPower) new BGWeakPower((AbstractCreature) this.m, this.magicNumber, false), this.magicNumber));
        }
        this.isDone = true;
    }
}

