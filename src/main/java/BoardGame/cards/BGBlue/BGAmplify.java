package BoardGame.cards.BGBlue;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.powers.BGAmplifyPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGAmplify extends AbstractBGCard {
    public static final String ID = "BGAmplify";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGAmplify");

    public BGAmplify() {
        super("BGAmplify", cardStrings.NAME, "blue/skill/amplify", 1, cardStrings.DESCRIPTION, CardType.POWER, BGDefect.Enums.BG_BLUE, CardRarity.RARE, CardTarget.SELF);
        this.exhaust = true;
        this.baseMagicNumber = 3;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGAmplifyPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }

    public AbstractCard makeCopy() {
        return new BGAmplify();
    }
}

