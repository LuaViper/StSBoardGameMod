package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.Collections;

//TODO: showEvokeOrbCount needs a patch
//TODO: prevent AnimateOrbAction until we know WHICH orb is evoking

public class BGEvokeSpecificOrbAction extends AbstractGameAction {
    int orbSlot;
    public BGEvokeSpecificOrbAction(int orbSlot) {
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
            AbstractPlayer player = AbstractDungeon.player;
            if (player != null) {
                if(orbSlot>=0) {
                    if (player.orbs.size() > orbSlot && !(player.orbs.get(orbSlot) instanceof EmptyOrbSlot)) {
                        AbstractDungeon.player.triggerEvokeAnimation(orbSlot);
                        ((AbstractOrb) player.orbs.get(orbSlot)).onEvoke();
                        EmptyOrbSlot emptyOrbSlot = new EmptyOrbSlot();
                        int i;
                        for (i = orbSlot + 1; i < player.orbs.size(); i++)
                            Collections.swap(player.orbs, i, i - 1);
                        player.orbs.set(player.orbs.size() - 1, emptyOrbSlot);
                        for (i = 0; i < player.orbs.size(); i++)
                            ((AbstractOrb) player.orbs.get(i)).setSlot(i, player.maxOrbs);
                    }
                }
            }
        }
        this.tickDuration();
    }





}
