package BoardGame.relics;
import BoardGame.cards.BGRed.BGMetallicize;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGFrozenCore extends AbstractBGRelic {
    public static final String ID = "BGFrozenCore";

    public BGFrozenCore() {
        super("BGFrozenCore", "frozenOrb.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.CLINK);
    }
    private static final int BLOCK_AMT = 1; public boolean trigger = false;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void atBattleStart() {
        flash();
        addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new MetallicizePower((AbstractCreature)AbstractDungeon.player, BLOCK_AMT), BLOCK_AMT));
        addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
    }

//
//
//
//    public void onPlayerEndTurn() {
//        if (AbstractDungeon.player.currentBlock == 0 || this.trigger) {
//            this.trigger = false;
//            flash();
//            stopPulse();
//            addToTop((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, BLOCK_AMT));
//            addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
//        }
//    }
//
//
//    public void atTurnStart() {
//        this.trigger = false;
//        if (AbstractDungeon.player.currentBlock == 0) {
//            beginLongPulse();
//        }
//    }
//
//
//    public int onPlayerGainedBlock(float blockAmount) {
//        if (blockAmount > 0.0F) {
//            stopPulse();
//        }
//
//        return MathUtils.floor(blockAmount);
//    }
//
//
//    public void onVictory() {
//        stopPulse();
//    }


    public AbstractRelic makeCopy() {
        return new BGFrozenCore();
    }
}


