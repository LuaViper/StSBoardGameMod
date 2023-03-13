//TODO: this card is not supposed to be on sale at the merchant...

package BoardGame.cards.BGStatus;

import BoardGame.actions.AttemptAutoplayCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.CardDisappearsOnExhaust;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGVoidCard extends AbstractBGCard implements CardDisappearsOnExhaust {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGVoidCard");
    static final String ID = "BGVoidCard";

    public BGVoidCard() {
        super("BGVoidCard", cardStrings.NAME, "status/void", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);

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


