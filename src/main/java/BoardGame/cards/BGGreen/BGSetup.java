package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSetup extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSetup");
    public static final String ID = "BGSetup";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGSetup.class.getName());
    public BGSetup() {
        super("BGSetup", cardStrings.NAME, "green/skill/setup", 0, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.SELF);



        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust=true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainEnergyAction(this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
        this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
        initializeDescription();
    }


    public AbstractCard makeCopy() {
        return new BGSetup();
    }
}


