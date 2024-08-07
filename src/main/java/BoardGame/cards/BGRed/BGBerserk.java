package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import BoardGame.powers.BGBerserkPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGBerserk extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBerserk");
    public static final String ID = "BGBerserk";

    public BGBerserk() {
        super("BGBerserk", cardStrings.NAME, "red/power/berserk", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.POWER, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);










        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGBerserkPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGBerserk();
    }
}


