package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.powers.BGTheBombPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGTheBomb extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGThe Bomb");
    public static final String ID = "BGThe Bomb";

    public BGTheBomb() {
        super("BGThe Bomb", cardStrings.NAME, "colorless/skill/the_bomb", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.POWER, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);


        this.baseMagicNumber = 10;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGTheBombPower((AbstractCreature)p, 3, this.magicNumber, this), 3));
    }


    public AbstractCard makeCopy() {
        return new BGTheBomb();
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }
}


