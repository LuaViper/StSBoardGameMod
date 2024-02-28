package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
public class BGTemporaryRetainCardsPower extends AbstractBGPower{
    public static final String POWER_ID = "BGTemporaryRetainCardsPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGTemporaryRetainCardsPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGTemporaryRetainCardsPower(AbstractCreature owner, int numCards) {
        this.name = NAME;
        this.ID = "BGTemporaryRetainCardsPower";
        this.owner = owner;
        this.amount = numCards;
        updateDescription();
        loadRegion("retain");
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
        }
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic("Runic Pyramid") &&
                !AbstractDungeon.player.hasPower("Equilibrium"))
            addToBot((AbstractGameAction)new RetainCardsAction(this.owner, this.amount));
        addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
}