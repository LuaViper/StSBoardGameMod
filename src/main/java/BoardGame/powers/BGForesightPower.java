package BoardGame.powers;

import BoardGame.cards.BGColorless.BGShivSurrogate;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGForesightPower extends AbstractBGPower {
    public static final String POWER_ID = "BGForesightPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("WireheadingPower");

    public BGForesightPower(AbstractCreature owner, int scryAmt) {
        this.name = powerStrings.NAME;
        this.ID = "BGForesightPower";
        this.owner = owner;
        this.amount = scryAmt;
        updateDescription();
        loadRegion("wireheading");
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }

    public void atStartOfTurn() {
        //if (AbstractDungeon.player.drawPile.size() <= 0)
            //addToTop((AbstractGameAction)new EmptyDeckShuffleAction());
        flash();
        addToBot((AbstractGameAction)new ScryAction(this.amount));
    }
}
