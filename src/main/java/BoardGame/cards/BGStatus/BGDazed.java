package BoardGame.cards.BGStatus;

import BoardGame.cards.CardDisappearsOnExhaust;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;

public class BGDazed extends AbstractCard implements CardDisappearsOnExhaust {
    public static final String ID = "BGDazed";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Dazed");

    public BGDazed() {
        super("BGDazed", cardStrings.NAME, "status/dazed", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);










        this.isEthereal = true;
        //this.purgeOnUse=true;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGDazed();
    }
}


