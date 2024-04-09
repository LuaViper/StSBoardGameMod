package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class BGRageAction extends AbstractGameAction {
    private int blockPerCard;

    public BGRageAction(int blockAmount) {
        this.blockPerCard = blockAmount;
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player);
        this.actionType = AbstractGameAction.ActionType.BLOCK;
    }



    public void update() {
        ArrayList<AbstractCard> cardsToBlock = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                cardsToBlock.add(c);
            }
        }



        for (AbstractCard c : cardsToBlock) {
            addToTop((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, this.blockPerCard));
        }

        this.isDone = true;
    }
}


