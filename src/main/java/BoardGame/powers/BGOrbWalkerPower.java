package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGOrbWalkerPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGOrbWalkerPower");
    public static final String POWER_ID = "BGOrbWalkerPower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGOrbWalkerPower(AbstractCreature owner, String newName, int strAmt) {
        this.name = newName;
        this.ID = "BGOrbWalkerPower";
        this.owner = owner;
        this.amount = strAmt;
        updateDescription();
        loadRegion("stasis");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


    public void atEndOfRound() {
        flash();
        addToBot((AbstractGameAction)new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
    }
}


