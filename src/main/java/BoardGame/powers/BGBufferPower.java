package BoardGame.powers;

import BoardGame.relics.BGTheDieRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
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

public class BGBufferPower extends AbstractBGPower {
    public static final String POWER_ID = "BGBufferPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Buffer");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;


    private static int bufferIdOffset;
    private AbstractCard originalcard;
    public BGBufferPower(AbstractCreature owner, int amount, AbstractCard originalcard) {
        this.name = NAME;
        this.ID = "BoardGame:BGBufferPower" + bufferIdOffset;
        bufferIdOffset++;
        this.owner = owner;
        this.amount = amount;
        this.originalcard=originalcard;

        updateDescription();
        loadRegion("buffer");
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }

    public void updateDescription() {
        if (this.amount <= 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }



    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            addToTop((AbstractGameAction) new ReducePowerAction(this.owner, this.owner, this.ID, 1));

            if (this.amount <= 1) {
                //if amount is currently 1 (before ReducePowerAction is resolved), then it's going to 0 this turn
                Logger logger = LogManager.getLogger(BGBufferPower.class.getName());
                logger.info("Buffer: attempt to exhaust...");
                AbstractCard card = originalcard.makeStatEquivalentCopy();
                AbstractDungeon.player.discardPile.addToBottom(card);
                addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.discardPile,true));

                //this counts as losing a power card, so Awakened One loses 1 str
                for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                    AbstractPower p = m.getPower("BGCuriosityPower");
                    if (p != null) {
                        addToBot(new ReducePowerAction((AbstractCreature) m, (AbstractCreature) m, "BGUncappedStrengthPower", 1));
                    }
                }
                BGTheDieRelic.powersPlayedThisCombat -= 1;
            }
        }

        return 0;
    }

}


