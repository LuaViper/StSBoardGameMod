package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.powers.BGAmplifyPower;
import BoardGame.powers.BGOrbEndOfTurnPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGDefragment extends AbstractBGCard {
    public static final String ID = "BGDefragment";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDefragment");

    public BGDefragment() {
        super("BGDefragment", cardStrings.NAME, "blue/power/defragment", 3, cardStrings.DESCRIPTION, CardType.POWER, BGDefect.Enums.BG_BLUE, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGOrbEndOfTurnPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isEthereal=false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGDefragment();
    }
}

