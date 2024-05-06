//probably easiest to add a check to AbstractBGRelic functions
//...preferably with some way to notify the player that BGOldCoin was drawn+discarded

package BoardGame.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGDiscardedHallwayEvent
        extends AbstractBGRelic
{
    public static final String ID = "BGDiscardedHallwayEvent";
    public boolean usableAsPayment(){return false;}
    public BGDiscardedHallwayEvent() {
        super("BGDiscardedHallwayEvent", "gremlinMask.png", RelicTier.SPECIAL, LandingSound.CLINK);
        this.grayscale=true;
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }




    public boolean canSpawn() {
        return false;
     }


    public AbstractRelic makeCopy() {
        return new BGDiscardedHallwayEvent();
    }
}


