package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.powers.BGMayhemPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MayhemPower;

public class BGMayhem extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGMayhem");
    public static final String ID = "BGMayhem";

    public BGMayhem() {
        super("BGMayhem", cardStrings.NAME, "colorless/power/mayhem", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.POWER, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);


        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;

    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGMayhemPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }


    public AbstractCard makeCopy() {
        return new BGMayhem();
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }
}



