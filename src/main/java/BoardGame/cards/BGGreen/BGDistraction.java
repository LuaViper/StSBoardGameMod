package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGDistractionPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGDistraction extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDistraction");
    public static final String ID = "BGDistraction";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGDistraction.class.getName());
    public BGDistraction() {
        super("BGDistraction", cardStrings.NAME, "green/skill/distraction", 2, cardStrings.DESCRIPTION, CardType.POWER, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.SELF);

        this.baseMagicNumber = 2;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGDistractionPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.upgradeBaseCost(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGDistraction();
    }
}


