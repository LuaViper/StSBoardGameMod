package BoardGame.powers;

import BoardGame.actions.CheckAkabekoStrengthCapAction;
import BoardGame.cards.BGRed.BGLimitBreak;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

//TODO LATER: depending on rulings, we might uncap strength altogether but manually cap it to 8 during calculations/numberdisplay.
public class BGAkabekoPower extends AbstractBGPower {
    public static final String POWER_ID = "BGAkabeko";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Akabeko");

    public boolean gainedStrengthSuccessfully=true;
    public BGAkabekoPower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = "BGAkabeko";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("vigor");
        this.type = AbstractPower.PowerType.BUFF;
        this.isTurnBased = false;
    }

    public void onInitialApplication() {
        //TODO: if we were already strength-capped, set gainedStrengthSuccessfully to false
        addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, (AbstractPower) new StrengthPower((AbstractCreature) AbstractDungeon.player, 1), 1));
        addToTop(new CheckAkabekoStrengthCapAction(this));
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }


    public void onUseCard(AbstractCard card, UseCardAction action) {
        if(this.gainedStrengthSuccessfully) {
            if (card instanceof BGLimitBreak) {
                addToTop((AbstractGameAction) new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -this.amount), -this.amount));
                addToBot((AbstractGameAction) new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
            }
        }
        if (card.type == AbstractCard.CardType.ATTACK) {
            if(this.gainedStrengthSuccessfully) {
                flash();
                addToBot((AbstractGameAction) new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -this.amount), -this.amount));
            }
            addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "BGAkabeko"));
        }
    }
}