//this card is not supposed to be on sale at the merchant... keep an eye out in case the fix didn't work

package BoardGame.cards.BGStatus;

import BoardGame.actions.AttemptAutoplayCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.CardDisappearsOnExhaust;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: it has been ruled that this card is not actually Played when exhausted
public class BGVoidCard extends AbstractBGCard implements CardDisappearsOnExhaust {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGVoidCard");
    static final String ID = "BGVoidCard";

    public BGVoidCard() {
        super("BGVoidCard", cardStrings.NAME, "status/void", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);

        //this.isEthereal = true;
        this.exhaust=true;
    }


    public void triggerWhenDrawn() {
        addToBot((AbstractGameAction) new AttemptAutoplayCardAction(this));
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGVoidCard();
    }
}


