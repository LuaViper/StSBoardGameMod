package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGWeakPower extends AbstractBGPower {
    public static final String POWER_ID = BoardGame.makeID("Weakened (BG)");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = false;
    private static final int EFFECTIVENESS_STRING = 1;

    public BGWeakPower(AbstractCreature owner, int amount, boolean isSourceMonster) {
        this.name = NAME;
        this.ID = "BGWeakened";
        this.owner = owner;
        if(amount>3)amount=3;
        this.amount = amount;
        updateDescription();
        loadRegion("weak");

        if (isSourceMonster) {
            this.justApplied = true;
        }

        this.type = AbstractPower.PowerType.DEBUFF;
        this.isTurnBased = false;




        this.priority = 99;
    }

//    @Override
//    public boolean shouldPushMods(DamageInfo damageInfo, Object o, List<AbstractDamageModifier> list) {
////        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
////        logger.info("BGWeakPower: shouldPushMods: true");
//        return true;
//        //return o instanceof AbstractCard && ((AbstractCard) o).type == AbstractCard.CardType.ATTACK && list.stream().noneMatch(mod -> mod instanceof FireDamage);
//    }
//
//    @Override
//    public List<AbstractDamageModifier> modsToPush(DamageInfo damageInfo, Object o, List<AbstractDamageModifier> list) {
////        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
////        logger.info("BGWeakPower: modsToPush");
//        return Collections.singletonList(new DamageModBGWeak());
//    }

//    public void atEndOfRound() {
//        if (this.justApplied) {
//            this.justApplied = false;
//
//            return;
//        }
//        if (this.amount == 0) {
//            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "Weakened"));
//        } else {
//            addToBot((AbstractGameAction)new ReducePowerAction(this.owner, this.owner, "Weakened", 1));
//        }
//    }


    public void updateDescription() {
//        if (this.amount == 1) {
//            if (this.owner != null && !this.owner.isPlayer && AbstractDungeon.player.hasRelic("Paper Crane")) {
//                this.description = DESCRIPTIONS[0] + '(' + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
//            } else {
//
//                this.description = DESCRIPTIONS[0] + '\031' + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
//            }
//
//        } else if (this.owner != null && !this.owner.isPlayer && AbstractDungeon.player.hasRelic("Paper Crane")) {
//            this.description = DESCRIPTIONS[0] + '(' + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[3];
//        }
//        else {
//
//            this.description = DESCRIPTIONS[0] + '\031' + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[3];
//        }

        //TODO: note about canceling out Vulnerable, if there's room for it
        if (this.amount == 1) {


            this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + "1" + DESCRIPTIONS[3];

        } else {

            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2] + "1" + DESCRIPTIONS[3];
        }
    }


    public void stackPower(int stackAmount) {
        if (this.amount == -1) {
            //logger.info(this.name + " does not stack");
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if(this.amount>3)this.amount=3;
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        //if(this.amount>0) {
        if(this.owner!=AbstractDungeon.player){
            if(AbstractDungeon.player.hasPower("BGVulnerable")){
                return (float) (damage*0.5);
            }
        }
        if(true){
            if (type == DamageInfo.DamageType.NORMAL) {
                return damage - 1;
            }
        }
        return damage;
    }

//    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
//        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
////        logger.info("BGWeakPower: onAttack");
////        logger.info("BGWeakPower: target: "+target);
////        logger.info("BGWeakPower: owner: "+this.owner);
////        if (this.amount == 0) {
////            addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "BGWeakened"));
////        } else {
////            addToBot((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, "BGWeakened", 1));
////        }
////        if(target==AbstractDungeon.player) {
////        }
//        if(this.owner!=AbstractDungeon.player){
//            //if monster, wears off after the attack action is complete
//            addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, (AbstractPower) new BGWeakProccedPower((AbstractCreature) this.owner, 1, false), 1));
//            //addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, (AbstractPower) new ExplosivePower((AbstractCreature) this.owner, 3), 1));
//        }
//
//    }

    //public void atEndOfRound() {
    public void duringTurn() {
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGWeakPower: duringTurn");

        //if monster, wears off at end of turn
        if(this.owner!=AbstractDungeon.player) {
            AbstractMonster m= (AbstractMonster) this.owner;
            if(m.getIntentBaseDmg() >= 0) {
                addToBot((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, "BGWeakened", 1));
                //addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "WeakProcced"));
            }
        }
    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        //if player, wears off after playing an ATTACK card
        if(this.owner==AbstractDungeon.player) {
            final Logger logger = LogManager.getLogger(BoardGame.class.getName());
            //logger.info("BGWeakPower: player onAfterUseCard");
            if (card.type == AbstractCard.CardType.ATTACK) {
                //logger.info("BGWeakPower: played an ATTACK");
                addToTop((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, "BGWeakened", 1));
            }
        }
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("BGWeakPower: onAfterUseCard ");
        //onUseCard -> onAttack -> onAfterUseCard
    }

    public void onInitialApplication() {
        //TODO: if possible, don't apply the power in the first place (requires a very specific insert patch)
        if(this.owner.hasPower("BGInvinciblePower")){
            addToTop((AbstractGameAction) new RemoveSpecificPowerAction(this.owner, this.owner, "BGWeakened"));
        }
    }
}


