package BoardGame.relics;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGAnchor extends AbstractBGRelic {
    public static final String ID = "BGAnchor";

    public BGAnchor() {
        super("BGAnchor", "anchor.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.HEAVY);
    }
    public int getPrice() {return 6;}
    private static final int BLOCK_AMT = 2;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + BLOCK_AMT + this.DESCRIPTIONS[1];
    }


    public void atBattleStart() {
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, BLOCK_AMT));
        this.grayscale = true;
    }


    public void justEnteredRoom(AbstractRoom room) {
        this.grayscale = false;
    }


    public AbstractRelic makeCopy() {
        return new BGAnchor();
    }
}


