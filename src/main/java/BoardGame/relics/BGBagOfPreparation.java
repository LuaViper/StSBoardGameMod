package BoardGame.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGBagOfPreparation extends AbstractBGRelic {
    public static final String ID = "BGBag of Preparation";

    public BGBagOfPreparation() {
        super("BGBag of Preparation", "bag_of_prep.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.FLAT);
    }
    public int getPrice() {return 7;}
    private static final int NUM_CARDS = 2;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + NUM_CARDS + this.DESCRIPTIONS[1];
    }


    public void atBattleStart() {
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 2));
    }


    public AbstractRelic makeCopy() {
        return new BGBagOfPreparation();
    }
}


