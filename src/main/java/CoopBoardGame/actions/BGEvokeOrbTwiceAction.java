package CoopBoardGame.actions;

import CoopBoardGame.screen.OrbSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;

public class BGEvokeOrbTwiceAction extends AbstractGameAction {

    public String description;

    public BGEvokeOrbTwiceAction(String description) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
        this.duration = this.startDuration;
        this.description = description;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            OrbSelectScreen.OrbSelectAction ossAction = target -> {
                //addToTop -- reverse order
                CoopBoardGame.CoopBoardGame.logger.info("BGEvokeOrbTwiceAction: slot " + target);
                addToTop(new BGEvokeSpecificOrbAction(target));
                addToTop(new BGEvokeWithoutRemovingSpecificOrbAction(target));
            };
            addToTop(new OrbSelectScreenAction(ossAction, description, false));
        }
        this.tickDuration();
    }
}
