package BoardGame.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public interface AfterCompletelyResolveCardPower {

    public abstract void onAfterCompletelyResolveCard(AbstractCard card, UseCardAction action);
}
