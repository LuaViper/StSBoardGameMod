package BoardGame.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGSsserpentHead
        extends AbstractBGRelic {
    public static final String ID = "BGSsserpentHead";
    private static final int GOLD_AMT = 2;

    public BGSsserpentHead() {
        super("BGSsserpentHead", "serpentHead.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.CLINK);
    }
    public int getPrice() {return 6;}


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + GOLD_AMT + this.DESCRIPTIONS[1];
    }


    public void onEnterRoom(AbstractRoom room) {
        if (room instanceof com.megacrit.cardcrawl.rooms.EventRoom) {
            flash();
            AbstractDungeon.player.gainGold(GOLD_AMT);
        }
    }


    public AbstractRelic makeCopy() {
        return new BGSsserpentHead();
    }
}


