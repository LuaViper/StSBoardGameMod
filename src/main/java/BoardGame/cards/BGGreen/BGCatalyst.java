package BoardGame.cards.BGGreen;

import BoardGame.actions.BGDoublePoisonAction;
import BoardGame.actions.BGTriplePoisonAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGCatalyst extends AbstractBGCard {
    public static final String ID = "BGCatalyst";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGCatalyst");
    public BGCatalyst() {
        super("BGCatalyst", cardStrings.NAME, "green/skill/catalyst", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!this.upgraded) {
            addToBot((AbstractGameAction)new BGDoublePoisonAction((AbstractCreature)m, (AbstractCreature)p));
        } else {
            addToBot((AbstractGameAction)new BGTriplePoisonAction((AbstractCreature)m, (AbstractCreature)p));
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

