package BoardGame.cards.BGColorless;

import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.BGRed.BGWhirlwind;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGXCostChoice extends AbstractBGAttackCardChoice {
    public static final String ID = "BGXCostChoice";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGXCostChoice");

    //TODO LATER: action could be private if our DoppelgangerAction was better structured.  Alternately, public card.executeAction()
    public BGXCostCardAction.XCostAction action;
    private AbstractCard doppelgangerCard;

    public BGXCostChoice(){
        this(new BGWhirlwind(), -1, null);
    }

    public BGXCostChoice(AbstractCard card, int energyOnUse, BGXCostCardAction.XCostAction action) {
        this(card,energyOnUse,action,null);
    }

    public BGXCostChoice(AbstractCard card, int energyOnUse, BGXCostCardAction.XCostAction action, AbstractCard doppelgangerCard) {
        super("BGXCostChoice", cardStrings.NAME, card.assetUrl, energyOnUse, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);

        //Important: original card class must check for energy/freeplay restrictions (see BGWhirlwind.java for example)
        if(cost==-1) {
            //do nothing; probably browsing the compendium
        }else{
            this.name=card.name;
            this.originalName=card.originalName;
        }
        this.baseMagicNumber=cost;
        this.magicNumber=cost;
        this.action=action;
        this.doppelgangerCard=doppelgangerCard;
        if (cost==-1) {
            this.rawDescription = cardStrings.DESCRIPTION;
        }else{
            if(this.doppelgangerCard==null) {
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            }else{
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0]+doppelgangerCard.name+cardStrings.EXTENDED_DESCRIPTION[1];
            }
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

