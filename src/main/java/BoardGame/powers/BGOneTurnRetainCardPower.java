package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGOneTurnRetainCardPower extends AbstractBGPower {
    public static final String POWER_ID = "BGOneTurnRetain Cards";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGOneTurnRetain Cards");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGOneTurnRetainCardPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGOneTurnRetain Cards";
        this.owner = owner;
        updateDescription();
        loadRegion("retain");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic("Runic Pyramid") &&
                !AbstractDungeon.player.hasPower("Equilibrium"))
            addToBot((AbstractGameAction)new RetainCardsAction(this.owner, 999));
        addToBot((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, this));
    }



}


