package BoardGame.cards.BGCurse;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGClumsy extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Clumsy"); public static final String ID = "Clumsy";

    public BGClumsy() {
        super("BGClumsy", cardStrings.NAME, "curse/clumsy", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);









        this.isEthereal = true;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void triggerOnEndOfPlayerTurn() {
        addToTop((AbstractGameAction)new ExhaustSpecificCardAction(this, AbstractDungeon.player.hand));
    }



    public void upgrade() {}



    public AbstractCard makeCopy() {
        return new BGClumsy();
    }
}


