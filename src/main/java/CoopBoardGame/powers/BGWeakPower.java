package CoopBoardGame.powers;

import static CoopBoardGame.powers.BGVulnerablePower.getEffectiveIndex;
import static CoopBoardGame.powers.BGVulnerablePower.whichMonsterIsCalculatingDamage;

import CoopBoardGame.CoopBoardGame;
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
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGWeakPower extends AbstractBGPower {

    public static final String POWER_ID = CoopBoardGame.makeID("Weakened (BG)");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(
        POWER_ID
    );
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGWeakPower(AbstractCreature owner, int amount, boolean isSourceMonster) {
        this.name = NAME;
        this.ID = "BGWeakened";
        this.owner = owner;
        if (amount > 3) amount = 3;
        this.amount = amount;
        updateDescription();
        loadRegion("weak");

        this.type = AbstractPower.PowerType.DEBUFF;
        this.isTurnBased = false;

        this.priority = 99;
    }

    public void updateDescription() {
        //TODO: note about canceling out Vulnerable, if there's room for it
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + "1" + DESCRIPTIONS[3];
        } else {
            this.description =
                DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2] + "1" + DESCRIPTIONS[3];
        }
    }

    public void stackPower(int stackAmount) {
        if (this.amount == -1) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount > 3) this.amount = 3;
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (this.owner != AbstractDungeon.player) {
            if (AbstractDungeon.player.hasPower("BGVulnerable")) {
                int stacks = 1;
                if (AbstractDungeon.player.hasPower("BGVulnerable")) {
                    stacks = AbstractDungeon.player.getPower("BGVulnerable").amount;
                }
                int index = getEffectiveIndex(whichMonsterIsCalculatingDamage);

                if (index < stacks) {
                    return (float) (damage * 0.5);
                }
            }
        }
        if (true) {
            if (type == DamageInfo.DamageType.NORMAL) {
                return damage - 1;
            }
        }
        return damage;
    }

    //public void atEndOfRound() {
    public void duringTurn() {
        //if monster, wears off at end of turn
        if (this.owner != AbstractDungeon.player) {
            AbstractMonster m = (AbstractMonster) this.owner;
            if (m.getIntentBaseDmg() >= 0) {
                addToBot(
                    (AbstractGameAction) new ReducePowerAction(
                        this.owner,
                        this.owner,
                        "BGWeakened",
                        1
                    )
                );
            }
        }
    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        //if player, wears off after playing an ATTACK card
        if (this.owner == AbstractDungeon.player) {
            if (card.type == AbstractCard.CardType.ATTACK) {
                addToTop(
                    (AbstractGameAction) new ReducePowerAction(
                        this.owner,
                        this.owner,
                        "BGWeakened",
                        1
                    )
                );
            }
        }
        final Logger logger = LogManager.getLogger(CoopBoardGame.class.getName());
        logger.info("BGWeakPower: onAfterUseCard ");
    }

    public void onInitialApplication() {
        if (this.owner.hasPower("BGInvinciblePower")) {
            addToTop(
                (AbstractGameAction) new RemoveSpecificPowerAction(
                    this.owner,
                    this.owner,
                    "BGWeakened"
                )
            );
        }
    }
}
