package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGCuriosityPower extends AbstractBGPower {
    public static final String POWER_ID = "BGCuriosityPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGCuriosityPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGCuriosityPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGCuriosityPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("curiosity");
        this.type = AbstractPower.PowerType.BUFF;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.POWER) {
            flash();
            addToBot((AbstractGameAction)new ApplyPowerAction(this.owner, this.owner, new BGUncappedStrengthPower(this.owner, this.amount), this.amount));
        }
    }
}


