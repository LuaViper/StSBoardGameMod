package BoardGame.powers;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class BGBerserkPower extends AbstractPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Berserk"); public static final String POWER_ID = "BGBerserk";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGBerserkPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGBerserk";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("berserk");
    }


    public void onExhaust(AbstractCard card) {
        flash();
        addToBot((AbstractGameAction)new SFXAction("ATTACK_HEAVY"));
        if (Settings.FAST_MODE) {
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new CleaveEffect()));
        } else {
            addToBot((AbstractGameAction)new VFXAction(this.owner, (AbstractGameEffect)new CleaveEffect(), 0.2F));
        }
        addToBot((AbstractGameAction)new DamageAllEnemiesAction(this.owner,


                DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE, true));
    }





    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


}


