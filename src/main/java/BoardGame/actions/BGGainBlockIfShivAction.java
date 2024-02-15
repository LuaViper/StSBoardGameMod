package BoardGame.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGGainBlockIfShivAction extends AbstractGameAction {
    private int blockGain;

    public BGGainBlockIfShivAction(int amount) {
        setValues((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;
        this.blockGain = amount;
    }

    public void update() {
        AbstractRelic shivs=AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        if(shivs!=null){
            if(shivs.counter>0) {
                AbstractDungeon.actionManager.addToTop((AbstractGameAction) new GainBlockAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) AbstractDungeon.player, this.blockGain));
            }
        }
        this.isDone = true;
    }
}
