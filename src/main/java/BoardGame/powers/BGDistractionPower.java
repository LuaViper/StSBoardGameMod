package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGDistractionPower
        extends AbstractBGPower {
    public static final String POWER_ID = "BGDistractionPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGDistractionPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public int baseAmount;

    public BGDistractionPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGDistractionPower";
        this.owner = owner;
        this.baseAmount=amount;
        this.amount = this.baseAmount;
        updateDescription();
        loadRegion("sadistic");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.baseAmount + DESCRIPTIONS[1];
    }

    public void atStartOfTurn(){
        this.amount=this.baseAmount;
    }

    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        //TODO: check debuff caps
        //TODO: make sure all debuffs are correctly labeled as source==player
        if(this.amount>0) {
            if (source == this.owner && target != this.owner &&
                    !target.hasPower("Artifact")) {
                //TODO: Corrupted Heart needs to check for Invulnerable debuff too
                if (power.ID.equals("BGWeakened") || power.ID.equals("BGVulnerable") || power.ID.equals("BGPoison")) {
                    flash();
                    //TODO: if it's ruled that this doesn't count if we're at the stack limit, must check weak/vuln <= 3, poison <= 30
                    //TODO: does Corpse Explosion count, and is this consistent with the card's current text?
                    addToBot((AbstractGameAction) new GainBlockAction(AbstractDungeon.player,this.baseAmount));
                    this.amount=0;
                }
            }
        }
    }
}


