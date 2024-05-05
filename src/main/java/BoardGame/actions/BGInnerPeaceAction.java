package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGInnerPeaceAction extends AbstractGameAction {
    public BGInnerPeaceAction(int amount) {
        this.amount = amount;
    }

    public void update() {
        if (AbstractDungeon.player.stance.ID.equals("BGCalm")) {
            addToTop((AbstractGameAction)new DrawCardAction(this.amount));
        } else {
            addToTop(new ChangeStanceAction("BGCalm"));
        }
        this.isDone = true;
    }
}


