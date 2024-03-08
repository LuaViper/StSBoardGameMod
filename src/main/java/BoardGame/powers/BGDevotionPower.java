package BoardGame.powers;

import BoardGame.actions.BGGainMiracleAction;
import BoardGame.relics.BGTheDieRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import javax.swing.*;

public class BGDevotionPower extends AbstractBGPower {
    public static final String POWER_ID = "BGDevotionPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGDevotionPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static int devotionIdOffset;
    private AbstractCard originalcard;

    public BGDevotionPower(AbstractCreature owner, int amount, AbstractCard originalcard) {
        this.name = NAME;
        this.ID = "BGDevotionPower" + devotionIdOffset;
        devotionIdOffset++;
        this.originalcard=originalcard;
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("devotion");
        this.isTurnBased = true;
    }
    public void atStartOfTurnPostDraw() {
        flash();
        addToBot(new BGGainMiracleAction(1));
        addToBot(new DrawCardAction(1));

        if (this.amount > 1) {
            addToBot((AbstractGameAction)new ReducePowerAction(this.owner, this.owner, this, 1));
        } else {
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, this));

            AbstractCard card=originalcard.makeStatEquivalentCopy();
            AbstractDungeon.player.discardPile.addToBottom(card);
            //for BGDevotionPower only, we have to addToTop, otherwise there's a chance the player's draw-1 will cause the discard pile to shuffle back into draw pile before the card is exhausted
            addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.discardPile,true));
            //this counts as losing a power card, so Awakened One loses 1 str
            for(AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters){
                AbstractPower p=m.getPower("BGCuriosityPower");
                //logger.info("check "+m+" "+p);
                if(p!=null){
                    addToBot(new ReducePowerAction((AbstractCreature)m,(AbstractCreature)m,"BGUncappedStrengthPower",1));
                }
            }
            BGTheDieRelic.powersPlayedThisCombat-=1;
        }
    }


    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        }else{
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

}



