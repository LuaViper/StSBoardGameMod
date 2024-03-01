//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGEvokeFirstOrbAction extends AbstractGameAction {
    private int orbCount;

    public BGEvokeFirstOrbAction(int amount) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }

        this.duration = this.startDuration;
        this.orbCount = amount;
        this.actionType = ActionType.DAMAGE;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            for(int i = 0; i < this.orbCount; ++i) {
                AbstractDungeon.player.evokeOrb();
            }
        }

        this.tickDuration();
    }
}
