package BoardGame.cards.BGCurse;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGInjury extends AbstractBGCard {
    public static final String ID = "BGInjury";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Injury");

    public BGInjury() {
        super("BGInjury", cardStrings.NAME, "curse/injury", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);
    }







    public void use(AbstractPlayer p, AbstractMonster m) {}






    public void upgrade() {}






    public AbstractCard makeCopy() {
        return new BGInjury();
    }
}


