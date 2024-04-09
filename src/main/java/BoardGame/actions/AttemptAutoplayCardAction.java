package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;

public class AttemptAutoplayCardAction extends AbstractGameAction {
    public AbstractCard card;
    public AttemptAutoplayCardAction(AbstractCard card) {
        this.card=card;
    }

    public void update() {
        ArrayList<AbstractCard> cardsToBlock = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c == card) {
                if(card.cost>0){
                    if(EnergyPanel.getCurrentEnergy()>=card.cost){
                        card.applyPowers();
                        addToBot((AbstractGameAction) new LoseEnergyAction(card.cost));
                        addToTop((AbstractGameAction)new NewQueueCardAction(card, this.target, false, true));
                        addToTop((AbstractGameAction)new UnlimboAction(card));
                        if (!Settings.FAST_MODE) {
                            addToTop((AbstractGameAction)new WaitAction(Settings.ACTION_DUR_MED));
                        } else {
                            addToTop((AbstractGameAction)new WaitAction(Settings.ACTION_DUR_FASTER));
                        }
                    }
                }

            }
        }

        this.isDone = true;
    }

}
