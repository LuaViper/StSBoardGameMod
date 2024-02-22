package BoardGame.powers;

//TODO NEXT: we were not given the opportunity to switch targets when duplicating CorpseExplosion

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBurstPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGBurstPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGBurstPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGBurstPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BoardGame:BGBurstPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("burst");
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        //TODO: copied card needs to get played FIRST, somehow
        //TODO: check card.cannotBeCopied flag
        boolean copyOK=true;
        if(card instanceof AbstractBGCard){
            if(((AbstractBGCard)card).cannotBeCopied) copyOK=false;
        }
        //TODO: depending on ruling, maybe preserve burst and wait until next card?
        if (!card.purgeOnUse && card.type == AbstractCard.CardType.SKILL && this.amount > 0 && copyOK) {
            flash();
            AbstractMonster m = null;
            if (action.target != null)
                m = (AbstractMonster)action.target;
            AbstractCard tmp = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = Settings.HEIGHT / 2.0F;

            tmp.purgeOnUse = true;

            if(card instanceof AbstractBGCard){
                //logger.info("set old card's copy reference: "+tmp);
                ((AbstractBGCard)card).copiedCard=(AbstractBGCard)tmp;
            }

            //TODO: restore DoubleAttack's target-picker code here
            if (m != null)
                tmp.calculateCardDamage(m);
            tmp.purgeOnUse = true;
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);



            this.amount--;
            if (this.amount == 0) {
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BoardGame:BGBurstPower"));
            }
        }
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BoardGame:BGBurstPower"));
    }
}



