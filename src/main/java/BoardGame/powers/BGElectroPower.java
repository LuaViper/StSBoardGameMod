package BoardGame.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGElectroPower extends AbstractBGPower {
    public static final String POWER_ID = "BGElectroPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGElectroPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGElectroPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGElectroPower";
        this.owner = owner;
        updateDescription();
        loadRegion("mastery");
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}



