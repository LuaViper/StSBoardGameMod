package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGEnergyUpPower extends AbstractBGPower {
    public static final String POWER_ID = "BGEnergyUpPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGEnergyUpPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGEnergyUpPower(AbstractCreature owner, int energyAmt) {
        this.name = NAME;
        this.ID = "BGEnergyUpPower";
        this.owner = owner;
        this.amount = energyAmt;
        if (this.amount >= 999)
            this.amount = 999;
        updateDescription();
        loadRegion("energized_blue");
    }

    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount >= 999)
            this.amount = 999;
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
        }
    }

    public void onEnergyRecharge() {
        flash();
        AbstractDungeon.player.gainEnergy(this.amount);
    }
}