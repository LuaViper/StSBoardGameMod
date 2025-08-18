package BoardGame.powers;


import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGThieveryPower extends AbstractBGPower {
    public static final String POWER_ID = "BGThieveryPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Thievery");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGThieveryPower(AbstractCreature owner, int stealAmount) {
        this.name = NAME;
        this.ID = "BGThieveryPower";
        this.owner = owner;
        this.amount = stealAmount;
        updateDescription();
        loadRegion("thievery");
    }


    public void updateDescription() {
        this.description = this.owner.name + DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}



