package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGOrbEndOfTurnPower extends AbstractBGPower {
    public static final String POWER_ID = "BGOrbEndOfTurnPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGOrbEndOfTurnPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGOrbEndOfTurnPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGOrbEndOfTurnPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("focus");
        this.canGoNegative = true;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_FOCUS", 0.05F);
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount == 0)
            addToTop((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGOrbEvokePower"));
        if (this.amount >= 25)
            UnlockTracker.unlockAchievement("FOCUSED");
        if (this.amount >= 999)
            this.amount = 999;
        if (this.amount <= -999)
            this.amount = -999;
    }

    public void reducePower(int reduceAmount) {
        this.fontScale = 8.0F;
        this.amount -= reduceAmount;
        if (this.amount == 0)
            addToTop((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, NAME));
        if (this.amount >= 999)
            this.amount = 999;
        if (this.amount <= -999)
            this.amount = -999;
    }

    public void updateDescription() {
        if (this.amount > 0) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
            this.type = PowerType.BUFF;
        } else {
            int tmp = -this.amount;
            this.description = DESCRIPTIONS[1] + tmp + DESCRIPTIONS[2];
            this.type = PowerType.DEBUFF;
        }
    }
}

