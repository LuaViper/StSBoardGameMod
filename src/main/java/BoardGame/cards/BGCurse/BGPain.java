package BoardGame.cards.BGCurse;

import BoardGame.actions.BGPainAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import BoardGame.powers.BGWeakPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGPain extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGPain");
    public static final String ID = "BGPain";

    private int cardsInHand=-1;
    public BGPain() {
        super("BGPain", cardStrings.NAME, "curse/pain", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);

    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            if(cardsInHand<0){
                cardsInHand=AbstractDungeon.player.hand.size(); //TODO: is this working correctly?
            }
            addToTop((AbstractGameAction) new BGPainAction(AbstractDungeon.player, cardsInHand));
            cardsInHand=-1;
        }
    }



    public void triggerWhenDrawn() {
        addToBot((AbstractGameAction)new SetDontTriggerAction(this, false));
    }


    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        cardsInHand=AbstractDungeon.player.hand.size(); //TODO: is this working correctly?
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }





    public void upgrade() {}



    public AbstractCard makeCopy() {
        return new BGPain();
    }
}


