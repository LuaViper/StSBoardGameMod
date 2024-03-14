package BoardGame.powers;


import BoardGame.actions.BGCopyCardAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

//TODO: can we use whatever vanilla Entropic Brew uses to play three copies at once?  or did that not work and we have to use CopyCardAction

public class BGTripleAttackPower extends AbstractBGPower {
    public static final String POWER_ID = "BGTripleAttackPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGTripleAttackPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGTripleAttackPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGTripleAttackPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("end_turn_death");
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0]+DESCRIPTIONS[3];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2]+DESCRIPTIONS[3];
        }
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        //TODO: copied card needs to get played FIRST, somehow
        //TODO: check card.cannotBeCopied flag
        if (!card.purgeOnUse && card.type == AbstractCard.CardType.ATTACK && this.amount > 0) {
            flash();


            addToBot(new BGCopyCardAction(card,true));
            addToBot(new WaitAction(0.25F));


            this.amount--;
            if (this.amount == 0) {
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGTripleAttackPower"));
            }
        }
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGTripleAttackPower"));
    }




}



