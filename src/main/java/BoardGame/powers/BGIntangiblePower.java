package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGIntangiblePower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGIntangiblePower");
    public static final String POWER_ID = "BGIntangiblePower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied;

    public BGIntangiblePower(AbstractCreature owner, int turns) {
        this.name = NAME;
        this.ID = "BGIntangible";
        this.owner = owner;
        //this.amount = turns;
        this.amount=0;
        updateDescription();
        loadRegion("intangible");
        this.priority = 75;
        this.justApplied = true;
    }


    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }


    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        if (damage > 0.0F) {
            damage = 0.0F;
        }
        return damage;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (this.justApplied) {
            this.justApplied = false;
            return;
        }
        flash();

        addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGIntangible"));
    }
}


