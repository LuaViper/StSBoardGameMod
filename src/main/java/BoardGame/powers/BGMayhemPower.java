package BoardGame.powers;

import BoardGame.actions.BGPlayTopCardAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGMayhemPower extends AbstractBGPower {
    public static final String POWER_ID = "BGMayhemPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGMayhemPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGMayhemPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGMayhemPower";
        this.owner = owner;
        this.amount = amount;
        this.clickable=true;
        this.autoActivate=true;
        updateDescription();
        loadRegion("mayhem");
    }


    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0] + getRightClickDescriptionText();
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2] + getRightClickDescriptionText();
        }
    }


    public void onRightClick() {
        //TODO: this doesn't work -- "I can only play cards on my turn"
        //TODO: phantom cards are still a thing
        if(!onCooldown) {
            onCooldown=true;
            flash();

            for (int i = 0; i < this.amount; i++) {
                addToBot(new AbstractGameAction() {
                    public void update() {
                        //TODO: need to DRAW the card too
                        addToBot((AbstractGameAction) new BGPlayTopCardAction(
                                false));
                        this.isDone = true;

                    }
                });
            }
        }


    }
}


