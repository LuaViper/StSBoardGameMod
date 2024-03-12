package BoardGame.powers;

import BoardGame.BoardGame;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGInstantReboundPower extends AbstractBGPower implements InvisiblePower {
    public static final String POWER_ID = BoardGame.makeID("Instant Rebound");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justEvoked = false;

    public BGInstantReboundPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGInstantRebound";
        this.owner = owner;
        this.amount = 1;
        updateDescription();
        loadRegion("rebound");
        this.isTurnBased = true;
        this.type = AbstractPower.PowerType.BUFF;
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("BGInstantReboundPower constructor");
    }


    public void updateDescription() {
        if (this.amount > 1) {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0];
        }
    }


    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("BGInstantReboundPower.onAfterUseCard");
        if (card.type != AbstractCard.CardType.POWER) {
            flash();
            action.reboundCard = true;
        }
        addToBot((AbstractGameAction)new ReducePowerAction(this.owner, this.owner, "BGInstantRebound", 1));
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGInstantRebound"));
    }
}


