package BoardGame.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGWhiteBeast extends AbstractBGRelic {

    //TODO LATER: can this break the act3ending screens in any way if the player skips potion then reopens rewards?

    public static final String ID = "BGWhite Beast Statue";

    public BGWhiteBeast() {
        super("BGWhite Beast Statue", "whiteBeast.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.SOLID);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new BGWhiteBeast();
    }
}
