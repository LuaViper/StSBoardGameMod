package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGSnakeRing extends AbstractBGRelic  {
    public static final String ID = "BGRing of the Snake";

    public BGSnakeRing() {
        super("BGRing of the Snake", "snake_ring.png", AbstractRelic.RelicTier.STARTER, AbstractRelic.LandingSound.FLAT);
    }
    private static final int NUM_CARDS = 2;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + 2 + this.DESCRIPTIONS[1];
    }


    public void atBattleStart() {
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 2));
    }


    public AbstractRelic makeCopy() {
        return new BGSnakeRing();
    }
}


