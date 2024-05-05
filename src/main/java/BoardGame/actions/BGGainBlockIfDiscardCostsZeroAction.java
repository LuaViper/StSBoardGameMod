package BoardGame.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGGainBlockIfDiscardCostsZeroAction extends AbstractGameAction {
    private int blockGain;

    public BGGainBlockIfDiscardCostsZeroAction(int amount) {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;
        this.blockGain = amount;
    }

    public void update() {
        if(!AbstractDungeon.player.discardPile.group.isEmpty()) {
            AbstractCard c = AbstractDungeon.player.discardPile.group.get(AbstractDungeon.player.discardPile.group.size() - 1);
            if (c.cost == 0)
                AbstractDungeon.actionManager.addToTop((AbstractGameAction) new GainBlockAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) AbstractDungeon.player, this.blockGain));
        }

        this.isDone = true;
    }
}
