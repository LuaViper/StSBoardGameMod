package BoardGame.powers;

import BoardGame.BoardGame;
import BoardGame.actions.SpikerReflectAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static BoardGame.powers.WeakVulnCancel.*;

public class BGSpikerPower extends AbstractBGPower {
    public static final String POWER_ID = BoardGame.makeID("BGSpiker");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean justApplied = false;
    private static final float EFFECTIVENESS = 2.0F;
    private static final int EFFECTIVENESS_STRING = 100;

    public BGSpikerPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGSpiker";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("sharpHide");

        this.type = AbstractPower.PowerType.DEBUFF;
        this.isTurnBased = false;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void stackPower(int stackAmount) {
        if (this.amount == -1) {
            //logger.info(this.name + " does not stack");
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if(this.amount>5)this.amount=5;
    }


    public int onAttacked(DamageInfo info, int damageAmount) {
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("BGSpikerPower: onAttacked "+info.type+" "+this.owner);
        if(info.type == DamageInfo.DamageType.NORMAL || info.type == WEAKVULN_ZEROHITS) {
            if (this.owner != AbstractDungeon.player) {
                //monster was attacked
                //this must be addToTop.  addToBot doesn't work -- the proc remains on the monster until next card
                //addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, (AbstractPower) new BGSpikerProccedPower((AbstractCreature) this.owner, this.amount, false), this.amount));
                addToTop((AbstractGameAction) new SpikerReflectAction((AbstractCreature) this.owner, this.amount));
            }
        }
        return damageAmount;
    }

}


