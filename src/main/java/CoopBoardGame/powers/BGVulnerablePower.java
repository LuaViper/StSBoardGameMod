//TODO: Bash an enemy with 3 vuln stacks -> should still have 3 vuln stacks, not 2

package CoopBoardGame.powers;

import static CoopBoardGame.powers.WeakVulnCancel.WEAKVULN_ZEROHITS;

import CoopBoardGame.CoopBoardGame;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
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
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGVulnerablePower extends AbstractBGPower {

    public static final String POWER_ID = CoopBoardGame.makeID("Vulnerable (BG)");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(
        POWER_ID
    );

    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

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


    public void updateDescription() {
        //TODO: note about canceling out Weak, if there's room for it
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0] + "100" + DESCRIPTIONS[1] + DESCRIPTIONS[2];
        } else {
            this.description =
                DESCRIPTIONS[0] + "100" + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[3];
        }
    }

    public void stackPower(int stackAmount) {
        if (this.amount == -1) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (!this.owner.hasPower("VulnerableProcced")) {
            if (this.amount > 3) this.amount = 3;
        } else {
            if (this.amount > 4) this.amount = 4;
        }
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (this.owner != AbstractDungeon.player) {
            if (AbstractDungeon.player.hasPower("BGWeakened")) {
                return damage + 1;
            }
        }
        if (type == DamageInfo.DamageType.NORMAL) {
            if (this.amount > 0) {
                int stacks = 1;
                if (AbstractDungeon.player.hasPower("BGVulnerable")) {
                    stacks = AbstractDungeon.player.getPower("BGVulnerable").amount;
                }
                int index = getEffectiveIndex(whichMonsterIsCalculatingDamage);

                if (index < stacks) {
                    return damage * 2.0F;
                }
            }
        }

        return damage;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        final Logger logger = LogManager.getLogger(CoopBoardGame.class.getName());
        logger.info("BGVulnerablePower: onAttacked " + info.type + " " + this.owner);
        if (info.type == DamageInfo.DamageType.NORMAL || info.type == WEAKVULN_ZEROHITS) {
            if (this.owner != AbstractDungeon.player) {
                addToTop(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) this.owner,
                        (AbstractCreature) this.owner,
                        (AbstractPower) new BGVulnerableProccedPower(
                            (AbstractCreature) this.owner,
                            1,
                            false
                        ),
                        1
                    )
                );
            }
        }
        return damageAmount;
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                addToBot(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) mo,
                        (AbstractCreature) mo,
                        (AbstractPower) new BGVulnerableWatchPlayerPower(
                            (AbstractCreature) mo,
                            1,
                            false
                        ),
                        1
                    )
                );
            }
        }
    }

    public void onRemove() {
        //it was discovered that if Taskmaster encounter removes vuln then reapplies it in the same turn, the red slaver will try to immediately remove vuln upon reapplying it.  this patches it.
        if (this.owner == AbstractDungeon.player) {
            for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                addToTop(
                    (AbstractGameAction) new RemoveSpecificPowerAction(
                        (AbstractCreature) mo,
                        (AbstractCreature) mo,
                        "BGVulnerableWatchPlayer"
                    )
                );
            }
        }
    }

    public static AbstractMonster whichMonsterIsCalculatingDamage = null;

    public static int getEffectiveIndex(AbstractMonster monster) {
        if (
            AbstractDungeon.lastCombatMetricKey.equals(
                "CoopBoardGame:A7 Shelled Parasite and Fungi Beast"
            )
        ) return 0;
        if (monster == null) return 0;
        int index = 0;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!(m.halfDead || m.isDying || m.isDead)) {
                if (m.getIntentBaseDmg() > 0) {
                    if (m == monster) break;
                    index += 1;
                }
            }
        }
        return index;
    }

    @SpirePatch(clz = AbstractMonster.class, method = "calculateDamage", paramtypez = { int.class })
    public static class MonsterCalculateDamagePatch {

        @SpirePrefixPatch
        public static void Foo(AbstractMonster __instance) {
            whichMonsterIsCalculatingDamage = __instance;
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "calculateDamage", paramtypez = { int.class })
    public static class MonsterCalculateDamagePatch2 {

        @SpirePostfixPatch
        public static void Foo(AbstractMonster __instance) {
            whichMonsterIsCalculatingDamage = null;
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = { boolean.class })
    public static class MonsterCalculateDamagePatch3 {

        @SpireInsertPatch(locator = Locator.class, localvars = {})
        public static void Foo(AbstractMonster __instance) {
            if (AbstractDungeon.player.hasPower("BGVulnerable")) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.getIntentBaseDmg() > 0) {
                        m.createIntent();
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch)
                throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(
                    AbstractMonster.class,
                    "currentHealth"
                );
                return LineFinder.findInOrder(
                    ctMethodToPatch,
                    new ArrayList<Matcher>(),
                    finalMatcher
                );
            }
        }
    }
}
