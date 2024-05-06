package BoardGame.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGChampPhase2WarningPower extends AbstractBGPower {
    public static final String POWER_ID = "BGChampPhase2WarningPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGChampPhase2WarningPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGChampPhase2WarningPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGChampPhase2WarningPower";
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("unawakened");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}

