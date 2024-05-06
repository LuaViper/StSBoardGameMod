package BoardGame.powers;


import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.patches.BGAboutToUseCard;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class BGDoubleAttackPower extends AbstractBGPower {
    public static final String POWER_ID = "BGDouble Attack";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGDouble Attack");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGDoubleAttackPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGDouble Attack";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("doubleTap");
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

    //public void onUseCard(AbstractCard card, UseCardAction action) {
    public void onAboutToUseCard(AbstractCard originalCard, AbstractCreature originalTarget) {
        //TODO: in addition to the cannotBeCopied flag, also check if the card itself is a copy
        if(this.owner.getPower("BGDouble Tap")!=null){
            //neither Double Tap nor Double Attack stack in the BG
            //it's slightly more likely that Double Tap will be available twice, so use it up first
            return;
        }
        if(this.owner.getPower("BGTripleAttackPower")!=null){
            //same check for Blasphemy tripleattack
            return;
        }

        boolean copyOK=true;
        if(originalCard instanceof AbstractBGCard){
            if(((AbstractBGCard)originalCard).cannotBeCopied) copyOK=false;
        }

        if (!originalCard.purgeOnUse && originalCard.type == AbstractCard.CardType.ATTACK && this.amount > 0 && copyOK) {
            flash();
            AbstractMonster m = null;


            AbstractCard copiedCard = originalCard.makeSameInstanceOf();
            BGDoubleAttackPower.swapOutQueueCard(copiedCard);

            AbstractDungeon.player.limbo.addToTop(copiedCard);
            copiedCard.current_x = originalCard.current_x;
            copiedCard.current_y = originalCard.current_y;
            copiedCard.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            copiedCard.target_y = Settings.HEIGHT / 2.0F;

            copiedCard.purgeOnUse = true;

            Logger logger = LogManager.getLogger(BGDoubleTapPower_DEPRECATED.class.getName());
            //logger.info("DoubleAttackPower instanceof check");
            if(originalCard instanceof AbstractBGCard){
                //logger.info("set old card's copy reference: "+copiedCard);
                ((AbstractBGCard)originalCard).copiedCard=(AbstractBGCard)copiedCard;
            }

            //((AbstractBGCard)copiedCard).followUpCardChain=new ArrayList<>(Arrays.asList(originalCard));
            ((AbstractBGCard)copiedCard).followUpCardChain=new ArrayList<>(Collections.singletonList(originalCard));

            //AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);

            //logger.info("DoubleTap card target type: "+card.target);
//            if(originalCard.target == AbstractCard.CardTarget.ENEMY || originalCard.target== AbstractCard.CardTarget.SELF_AND_ENEMY) {
//                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
//                    //logger.info("DoubleTap tssAction.execute");
//                    if (target != null) {
//                        originalCard.calculateCardDamage(target);
//                    }
//                    //logger.info("DoubleTap final target: "+target);
//                    addToBot((AbstractGameAction) new NewQueueCardAction(originalCard, target, true, true));
//                };
//                //logger.info("DoubleTap addToTop");
//                addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for "+originalCard.name+"."));
//            }else{
//                addToBot((AbstractGameAction) new NewQueueCardAction(originalCard, null, true, true));
//            }

            this.amount--;
            if (this.amount == 0) {
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGDouble Attack"));
            }
        }
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGDouble Attack"));
    }



    public static void swapOutQueueCard(AbstractCard newCopy){
        AbstractCard originalCard=BGAboutToUseCard.cardQueueItemInstance.card;
        BGAboutToUseCard.cardQueueItemInstance.card=newCopy;

        for (Iterator<AbstractCard> c = AbstractDungeon.player.hand.group.iterator(); c.hasNext(); ) {
            AbstractCard e = c.next();
            if (e == originalCard) {
                AbstractDungeon.player.limbo.addToTop(e);
                c.remove();
            }
        }

        //TODO: do we need to do this, or does queuecard handle it for us?
//        if(BGAboutToUseCard.cardQueueItemInstance.monster!=null){
//            newCopy.applyPowers();
//            newCopy.calculateCardDamage(BGAboutToUseCard.cardQueueItemInstance.monster);
//        }
    }
}



