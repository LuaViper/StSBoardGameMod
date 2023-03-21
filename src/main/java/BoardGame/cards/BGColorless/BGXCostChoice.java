package BoardGame.cards.BGColorless;

import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractAttackCardChoice;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGRed.BGWhirlwind;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGXCostChoice extends AbstractAttackCardChoice {
    public static final String ID = "BGXCostChoice";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGXCostChoice");

    private BGXCostCardAction.XCostAction action;

    public BGXCostChoice(){
        this(new BGWhirlwind(), -1, null);
    }
    public BGXCostChoice(AbstractCard card, int energyOnUse, BGXCostCardAction.XCostAction action) {
        super("BGXCostChoice", cardStrings.NAME, card.assetUrl, energyOnUse, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);
        if(cost==-1) {
//            this.name=cardStrings.NAME;
//            this.originalName=cardStrings.NAME;
        }else{
            this.name=card.name;
            this.originalName=card.originalName;
        }
        this.baseMagicNumber=cost;
        this.magicNumber=cost;
        this.action=action;
        if (cost==-1) {
            this.rawDescription = cardStrings.DESCRIPTION;
        }else{
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
        }
        initializeDescription();
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        if(action!=null) {
            if(this.copiedCard!=null){
                this.copiedCard.copiedCardEnergyOnUse=this.cost;
            }
            action.execute(this.cost);
        }
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGXCostChoice();
    }
}

