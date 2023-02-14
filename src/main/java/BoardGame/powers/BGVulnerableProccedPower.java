package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGVulnerableProccedPower extends AbstractPower {//optional: "implements InvisiblePower"
    public static final String POWER_ID = BoardGame.makeID("VulnerableProcced");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = false;
    private static final int EFFECTIVENESS_STRING = 1;

    public BGVulnerableProccedPower(AbstractCreature owner, int amount, boolean isSourceMonster) {
        this.name = NAME;
        this.ID = "VulnerableProcced";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("shackle");

//        if (isSourceMonster) {
//            this.justApplied = true;
//        }

        this.type = AbstractPower.PowerType.BUFF;
        this.isTurnBased = false;


        this.priority = 99;
    }




//    public void atEndOfTurn(boolean isPlayer) {
//        //if player, wears off at end of monster action
//        if(this.owner==AbstractDungeon.player) {
//            addToBot((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, "BGVulnerable", 1));
//            addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "VulnerableProcced"));
//        }
//    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        //if monster, wears off after card resolves
        //be careful when using with "slow" cards -- damage action must go on top of stack
        addToBot((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, "BGVulnerable", 1));
        addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "VulnerableProcced"));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }



    public void stackPower(int stackAmount) {
        this.amount=1;
    }


}


