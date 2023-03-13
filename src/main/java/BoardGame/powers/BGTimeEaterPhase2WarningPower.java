package BoardGame.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGTimeEaterPhase2WarningPower extends AbstractPower {
    public static final String POWER_ID = "BGTimeEaterPhase2WarningPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGTimeEaterPhase2WarningPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGTimeEaterPhase2WarningPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGTimeEaterPhase2WarningPower";
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("unawakened");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}

