package BoardGame.actions;

import BoardGame.screen.OrbSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.Collections;

public class BGEvokeOrbTwiceAction extends AbstractGameAction {
    public String description;
    public BGEvokeOrbTwiceAction(String description) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
        this.duration = this.startDuration;
        this.description=description;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            OrbSelectScreen.OrbSelectAction ossAction = (target) -> {
                AbstractPlayer player = AbstractDungeon.player;
                //addToTop -- reverse order
                BoardGame.BoardGame.logger.info("BGEvokeOrbTwiceAction: slot "+target);
                addToTop((AbstractGameAction)new BGEvokeSpecificOrbAction(target));
                addToTop((AbstractGameAction)new BGEvokeWithoutRemovingSpecificOrbAction(target));
            };
            addToTop((AbstractGameAction)new OrbSelectScreenAction(ossAction,description));
        }
        this.tickDuration();
    }
}
