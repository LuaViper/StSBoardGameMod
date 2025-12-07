package CoopBoardGame.cards.BGBlue;

import CoopBoardGame.actions.BGChannelAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.orbs.BGFrost;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class BGCoolheaded extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGCoolheaded"
    );
    public static final String ID = "BGCoolheaded";

    public BGCoolheaded() {
        super(
            "BGCoolheaded",
            cardStrings.NAME,
            "blue/skill/coolheaded",
            1,
            cardStrings.DESCRIPTION,
            CardType.SKILL,
            BGDefect.Enums.BG_BLUE,
            CardRarity.COMMON,
            CardTarget.SELF
        );
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction) new BGChannelAction((AbstractOrb) new BGFrost()));
        if (upgraded) {
            addToBot((AbstractGameAction) new DrawCardAction((AbstractCreature) p, 1));
        }
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGCoolheaded();
    }
}
