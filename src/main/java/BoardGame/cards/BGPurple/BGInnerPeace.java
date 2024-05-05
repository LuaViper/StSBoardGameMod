package BoardGame.cards.BGPurple;

import BoardGame.actions.BGInnerPeaceAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGInnerPeace extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGInnerPeace");
    public static final String ID = "BGInnerPeace";

    public BGInnerPeace() {
        super("BGInnerPeace", cardStrings.NAME, "purple/skill/inner_peace", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);
        baseMagicNumber=3;
        magicNumber=baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGInnerPeaceAction(magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGInnerPeace();
    }



}


