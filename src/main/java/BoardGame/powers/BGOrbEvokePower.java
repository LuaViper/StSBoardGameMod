package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGOrbEvokePower extends AbstractBGPower {
    public static final String POWER_ID = "BGOrbEvokePower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGOrbEvokePower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGOrbEvokePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGOrbEvokePower";
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
            this.type = AbstractPower.PowerType.BUFF;
        } else {
            int tmp = -this.amount;
            this.description = DESCRIPTIONS[1] + tmp + DESCRIPTIONS[2];
            this.type = AbstractPower.PowerType.DEBUFF;
        }
    }
}

