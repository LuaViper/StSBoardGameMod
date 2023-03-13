package BoardGame.cards.BGCurse;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGWrithe extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWrithe"); public static final String ID = "BGWrithe";

    public BGWrithe() {
        super("BGWrithe", cardStrings.NAME, "curse/writhe", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.SELF);

        this.exhaust = true;
    }




    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void upgrade() {}




    public AbstractCard makeCopy() {
        return new BGWrithe();
    }
}


