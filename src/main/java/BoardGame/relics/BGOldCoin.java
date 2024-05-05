//probably easiest to add a check to AbstractBGRelic functions
//...preferably with some way to notify the player that BGOldCoin was drawn+discarded

package BoardGame.relics;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGOldCoin
        extends AbstractBGRelic
{
    public static final String ID = "BGOld Coin";
    private static final int GOLD_AMT = 10;
    public boolean usableAsPayment(){return false;}

    public BGOldCoin() {
        super("BGOld Coin", "oldCoin.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.CLINK);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onEquip() {
        CardCrawlGame.sound.play("GOLD_GAIN");
        AbstractDungeon.player.gainGold(10);
    }


//   public boolean canSpawn() {
    // return ((Settings.isEndless || AbstractDungeon.floorNum <= 48) &&
    // !(AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.ShopRoom));
    // }


    public AbstractRelic makeCopy() {
        return new BGOldCoin();
    }
}


