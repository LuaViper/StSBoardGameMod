package CoopBoardGame.cards.BGRed;

import CoopBoardGame.actions.BGPlayDrawnCardAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGHavoc extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGHavoc"
    );
    public static final String ID = "BGHavoc";

    public BGHavoc() {
        super(
            "BGHavoc",
            cardStrings.NAME,
            "red/skill/havoc",
            1,
            cardStrings.DESCRIPTION,
            AbstractCard.CardType.SKILL,
            BGIronclad.Enums.BG_RED,
            AbstractCard.CardRarity.COMMON,
            AbstractCard.CardTarget.NONE
        );
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            (AbstractGameAction) new DrawCardAction(
                1,
                (AbstractGameAction) new BGPlayDrawnCardAction(true)
            )
        );
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    public AbstractCard makeCopy() {
        return new BGHavoc();
    }
}
