package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class BGDiscardCorpseExplosionAction extends AbstractGameAction {
    private AbstractCard card;

    public BGDiscardCorpseExplosionAction(AbstractCard card) {
        this.card=card;
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player);
        this.actionType = ActionType.DISCARD;   //TODO: does this line break anything?
    }



    public void update() {
        AbstractDungeon.player.discardPile.addToTop(card);

        this.isDone = true;
    }
}


