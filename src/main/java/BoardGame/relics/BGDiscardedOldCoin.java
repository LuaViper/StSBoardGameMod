//probably easiest to add a check to AbstractBGRelic functions
//...preferably with some way to notify the player that BGOldCoin was drawn+discarded

package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGDiscardedOldCoin
        extends AbstractBGRelic
{
    public static final String ID = "BGDiscardedOld Coin";
    public boolean usableAsPayment(){return this.counter>0;}
    public BGDiscardedOldCoin() {
        super("BGDiscardedOld Coin", "oldCoin.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.CLINK);
        this.grayscale=true;
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }




    public boolean canSpawn() {
        return false;
     }


    public AbstractRelic makeCopy() {
        return new BGDiscardedOldCoin();
    }
}


