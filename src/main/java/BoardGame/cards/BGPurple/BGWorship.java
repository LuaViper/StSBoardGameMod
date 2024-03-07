package BoardGame.cards.BGPurple;
import BoardGame.actions.BGWhirlwindAction;
import BoardGame.actions.BGWorshipAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.BGFreeCardPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGWorship extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWorship");
    public static final String ID = "BGWorship";

    public BGWorship() {
        super("BGWorship", cardStrings.NAME, "purple/skill/worship", -1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);

        this.exhaust=true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        BGXCostCardAction.XCostInfo info = BGXCostCardAction.preProcessCard(this);
        addToTop((AbstractGameAction)new BGXCostCardAction(this, info,
                (e,d)->addToTop((AbstractGameAction)new BGWorshipAction(AbstractDungeon.player, d, e))));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.selfRetain=true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGWorship();
    }
}


