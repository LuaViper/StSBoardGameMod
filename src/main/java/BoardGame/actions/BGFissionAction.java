//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.defect.RemoveAllOrbsAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGFissionAction extends AbstractGameAction {
    private boolean upgraded = false;

    public BGFissionAction(boolean upgraded) {
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = AbstractGameAction.ActionType.ENERGY;
        this.upgraded = upgraded;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_XFAST) {
            int orbCount = AbstractDungeon.player.filledOrbCount();
            addToTop((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, orbCount));
            addToTop((AbstractGameAction)new GainEnergyAction(orbCount));
            if (this.upgraded) {
                addToTop(new BGEvokeAllOrbsAction());
            } else {
                addToTop(new RemoveAllOrbsAction());
            }
        }
        tickDuration();
    }
}

