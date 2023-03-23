package BoardGame.cards.BGColorless;

import BoardGame.actions.BGDarkShacklesAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGDarkShackles extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDarkShackles");
    public static final String ID = "BGDarkShackles";

    public BGDarkShackles() {
        super("BGDarkShackles", cardStrings.NAME, "colorless/skill/dark_shackles", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);




        this.exhaust = true;
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGDarkShacklesAction(this.magicNumber,p));

    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGDarkShackles();
    }
}


