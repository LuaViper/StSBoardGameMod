package BoardGame.cards.BGRed;

import BoardGame.characters.BGIronclad;
import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DoubleYourBlockAction;
import com.megacrit.cardcrawl.actions.utility.ExhaustAllEtherealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO LATER: change font size, hide 2nd "block" keyword

public class BGEntrench extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGEntrench");
    public static final String ID = "BGEntrench";

    public BGEntrench() {
        super("BGEntrench", cardStrings.NAME, "red/skill/entrench", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);

        this.exhaust = true;
    }











    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DoubleYourBlockAction((AbstractCreature)p));
    }


    public void triggerOnEndOfPlayerTurn() {
        addToTop((AbstractGameAction)new ExhaustAllEtherealAction());
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            //upgradeBaseCost(1);
            this.exhaust = false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGEntrench();
    }
}


