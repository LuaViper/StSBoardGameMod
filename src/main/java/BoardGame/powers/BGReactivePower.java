package BoardGame.powers;

import BoardGame.actions.SpaghettiRerollDieAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGReactivePower extends AbstractBGPower {
    public static final String POWER_ID = "BGReactivePower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGReactivePower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGReactivePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGReactivePower";
        this.owner = owner;
        updateDescription();
        loadRegion("reactive");
        this.priority = 50;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }


    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && damageAmount < this.owner.currentHealth) {

            flash();
            //addToBot((AbstractGameAction)new RollMoveAction((AbstractMonster)this.owner));
            addToBot((AbstractGameAction)new SpaghettiRerollDieAction());
        }
        return damageAmount;
    }
}


