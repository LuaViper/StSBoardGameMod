package BoardGame.relics;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSecretPortalRelic extends AbstractBGRelic {
    public static final String ID = "BGSecretPortalRelic";

    private static final Logger logger = LogManager.getLogger(BGSecretPortalRelic.class.getName());

    public BGSecretPortalRelic() {
        super("BGSecretPortalRelic", "winged.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
        this.counter = -2;
    }
    public int getPrice() {return 9999;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void setCounter(int setCounter) {
        this.counter = -2;
    }



    public AbstractRelic makeCopy() {
        return new BGSecretPortalRelic();
    }


}


