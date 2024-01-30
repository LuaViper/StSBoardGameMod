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

public class BGDoppelganger extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDoppelganger");
    public static final String ID = "BGDoppelganger";

    public BGDoppelganger() {
        super("BGDoppelganger", cardStrings.NAME, "green/skill/doppelganger", -1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.NONE);
        this.exhaust=true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        //TODO: DoppelgangerAction, somehow
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust=false;
            this.rawDescription = cardStrings.DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGDoppelganger();
    }
}


