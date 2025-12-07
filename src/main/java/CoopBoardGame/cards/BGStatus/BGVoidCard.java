//this card is not supposed to be on sale at the merchant... keep an eye out in case the fix didn't work

package CoopBoardGame.cards.BGStatus;

import CoopBoardGame.actions.BGAttemptAutoExhaustCardAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.cards.CardDisappearsOnExhaust;
import CoopBoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: it has been ruled that this card is not actually Played when exhausted
public class BGVoidCard extends AbstractBGCard implements CardDisappearsOnExhaust {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGVoidCard"
    );
    static final String ID = "BGVoidCard";
    static final String IMG_PATH = "status/void";
    static final int ENERGY_COST = 1;

    public BGVoidCard() {
        super(
            ID,
            cardStrings.NAME,
            IMG_PATH,
            ENERGY_COST,
            cardStrings.DESCRIPTION,
            AbstractCard.CardType.STATUS,
            BGColorless.Enums.CARD_COLOR,
            AbstractCard.CardRarity.COMMON,
            AbstractCard.CardTarget.NONE
        );
        // this.isEthereal = true;
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {}

    public void upgrade() {}

    public AbstractCard makeCopy() {
        return new BGVoidCard();
    }

    public void triggerWhenDrawn() {
        addToBot(new BGAttemptAutoExhaustCardAction(this));
    }
}
