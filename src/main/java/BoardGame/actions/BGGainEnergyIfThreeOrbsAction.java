package BoardGame.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class BGGainEnergyIfThreeOrbsAction extends AbstractGameAction {

    public BGGainEnergyIfThreeOrbsAction() {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        int orbcount=0;
        for(AbstractOrb o : AbstractDungeon.player.orbs){
            if(!(o instanceof EmptyOrbSlot)) {
                orbcount+=1;
            }
        }

        if (orbcount >= 3)
            AbstractDungeon.actionManager.addToTop((AbstractGameAction) new GainEnergyAction(1));


        this.isDone = true;
    }
}
