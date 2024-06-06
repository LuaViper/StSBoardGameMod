package BoardGame.cards.BGStatus;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.CardDisappearsOnExhaust;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGDazed extends AbstractBGCard implements CardDisappearsOnExhaust {
    public static final String ID = "BGDazed";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDazed");

    public BGDazed() {
        super("BGDazed", cardStrings.NAME, "status/dazed", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);










        this.isEthereal = true;
        //this.purgeOnUse=true;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGDazed();
    }
}


