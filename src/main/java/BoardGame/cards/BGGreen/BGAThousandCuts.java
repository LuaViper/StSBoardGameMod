package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGAThousandCutsPower;
import BoardGame.powers.BGWraithFormPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGAThousandCuts extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGAThousandCuts");
    public static final String ID = "BGAThousandCuts";

    public BGAThousandCuts() {
        super("BGAThousandCuts", cardStrings.NAME, "green/power/a_thousand_cuts", 2, cardStrings.DESCRIPTION, CardType.POWER, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber=5;
        this.magicNumber=this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGAThousandCutsPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }


    public AbstractCard makeCopy() {
        return new BGAThousandCuts();
    }


}


