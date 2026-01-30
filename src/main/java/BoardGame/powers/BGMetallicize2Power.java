package BoardGame.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.MetallicizePower;

public class BGMetallicize2Power extends MetallicizePower {
    public static final String POWER_ID = "BGMetallicize2Power";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public BGMetallicize2Power(AbstractCreature owner, int armorAmt) {
        super(owner, armorAmt);
        this.name = NAME;// 15
        this.ID = "BGMetallicize2Power";// 16
        this.owner = owner;// 17
        this.amount = armorAmt;// 18
        this.updateDescription();// 19
        this.loadRegion("armor");// 20
    }// 21

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_METALLICIZE", 0.05F);// 25
    }// 26

    public void updateDescription() {
        if (this.owner.isPlayer) {// 30
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];// 31
        } else {
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[3];// 33
        }

    }// 35

    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        this.flash();// 39
        this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount));// 40
    }// 41

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Metallicize");// 10
        NAME = powerStrings.NAME;// 11
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;// 12
    }
}