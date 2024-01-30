package BoardGame.powers;


import BoardGame.BoardGame;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGDoubleTapPower extends AbstractBGPower {
    public static final String POWER_ID = "BGDouble Tap";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Double Tap");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    //TODOLATER: somehow allow 2nd attack to be targeted (then remove Approximate label from Double Tap)
    public BGDoubleTapPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGDouble Tap";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("doubleTap");
        logger.info("BGDOUBLETAPPOWER.CONSTRUCTOR");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0];

    }

    public void stackPower(int stackAmount) {
        if(stackAmount>0) this.amount=1;
        //this.fontScale = 8.0F;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        Logger logger = LogManager.getLogger(BGDoubleTapPower.class.getName());
        logger.info("BGDOUBLETAPPOWER.ONUSECARD");
        //TODO: copied card needs to get played FIRST, somehow
        if (!card.purgeOnUse && card.type == AbstractCard.CardType.ATTACK && this.amount > 0) {
            flash();
            //AbstractMonster m = null;



            AbstractCard tmp = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = Settings.HEIGHT / 2.0F;

            tmp.purgeOnUse = true;

            logger.info("DoubleTapPower instanceof check");
            if(card instanceof AbstractBGCard){
                logger.info("set old card's copy reference: "+tmp);
                ((AbstractBGCard)card).copiedCard=(AbstractBGCard)tmp;
            }

            //AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);

            //logger.info("DoubleTap card target type: "+card.target);
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
            }else{
                addToBot((AbstractGameAction) new NewQueueCardAction(tmp, null, true, true));
            }


            this.amount--;
            if (this.amount == 0) {
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGDouble Tap"));
            }
        }
    }


    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGDouble Tap"));
    }
}



