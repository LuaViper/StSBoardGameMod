//copying Whirlwind is currently treated as spending 0 energy on it? ...false alarm, we played DoubleTap instead of BGDoubleTap


package BoardGame.cards.BGRed;


import BoardGame.actions.BGWhirlwindAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGWhirlwind extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWhirlwind");
    public static final String ID = "BGWhirlwind";

    public BGWhirlwind() {
        super("BGWhirlwind", cardStrings.NAME, "red/attack/whirlwind", -1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ALL_ENEMY);


        this.baseDamage = 1;
        this.baseMagicNumber=0;
        this.magicNumber = this.baseMagicNumber;
        this.isMultiDamage = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        BGXCostCardAction.XCostInfo info = BGXCostCardAction.preProcessCard(this);

        //TODO: we recently changed this from Top to Bot, make sure nothing broke
        //addToTop((AbstractGameAction)new BGXCostCardAction(this, info,
        addToBot((AbstractGameAction)new BGXCostCardAction(this, info,
                (e,d)->addToTop((AbstractGameAction)new BGWhirlwindAction(AbstractDungeon.player, this.multiDamage, this.damageTypeForTurn, d, e, this.magicNumber))));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            //upgradeDamage(3);
        }
    }


    public AbstractCard makeCopy() {
        return new BGWhirlwind();
    }
}


