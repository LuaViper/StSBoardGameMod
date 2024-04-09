package BoardGame.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

//TODO: during end turn phase, if player's Block falls to 0 for any reason, check if we can activate Orichalcum again
//TODO: do something similar for Frozen Core
    //all end-turn effects appear to deal 1 damage at a time EXCEPT beat of death
public class BGOrichalcum extends AbstractBGRelic {
    public static final String ID = "BGOrichalcum";

    public BGOrichalcum() {
        super("BGOrichalcum", "orichalcum.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.HEAVY);
    }
    public int getPrice() {return 5;}
    private static final int BLOCK_AMT = 1; public boolean trigger = false;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + BLOCK_AMT + this.DESCRIPTIONS[1];
    }




    public void onPlayerEndTurn() {
        if (AbstractDungeon.player.currentBlock == 0 || this.trigger) {
            this.trigger = false;
            flash();
            stopPulse();
            addToTop((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, BLOCK_AMT));
            addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        }
    }


    public void atTurnStart() {
        this.trigger = false;
        if (AbstractDungeon.player.currentBlock == 0) {
            beginLongPulse();
        }
    }


    public int onPlayerGainedBlock(float blockAmount) {
        if (blockAmount > 0.0F) {
            stopPulse();
        }

        return MathUtils.floor(blockAmount);
    }


    public void onVictory() {
        stopPulse();
    }


    public AbstractRelic makeCopy() {
        return new BGOrichalcum();
    }
}


