package CoopBoardGame.cards.BGPurple;

import CoopBoardGame.actions.BGGainMiracleAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGCollect extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGCollect"
    );
    public static final String ID = "BGCollect";

    public BGCollect() {
        super(
            "BGCollect",
            cardStrings.NAME,
            "purple/skill/collect",
            1,
            cardStrings.DESCRIPTION,
            CardType.SKILL,
            BGWatcher.Enums.BG_PURPLE,
            CardRarity.COMMON,
            CardTarget.SELF
        );
        exhaust = true;
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction) new BGGainMiracleAction(magicNumber, this));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGCollect();
    }
}
