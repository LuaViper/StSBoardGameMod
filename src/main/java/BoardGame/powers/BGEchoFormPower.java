package BoardGame.powers;

import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

//TODO: Echo Form doesn't play with DoubleAttack/DoubleSkill -- currently expends both at once!
// Use DoubleSkill/Attack first, then Echo Form

public class BGEchoFormPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGEchoFormPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGEchoFormPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    //Echo Form doesn't take effect until start of turn, so we can set the initial counter to 999
    public int cardsDoubledThisTurn = 999;

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

    public static boolean isEchoFormAvailable(){
        AbstractPower p = AbstractDungeon.player.getPower("BoardGame:BGEchoFormPower");
        if (p != null) {
            if(((BGEchoFormPower)p).cardsDoubledThisTurn<p.amount){
                return true;
            }
        }
        return false;
    }
    public void atStartOfTurn() {
        this.cardsDoubledThisTurn = 0;
    }

    public void onAboutToUseCard(AbstractCard originalCard, AbstractCreature originalTarget) {

        if(!isEchoFormAvailable()){
            return;
        }
        if(originalCard.type==AbstractCard.CardType.ATTACK && this.owner.getPower("BGDouble Attack")!=null){
            return;
        }
        if(originalCard.type==AbstractCard.CardType.ATTACK && this.owner.getPower("BGTripleAttackPower")!=null){
            return;
        }
        if(originalCard.type==AbstractCard.CardType.SKILL && this.owner.getPower("BoardGame:BGBurstPower")!=null){
            return;
        }


        boolean copyOK=true;
        if(originalCard instanceof AbstractBGCard){
            if(((AbstractBGCard)originalCard).cannotBeCopied) copyOK=false;
            if(((AbstractBGCard)originalCard).ignoreFurtherCopies) copyOK=false;
        }

        if (!originalCard.purgeOnUse &&
                (originalCard.type==AbstractCard.CardType.ATTACK || originalCard.type == AbstractCard.CardType.SKILL)
                && this.amount > 0 && copyOK) {
            flash();
            AbstractMonster m = null;


            AbstractCard copiedCard = originalCard.makeSameInstanceOf();
            if(copiedCard instanceof AbstractBGCard){
                ((AbstractBGCard)originalCard).ignoreFurtherCopies=true;
                ((AbstractBGCard)copiedCard).ignoreFurtherCopies=true;
            }
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

            //((AbstractBGCard)copiedCard).followUpCardChain=new ArrayList<>(Arrays.asList(copiedCard));
            ((AbstractBGCard)copiedCard).followUpCardChain=new ArrayList<>(Collections.singletonList(originalCard));


//            if(originalCard.target== AbstractCard.CardTarget.ENEMY || originalCard.target== AbstractCard.CardTarget.SELF_AND_ENEMY) {
//                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
//                    //logger.info("DoubleTap tssAction.execute");
//                    if (target != null) {
//                        copiedCard.calculateCardDamage(target);
//                    }
//                    //logger.info("DoubleTap final target: "+target);
//                    addToBot((AbstractGameAction) new NewQueueCardAction(copiedCard, target, true, true));
//                };
//                //logger.info("DoubleTap addToTop");
//                addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for the copy of "+originalCard.name+"."));
//            }else {
//                //AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
//                addToBot((AbstractGameAction) new NewQueueCardAction(copiedCard, null, true, true));
//            }


            this.cardsDoubledThisTurn+=1;
        }
    }

}



