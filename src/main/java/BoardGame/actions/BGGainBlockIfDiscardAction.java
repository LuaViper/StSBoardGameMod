package BoardGame.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGGainBlockIfDiscardAction extends AbstractGameAction {
    private int blockGain;

    public BGGainBlockIfDiscardAction(int amount) {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;
        this.blockGain = amount;
    }

    public void update() {
        if (GameActionManager.totalDiscardedThisTurn > 0) {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, this.blockGain));
        }
        this.isDone = true;
    }
}
