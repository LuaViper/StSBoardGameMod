package BoardGame.cards.BGPurple;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.BGSimmeringFuryPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSimmeringFury extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSimmeringFury");
    public static final String ID = "BGSimmeringFury";

    public BGSimmeringFury() {
        super("BGSimmeringFury", cardStrings.NAME, "purple/skill/simmering_fury", 2, cardStrings.DESCRIPTION, CardType.POWER, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction(p, p, new BGSimmeringFuryPower(p, magicNumber), magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGSimmeringFury();
    }
}
