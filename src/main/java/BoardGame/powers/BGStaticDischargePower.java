package BoardGame.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGStaticDischargePower extends AbstractBGPower {
    public static final String POWER_ID = "BGStaticDischargePower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGStaticDischargePower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGStaticDischargePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGStaticDischargePower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("static_discharge");
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
        this.type = PowerType.BUFF;
    }
}

