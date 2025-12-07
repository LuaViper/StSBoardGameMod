package CoopBoardGame.actions;

import CoopBoardGame.patches.DiscardInOrderOfEnergyCostPatch;
import CoopBoardGame.powers.BGAfterImagePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import java.util.ArrayList;

public class BGVaultDiscardAction extends AbstractGameAction {

    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("WishAction")).TEXT;

    public BGVaultDiscardAction() {
        this.actionType = ActionType.SPECIAL;
    }

    public void update() {
        ArrayList<AbstractCard> cardsToBeDiscarded = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!c.selfRetain) {
                cardsToBeDiscarded.add(c);
            }
        }

        DiscardInOrderOfEnergyCostPatch.sortByCost(cardsToBeDiscarded, false);
        for (AbstractCard c : cardsToBeDiscarded) {
            AbstractDungeon.player.hand.moveToDiscardPile(c);
            GameActionManager.incrementDiscard(false);
            c.triggerOnManualDiscard();
        }

        if (!cardsToBeDiscarded.isEmpty()) {
            //That did NOT trigger DiscardAction's AfterImage call, so do that now
            AbstractPower pw = AbstractDungeon.player.getPower("CoopBoardGame:BGAfterImagePower");
            if (pw != null) {
                ((BGAfterImagePower) pw).onDiscardAction();
            }
        }
        isDone = true;
    }
}
