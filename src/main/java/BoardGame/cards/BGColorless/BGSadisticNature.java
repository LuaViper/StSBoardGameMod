package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.powers.BGSadisticPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.SadisticPower;

public class BGSadisticNature extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Sadistic Nature");
    public static final String ID = "BGSadistic Nature";

    public BGSadisticNature() {
        super("BGSadistic Nature", cardStrings.NAME, "colorless/power/sadistic_nature", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.POWER, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);










        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGSadisticPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGSadisticNature();
    }
}


