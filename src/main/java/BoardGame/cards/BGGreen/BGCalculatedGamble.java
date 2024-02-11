package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.CalculatedGambleAction;
import com.megacrit.cardcrawl.actions.unique.DoublePoisonAction;
import com.megacrit.cardcrawl.actions.unique.TriplePoisonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGCalculatedGamble extends AbstractBGCard {
    public static final String ID = "BGCalculatedGamble";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Calculated Gamble");

    public BGCalculatedGamble() {
        super("BGCalculatedGamble", cardStrings.NAME, "green/skill/calculated_gamble", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, AbstractCard.CardColor.GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.NONE);
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new CalculatedGambleAction(false));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            this.exhaust = false;
        }
    }

    public AbstractCard makeCopy() {
        return new BGCalculatedGamble();
    }
}
