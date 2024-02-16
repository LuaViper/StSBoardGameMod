//copying Whirlwind is currently treated as spending 0 energy on it? ...false alarm, we played DoubleTap instead of BGDoubleTap


package BoardGame.cards.BGRed;


import BoardGame.actions.BGWhirlwindAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.characters.BGIronclad;
import BoardGame.cards.AbstractBGCard;
import BoardGame.monsters.DieControlledMoves;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        int minEnergy=0;
        if(this.isCostModifiedForTurn){
            minEnergy=this.costForTurn;
            this.energyOnUse=this.costForTurn;
        }
        if(this.freeToPlay()){
            this.energyOnUse=0;
        }
        if(this.ignoreEnergyOnUse){
            this.energyOnUse=0;
        }
        if(this.copiedCardEnergyOnUse!=-99){
            this.energyOnUse=this.copiedCardEnergyOnUse;
        }

        addToTop((AbstractGameAction)new BGXCostCardAction(this, minEnergy, this.energyOnUse,
                (e)->addToTop((AbstractGameAction)new BGWhirlwindAction(AbstractDungeon.player, this.multiDamage, this.damageTypeForTurn, this.freeToPlayOnce, e,this.magicNumber))));
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


