package BoardGame.powers;

import BoardGame.orbs.BGLightning;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.stances.AbstractStance;

public class BGRushdownPower extends AbstractBGPower {
    public static final String POWER_ID = "BGRushdownPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGRushdownPower");

    int baseAmount=0;

    public BGRushdownPower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = "BGRushdownPower";
        this.owner = owner;
        this.baseAmount = amount;
        this.amount=this.baseAmount;
        updateDescription();
        loadRegion("rushdown");
    }

    public void stackPower(int stackAmount) {
        this.baseAmount+=stackAmount;
        if(this.amount>0) {
            this.fontScale = 8.0F;
            this.amount += stackAmount;
        }
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = powerStrings.DESCRIPTIONS[0] + this.baseAmount + powerStrings.DESCRIPTIONS[1];
        } else {
            this.description = powerStrings.DESCRIPTIONS[0] + this.baseAmount + powerStrings.DESCRIPTIONS[2];
        }
    }

    public void atStartOfTurn() {
        if(this.amount==0) {
            this.fontScale = 8.0F;
            this.amount = this.baseAmount;
        }
    }

    public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        if (!oldStance.ID.equals(newStance.ID) && newStance.ID.equals("BGWrath")) {
            if(this.amount>0) {
                flash();
                addToTop((AbstractGameAction) new DrawCardAction(this.owner, this.amount));
                this.amount=0;
            }
        }
    }
}