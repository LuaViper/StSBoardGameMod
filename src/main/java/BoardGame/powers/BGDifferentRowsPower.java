package BoardGame.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

//TODO LATER: we didn't actually expect this to work with Carve Reality etc.  make sure nothing weird is going on
public class BGDifferentRowsPower extends AbstractBGPower {
    public static final String POWER_ID = "BGDifferentRowsPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGDifferentRowsPower");

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public BGDifferentRowsPower(AbstractCreature owner) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("channel");
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }
}
