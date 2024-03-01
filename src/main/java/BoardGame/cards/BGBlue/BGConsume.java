package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.powers.BGOrbEvokePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGConsume extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGConsume");
    public static final String ID = "BGConsume";

    public BGConsume() {
        super("BGConsume", cardStrings.NAME, "blue/skill/consume", 2, cardStrings.DESCRIPTION, CardType.POWER, BGDefect.Enums.BG_BLUE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGOrbEvokePower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGConsume();
    }
}