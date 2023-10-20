//TODO: VG says if your 5th card is an autoplayed copy of the 4th, Vulnerable takes effect before 5th is played.  Is that intended behavior for BG?

package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGSlowPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGSlowPower");
    public static final String POWER_ID = "BGSlowPower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGSlowPower(AbstractCreature owner,int amount) {
        this.name = NAME;
        this.ID = "BGSlowPower";
        this.owner = owner;
        this.amount = 1;
        updateDescription();
        loadRegion("slow");
        this.type = AbstractPower.PowerType.DEBUFF;
    }

    public void updateDescription() {
        this.description = FontHelper.colorString(this.owner.name, "y");

        this.description += DESCRIPTIONS[0] + (this.amount) + DESCRIPTIONS[1];
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            if(this.amount>0){
                return damage + this.amount;
            }
        }

        return damage;
    }

//    public void atEndOfRound() {
//        this.amount = 4;
//        updateDescription();
//    }
//
//    public void stackPower(int stackAmount) {
//        this.fontScale = 8.0F;
//        this.amount += stackAmount;
//        if (this.amount <= 0) {
//            this.amount=-2;
//        }
//    }
//
//    public void updateDescription() {
//        this.description = FontHelper.colorString(this.owner.name, "y");
//
//        if (this.amount > 1 ) {
//            this.description += DESCRIPTIONS[0] + (this.amount) + DESCRIPTIONS[1];
//        }else if(this.amount==1){
//            this.description += DESCRIPTIONS[0] + (this.amount) + DESCRIPTIONS[2];
//        }else{
//            this.description += DESCRIPTIONS[3];
//        }
//    }
//
//
//    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
//        boolean applyVulnerable=(this.amount==1);
//        addToBot((AbstractGameAction)new ApplyPowerAction(this.owner, this.owner, new BGSlowPower(this.owner,-1), -1));
//        if(applyVulnerable){
//            addToBot((AbstractGameAction)new ApplyPowerAction(this.owner, AbstractDungeon.player, new BGVulnerablePower(this.owner,1,false), 1));
//        }
//    }


//    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
//        if (type == DamageInfo.DamageType.NORMAL) {
//            return damage * (1.0F + this.amount * 0.1F);
//        }
//        return damage;
//    }
}


