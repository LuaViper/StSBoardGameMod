
package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGEvokeAllOrbsAction extends AbstractGameAction {
    public BGEvokeAllOrbsAction() {
        this.actionType = ActionType.DAMAGE;
    }

    public void update() {
        for(int i = 0; i < AbstractDungeon.player.orbs.size(); ++i) {
            this.addToTop(new BGEvokeFirstOrbAction(1));
        }

        this.isDone = true;
    }
}
