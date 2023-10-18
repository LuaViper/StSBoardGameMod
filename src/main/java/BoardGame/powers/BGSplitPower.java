package BoardGame.powers;


import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGSplitPower extends AbstractBGPower {
    public static final String POWER_ID = "Split";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Split");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGSplitPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "Split";
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("split");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}


