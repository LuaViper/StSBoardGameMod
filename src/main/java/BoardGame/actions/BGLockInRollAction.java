
package BoardGame.actions;

import BoardGame.relics.BGTheDieRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGLockInRollAction
        extends AbstractGameAction {
    public BGLockInRollAction() {

    }

    public void update() {
        if (AbstractDungeon.player.hasRelic("BoardGame:BGTheDieRelic")) {
            AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGTheDieRelic");
            ((BGTheDieRelic) relic).lockRollAndActivateDieRelics();
        }
        this.isDone = true;
    }
}


