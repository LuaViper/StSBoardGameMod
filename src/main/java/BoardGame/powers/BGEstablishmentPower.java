package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.EstablishmentPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGEstablishmentPower extends AbstractBGPower {
    public static final String POWER_ID = "BGEstablishmentPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGEstablishmentPower");

    private int applyNextTurn;
    public BGEstablishmentPower(AbstractCreature owner, int strengthAmount) {
        this.name = powerStrings.NAME;
        this.ID = "BGEstablishmentPower";
        this.owner = owner;
        this.amount = 0;
        updateDescription();
        loadRegion("establishment");
        this.priority = 25;
        applyNextTurn=strengthAmount;
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        applyNextTurn+=stackAmount;
    }

    public void updateDescription() {
        if(amount<=0) {
            this.description = powerStrings.DESCRIPTIONS[2];
        }else{
            this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
        }

    }

    public void atEndOfTurn(boolean isPlayer) {
        flash();
        if(applyNextTurn>0){
            amount+=applyNextTurn;
            applyNextTurn=0;
        }
    }
}

