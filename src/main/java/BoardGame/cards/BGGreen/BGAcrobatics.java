package BoardGame.cards.BGGreen;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGAcrobatics extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Acrobatics");
    public static final String ID = "BGAcrobatics";

    public BGAcrobatics() {
        super("BGAcrobatics", cardStrings.NAME, "green/skill/acrobatics", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);










        this.baseMagicNumber = 3;
        this.magicNumber = this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)p, this.magicNumber));
        addToBot((AbstractGameAction)new DiscardAction((AbstractCreature)p, (AbstractCreature)p, 1, false));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGAcrobatics();
    }
}


