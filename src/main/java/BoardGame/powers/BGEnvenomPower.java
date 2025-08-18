package BoardGame.powers;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGEnvenomPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGEnvenomPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGEnvenomPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGEnvenomPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BoardGame:BGEnvenomPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("envenom");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    //TODO: do Shivs and CunningPotions proc this correctly?
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {
            flash();
            addToBot((AbstractGameAction)new ApplyPowerAction(target, this.owner, new BGPoisonPower(target, this.owner, this.amount), this.amount, true));
        }
    }
}



