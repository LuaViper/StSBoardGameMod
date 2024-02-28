package BoardGame.cards.BGGreen;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: "to any player"
public class BGDefend_Green extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDefend_G");
    public static final String ID = "BGDefend_G";

    public BGDefend_Green() {
        super("BGDefend_G", cardStrings.NAME, "green/skill/defend", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.SELF);










        this.baseBlock = 1;
        this.tags.add(AbstractCard.CardTags.STARTER_DEFEND);
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.isDebug) {
            addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, 50));
        } else {
            addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        }
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

        }
    }


    public AbstractCard makeCopy() {
        return new BGDefend_Green();
    }
}


