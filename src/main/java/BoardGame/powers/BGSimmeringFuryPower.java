package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGSimmeringFuryPower extends AbstractBGPower {
    public static final String POWER_ID = BoardGame.makeID("BGSimmeringFuryPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGSimmeringFuryPower");

    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;


    public BGSimmeringFuryPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGSimmeringFuryPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("anger");

        this.isTurnBased = false;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    //actual implementation is done in BGWrathStance

}


