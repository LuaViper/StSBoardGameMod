package CoopBoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGPainAction extends AbstractGameAction {

    private int cardsInHand = 0;

    public BGPainAction(AbstractPlayer player, int cardsInHand) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.cardsInHand = cardsInHand;
        //this.targetMonster = m;
    }

    public void update() {
        //logger.info("HAND SIZE CHECK: "+cardsInHand);
        if (cardsInHand <= 2) {
            addToTop(
                (AbstractGameAction) new LoseHPAction(
                    (AbstractCreature) AbstractDungeon.player,
                    (AbstractCreature) AbstractDungeon.player,
                    1
                )
            );
        }
        this.isDone = true;
    }
}
