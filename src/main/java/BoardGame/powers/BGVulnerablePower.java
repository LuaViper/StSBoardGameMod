package BoardGame.powers;

import BoardGame.BoardGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static BoardGame.powers.WeakVulnCancel.*;

public class BGVulnerablePower extends AbstractPower {
    public static final String POWER_ID = BoardGame.makeID("Vulnerable (BG)");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean justApplied = false;
    private static final float EFFECTIVENESS = 2.0F;
    private static final int EFFECTIVENESS_STRING = 100;

    public BGVulnerablePower(AbstractCreature owner, int amount, boolean isSourceMonster) {
        this.name = NAME;
        this.ID = "BGVulnerable";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("vulnerable");

        this.type = AbstractPower.PowerType.DEBUFF;
        this.isTurnBased = false;
    }

//    @Override
//    public boolean shouldPushMods(DamageInfo damageInfo, Object o, List<AbstractDamageModifier> list) {
//        return true;
//        //return o instanceof AbstractCard && ((AbstractCard) o).type == AbstractCard.CardType.ATTACK && list.stream().noneMatch(mod -> mod instanceof FireDamage);
//    }
//
//    @Override
//    public List<AbstractDamageModifier> modsToPush(DamageInfo damageInfo, Object o, List<AbstractDamageModifier> list) {
//        return Collections.singletonList(new DamageModBGVulnerable());
//    }

//    public void atEndOfRound() {
//        if (this.justApplied) {
//            this.justApplied = false;
//
//            return;
//        }
//        if (this.amount == 0) {
//            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "Vulnerable"));
//        } else {
//            addToBot((AbstractGameAction)new ReducePowerAction(this.owner, this.owner, "Vulnerable", 1));
//        }
//    }


    public void updateDescription() {
        //TODO: note about canceling out Weak, if there's room for it
        if (this.amount == 1) {
                this.description = DESCRIPTIONS[0] + "100" + DESCRIPTIONS[1] + DESCRIPTIONS[2];

        } else {

            this.description = DESCRIPTIONS[0] + "100" + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[3];
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

    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if(this.owner!=AbstractDungeon.player){
            if(AbstractDungeon.player.hasPower("BGWeakened")){
                return damage+1;
            }
        }
        if (type == DamageInfo.DamageType.NORMAL) {
            if(this.amount>0){
                return damage * 2.0F;
            }
        }

        return damage;
    }

//    public void duringTurn() {
//        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
//        logger.info("BGVulnerablePower: duringTurn");
//    }

public int onAttacked(DamageInfo info, int damageAmount) {
    final Logger logger = LogManager.getLogger(BoardGame.class.getName());
    logger.info("BGVulnerablePower: onAttacked "+info.type+" "+this.owner);
    if(info.type == DamageInfo.DamageType.NORMAL || info.type == WEAKVULN_ZEROHITS) {
        if (this.owner != AbstractDungeon.player) {
            //monster was attacked
            //this must be addToTop.  addToBot doesn't work -- the proc remains on the monster until next card
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.owner, (AbstractCreature) this.owner, (AbstractPower) new BGVulnerableProccedPower((AbstractCreature) this.owner, 1, false), 1));
        } else {
            //player was attacked
            //addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) info.owner, (AbstractCreature) info.owner, (AbstractPower) new BGVulnerableProccedPlayerPower((AbstractCreature) info.owner, 1, false), 1));
            //addToTop doesn't work either  :(
            //addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) info.owner, (AbstractCreature) info.owner, (AbstractPower) new BGVulnerableProccedPlayerPower((AbstractCreature) info.owner, 1, false), 1));
        }
    }
//    if(info.owner.hasPower("BGWeakened")) {
//        return damageAmount;
//    }
//    return damageAmount*2;
    return damageAmount;
}

    public void atEndOfTurn(boolean isPlayer) {
        if(isPlayer){
            for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) mo, (AbstractCreature) mo, (AbstractPower) new BGVulnerableWatchPlayerPower((AbstractCreature) mo, 1, false), 1));
            }
        }
    }

    public void onRemove(){
        //it was discovered that if Taskmaster encounter removes vuln then reapplies it in the same turn, the red slaver will try to immediately remove vuln upon reapplying it.  this patches it.
        if(this.owner==AbstractDungeon.player){
            for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                addToTop((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature) mo, (AbstractCreature) mo, "BGVulnerableWatchPlayer"));
            }
        }
    }



}


