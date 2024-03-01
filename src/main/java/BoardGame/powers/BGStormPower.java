package BoardGame.powers;

import BoardGame.orbs.BGLightning;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGStormPower extends AbstractBGPower {
    public static final String POWER_ID = "BGStormPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGStormPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGStormPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGStormPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("storm");
    }

    public void atStartOfTurn() {
        flash();
        for (int i = 0; i < this.amount; i++)
            addToBot((AbstractGameAction)new ChannelAction((AbstractOrb)new BGLightning()));

    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
