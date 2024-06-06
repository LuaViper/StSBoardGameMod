package BoardGame.cards.BGRed;

import BoardGame.actions.GainTemporaryStrengthIfNotCappedAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGFlex extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGFlex");
    public static final String ID = "BGFlex";

    public BGFlex() {
        super("BGFlex", cardStrings.NAME, "red/skill/flex", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.SELF);










        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainTemporaryStrengthIfNotCappedAction(p,magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust = false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGFlex();
    }
}


