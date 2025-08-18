package BoardGame.powers;

import BoardGame.actions.BGForcedWaitAction;
import BoardGame.actions.BGSurroundedAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;


public class BGSurroundedPower extends AbstractBGPower {
    public static final String POWER_ID = "BGSurroundedPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGSurroundedPower");

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public BGSurroundedPower(AbstractCreature owner) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        updateDescription();
        loadRegion("surrounded");
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    public void onEndPlayerStartTurnPhase(){
        AbstractDungeon.actionManager.addToBottom(new BGForcedWaitAction(0.5F));
        AbstractDungeon.actionManager.addToBottom(new BGSurroundedAction());
    }


}