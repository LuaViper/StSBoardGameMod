package BoardGame.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGUncappedStrengthPower extends StrengthPower {
    public static final String POWER_ID = "BGUncappedStrengthPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Strength");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGUncappedStrengthPower(AbstractCreature owner, int amount) {
        super(owner, amount);
        this.name=NAME;
        this.ID="BGUncappedStrengthPower";
    }
}
