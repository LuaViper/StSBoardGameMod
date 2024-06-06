package BoardGame.actions;

import BoardGame.screen.OrbSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class BGEvokeOrbRecursionAction extends AbstractGameAction {
    public String description;


    public void update() {
        if (this.duration == this.startDuration) {
            OrbSelectScreen.OrbSelectAction ossAction = (target) -> {
                AbstractPlayer player = AbstractDungeon.player;
                AbstractOrb orb = player.orbs.get(target);
                if (orb instanceof com.megacrit.cardcrawl.orbs.EmptyOrbSlot) {
                    this.isDone = true;
                } else {
                    //TODO: is there a convincing reason to do all this instead of BGEvokeWithoutRemovingOrbAction?
                    //addToTop -- reverse order
                    addToTop(new BGChannelAction(orb, false));
                    addToTop((AbstractGameAction) new BGEvokeSpecificOrbAction(target));
                }
            };
            addToTop((AbstractGameAction) new OrbSelectScreenAction(ossAction, "Choose an Orb to Evoke.",false));
        }
        this.tickDuration();
    }
}
