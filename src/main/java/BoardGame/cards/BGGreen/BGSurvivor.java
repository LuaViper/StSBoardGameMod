package BoardGame.cards.BGGreen;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSurvivor extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Survivor");
    public static final String ID = "BGSurvivor";

    public BGSurvivor() {
        super("BGSurvivor", cardStrings.NAME, "green/skill/survivor", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.SELF);










        this.baseBlock = 2;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        addToBot((AbstractGameAction)new DiscardAction((AbstractCreature)p, (AbstractCreature)p, 1, false));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGSurvivor();
    }
}


