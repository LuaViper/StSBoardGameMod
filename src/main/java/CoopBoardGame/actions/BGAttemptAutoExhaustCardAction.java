package CoopBoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

// Namely used for void status card
public class BGAttemptAutoExhaustCardAction extends AbstractGameAction {

    public AbstractCard card;
    private float duration;

    public BGAttemptAutoExhaustCardAction(AbstractCard card) {
        this.card = card;
        this.duration = Settings.FAST_MODE ? Settings.ACTION_DUR_FASTER : Settings.ACTION_DUR_MED;
    }

    public void update() {
        // Don't exhaust card if we can't spend the energy to do so
        if (EnergyPanel.getCurrentEnergy() < this.card.cost) {
            return;
        }

        // Spend the energy and exhaust the card
        addToBot(new LoseEnergyAction(this.card.cost));
        addToTop(new ExhaustSpecificCardAction(this.card, AbstractDungeon.player.hand));
        addToTop(new WaitAction(this.duration));

        this.isDone = true;
    }
}
