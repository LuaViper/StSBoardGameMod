package BoardGame.powers;

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
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

public class BGEchoFormPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGEchoFormPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGEchoFormPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    //Echo Form doesn't take effect until start of turn, so we can set the initial counter to 999
    private int cardsDoubledThisTurn = 999;

    public BGEchoFormPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BoardGame:BGEchoFormPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("echo");
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public void updateDescription() {
        if(cardsDoubledThisTurn==999) {
            this.description = DESCRIPTIONS[3];
            return;
        }
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

    public void atStartOfTurn() {
        this.cardsDoubledThisTurn = 0;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {

        if(this.cardsDoubledThisTurn >= this.amount){
            return;
        }

        //TODO: copied card needs to get played FIRST, somehow
        //TODO: check card.cannotBeCopied flag
        boolean copyOK=true;
        if(card instanceof AbstractBGCard){
            if(((AbstractBGCard)card).cannotBeCopied) copyOK=false;
        }

        if (!card.purgeOnUse &&
                (card.type==AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL)
                && this.amount > 0 && copyOK) {
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

            if (m != null)
                tmp.calculateCardDamage(m);
            tmp.purgeOnUse = true;

            if(card.target== AbstractCard.CardTarget.ENEMY || card.target== AbstractCard.CardTarget.SELF_AND_ENEMY) {
                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                    //logger.info("DoubleTap tssAction.execute");
                    if (target != null) {
                        tmp.calculateCardDamage(target);
                    }
                    //logger.info("DoubleTap final target: "+target);
                    addToBot((AbstractGameAction) new NewQueueCardAction(tmp, target, true, true));
                };
                //logger.info("DoubleTap addToTop");
                addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for the copy of "+card.name+"."));
            }else {
                //AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
                addToBot((AbstractGameAction) new NewQueueCardAction(tmp, null, true, true));
            }


            this.cardsDoubledThisTurn+=1;
        }
    }

}



