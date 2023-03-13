package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGSpikerProccedPower extends AbstractPower {//optional: "implements InvisiblePower"
    public static final String POWER_ID = BoardGame.makeID("BGSpikerProcced");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = false;
    private static final int EFFECTIVENESS_STRING = 1;

    public BGSpikerProccedPower(AbstractCreature owner, int amount, boolean isSourceMonster) {
        this.name = NAME;
        this.ID = "BGSpikerProcced";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("thorns");

//        if (isSourceMonster) {
//            this.justApplied = true;
//        }

        this.type = AbstractPower.PowerType.BUFF;
        this.isTurnBased = false;

        this.priority = 99;
    }

    public void stackPower(int stackAmount) {
        this.amount=stackAmount;
    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        //if monster, wears off after card resolves
        addToBot((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, new DamageInfo(this.owner, this.amount,
                DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, true));
        addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "BGSpikerProcced"));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];;
    }





}


