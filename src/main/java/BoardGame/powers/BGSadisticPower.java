package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSadisticPower
        extends AbstractBGPower {
    public static final String POWER_ID = "BGSadisticPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Sadistic");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGSadisticPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGSadisticPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("sadistic");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        //TODO: make sure all debuffs are correctly labeled as source==player
        if (source == this.owner && target != this.owner &&
                !target.hasPower("Artifact")) {
            if(power.ID.equals("BGWeakened") || power.ID.equals("BGVulnerable") || power.ID.equals("BGPoison")) {
                flash();
                //Logger logger = LogManager.getLogger(BoardGame.class.getName());
                //logger.info("power.amount: "+power.amount);
                //power.amount is the number of stacks being applied, not the total number of stacks after application
                //TODO: if it's ruled that this doesn't count if we're at the stack limit, must check weak/vuln <= 3, poison <= 30
                //TODO: does Corpse Explosion count, and is this consistent with the card's current text?
                addToBot((AbstractGameAction) new DamageAction(target, new DamageInfo(this.owner, this.amount*power.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
            }
        }
    }
}


