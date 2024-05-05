package BoardGame.cards.BGBlue;

import BoardGame.actions.BGTempestAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGTempest extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTempest");
    public static final String ID = "BGTempest";

    public BGTempest() {
        super("BGTempest", cardStrings.NAME, "blue/skill/tempest", -1, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.showEvokeValue = true;
        this.showEvokeOrbCount = 3;
        this.baseMagicNumber=0;
        this.magicNumber=this.baseMagicNumber;
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        BGXCostCardAction.XCostInfo info = BGXCostCardAction.preProcessCard(this);
        addToBot((AbstractGameAction)new BGXCostCardAction(this, info,
                (e,d)->addToTop((AbstractGameAction)new BGTempestAction(AbstractDungeon.player, m, this.damage, this.damageTypeForTurn, d, e, this.magicNumber))));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGTempest();
    }
}
