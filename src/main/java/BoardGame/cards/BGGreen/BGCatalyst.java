package BoardGame.cards.BGGreen;

import BoardGame.actions.BGGainShivAction;
import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DoublePoisonAction;
import com.megacrit.cardcrawl.actions.unique.TriplePoisonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGCatalyst extends AbstractBGCard {
    public static final String ID = "BGCatalyst";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Catalyst");
    public BGCatalyst() {
        super("BGCatalyst", cardStrings.NAME, "green/skill/catalyst", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, AbstractCard.CardColor.GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!this.upgraded) {
            addToBot((AbstractGameAction)new DoublePoisonAction((AbstractCreature)m, (AbstractCreature)p));
        } else {
            addToBot((AbstractGameAction)new TriplePoisonAction((AbstractCreature)m, (AbstractCreature)p));
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
        return new BGCatalyst();
    }
}

