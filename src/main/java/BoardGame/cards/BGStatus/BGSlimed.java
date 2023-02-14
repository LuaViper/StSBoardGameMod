package BoardGame.cards.BGStatus;


import BoardGame.cards.CardDisappearsOnExhaust;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;

public class BGSlimed extends AbstractCard implements CardDisappearsOnExhaust {
    public static final String ID = "BGSlimed";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Slimed");

    public BGSlimed() {
        super("BGSlimed", cardStrings.NAME, "status/slimed", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.SELF);









        //this.purgeOnUse=true;
        this.exhaust = true;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGSlimed();
    }
}


