package CoopBoardGame.cards.BGBlue;

import CoopBoardGame.actions.BGChannelAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.orbs.BGDark;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class BGDarkness extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGDarkness"
    );
    public static final String ID = "BGDarkness";

    public BGDarkness() {
        super(
            "BGDarkness",
            cardStrings.NAME,
            "blue/skill/darkness",
            1,
            cardStrings.DESCRIPTION,
            CardType.SKILL,
            BGDefect.Enums.BG_BLUE,
            CardRarity.UNCOMMON,
            CardTarget.SELF
        );
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction) new BGChannelAction((AbstractOrb) new BGDark()));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    public AbstractCard makeCopy() {
        return new BGDarkness();
    }
}
