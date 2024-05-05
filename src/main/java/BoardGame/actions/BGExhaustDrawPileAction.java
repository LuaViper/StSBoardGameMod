
package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class BGExhaustDrawPileAction
        extends AbstractGameAction {

    private AbstractPlayer p;

    public BGExhaustDrawPileAction(AbstractPlayer p) {
        this.p=p;
    }

    public void update() {
        int tmp = this.p.drawPile.size();
        for (int i = 0; i < tmp; i++) {
            AbstractCard c = this.p.drawPile.getTopCard();
            this.p.drawPile.moveToExhaustPile(c);
        }

        this.isDone = true;
    }
}


