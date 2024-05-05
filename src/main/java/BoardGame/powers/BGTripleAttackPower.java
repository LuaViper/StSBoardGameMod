package BoardGame.powers;


//import BoardGame.actions.BGCopyCardAction;
import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//TODO: can we use whatever vanilla Entropic Brew uses to play three copies at once?  or did that not work and we have to use CopyCardAction

public class BGTripleAttackPower extends AbstractBGPower {
    public static final String POWER_ID = "BGTripleAttackPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGTripleAttackPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGTripleAttackPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGTripleAttackPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("end_turn_death");
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0]+DESCRIPTIONS[3];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2]+DESCRIPTIONS[3];
        }
    }

    public void onAboutToUseCard(AbstractCard originalCard, AbstractCreature originalTarget) {
        boolean copyOK=true;
        if(originalCard instanceof AbstractBGCard){
            if(((AbstractBGCard)originalCard).cannotBeCopied) copyOK=false;
        }

        if (!originalCard.purgeOnUse && originalCard.type == AbstractCard.CardType.ATTACK && this.amount > 0 && copyOK) {
            flash();

            AbstractCard copiedCard = originalCard.makeSameInstanceOf();
            BGDoubleAttackPower.swapOutQueueCard(copiedCard);
            AbstractDungeon.player.limbo.addToTop(copiedCard);
            copiedCard.current_x = originalCard.current_x;
            copiedCard.current_y = originalCard.current_y;
            copiedCard.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            copiedCard.target_y = Settings.HEIGHT / 2.0F;
            copiedCard.purgeOnUse = true;


            AbstractCard copiedCard2 = originalCard.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToTop(copiedCard2);
            copiedCard2.current_x = originalCard.current_x;
            copiedCard2.current_y = originalCard.current_y;
            copiedCard2.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            copiedCard2.target_y = Settings.HEIGHT / 2.0F;
            copiedCard2.purgeOnUse = true;
            if (originalCard instanceof AbstractBGCard) {
                ((AbstractBGCard) originalCard).copiedCard = (AbstractBGCard) copiedCard2;
            }
            if(originalCard instanceof AbstractBGCard){
                ((AbstractBGCard)copiedCard2).copiedCard=(AbstractBGCard)copiedCard;
            }
            ((AbstractBGCard) copiedCard).followUpCardChain = new ArrayList<>(Arrays.asList(copiedCard2,originalCard));;


            this.amount--;
            if (this.amount == 0) {
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGTripleAttackPower"));
            }
        }
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGTripleAttackPower"));
    }




}



