package BoardGame.cards.BGPurple;
import BoardGame.actions.BGWorshipAction;
import BoardGame.actions.BGWreathOfFlameAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGWreathOfFlame extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWreathOfFlame");
    public static final String ID = "BGWreathOfFlame";

    public BGWreathOfFlame() {
        //img is properly wreathe_of_flame [sic]
        super("BGWreathOfFlame", cardStrings.NAME, "purple/skill/wreathe_of_flame", -1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);

        this.exhaust=true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        BGXCostCardAction.XCostInfo info = BGXCostCardAction.preProcessCard(this);
        addToTop((AbstractGameAction)new BGXCostCardAction(this, info,
                (e,d)->addToTop((AbstractGameAction)new BGWreathOfFlameAction(AbstractDungeon.player, d, e))));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust=true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGWreathOfFlame();
    }
}


