package CoopBoardGame.cards.BGPurple;

import CoopBoardGame.actions.BGOmniscienceAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGOmniscience extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGOmniscience"
    );
    public static final String ID = "BGOmniscience";

    public BGOmniscience() {
        super(
            "BGOmniscience",
            cardStrings.NAME,
            "purple/skill/omniscience",
            3,
            cardStrings.DESCRIPTION,
            CardType.SKILL,
            BGWatcher.Enums.BG_PURPLE,
            CardRarity.RARE,
            CardTarget.SELF
        );
        this.exhaust = true;
        baseMagicNumber = 2;
        magicNumber = baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction) new BGOmniscienceAction(this.magicNumber));
        addToBot(new ShuffleAction(p.drawPile, true));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(2);
        }
    }

    public AbstractCard makeCopy() {
        return new BGOmniscience();
    }
}
