package CoopBoardGame.cards.BGGreen;

import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.powers.BGDexterityPower;
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

public class BGFootwork extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGFootwork"
    );
    public static final String ID = "BGFootwork";

    static Logger logger = LogManager.getLogger(BGFootwork.class.getName());

    public BGFootwork() {
        super(
            "BGFootwork",
            cardStrings.NAME,
            "green/power/footwork",
            2,
            cardStrings.DESCRIPTION,
            CardType.POWER,
            BGSilent.Enums.BG_GREEN,
            CardRarity.UNCOMMON,
            CardTarget.SELF
        );
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        this.selfRetain = false;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) p,
                (AbstractCreature) p,
                (AbstractPower) new BGDexterityPower((AbstractCreature) p, this.magicNumber),
                this.magicNumber
            )
        );
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.selfRetain = true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGFootwork();
    }
}
