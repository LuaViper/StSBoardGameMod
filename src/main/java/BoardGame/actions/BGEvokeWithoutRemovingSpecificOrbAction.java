package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

//TODO: showEvokeOrbCount needs a patch
//TODO: prevent AnimateOrbAction until we know WHICH orb is evoking

public class BGEvokeWithoutRemovingSpecificOrbAction extends AbstractGameAction {
    int orbSlot;
    public BGEvokeWithoutRemovingSpecificOrbAction(int orbSlot) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
        this.orbSlot=orbSlot;
        this.duration = this.startDuration;
        this.actionType = ActionType.DAMAGE;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            if(orbSlot>=0) {
                AbstractPlayer player = AbstractDungeon.player;
                if (player != null) {
                    if (!player.orbs.isEmpty() && !(player.orbs.get(orbSlot) instanceof EmptyOrbSlot)) {
                        AbstractDungeon.player.triggerEvokeAnimation(orbSlot);
                        ((AbstractOrb) player.orbs.get(orbSlot)).onEvoke();
                    }
                }
            }
        }
        this.tickDuration();
    }


}
