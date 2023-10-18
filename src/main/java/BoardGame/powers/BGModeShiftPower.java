package BoardGame.powers;


import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGModeShiftPower extends AbstractBGPower {
    public static final String POWER_ID = "BGMode Shift";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Mode Shift");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGModeShiftPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGMode Shift";
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("modeShift");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}


