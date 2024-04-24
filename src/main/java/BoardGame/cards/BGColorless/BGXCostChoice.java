package BoardGame.cards.BGColorless;

import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.characters.BGColorless;
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
    //TODO: we don't appear to be using doppelgangerCard anymore, could be replaced by either message arg or a boolean flag
    private AbstractCard doppelgangerCard;

    public boolean dontExpendResources;
    public AbstractCard originalXCostCard;

    public BGXCostChoice(){
        this(new BGWhirlwind(), -1, true, null);
    }

    public BGXCostChoice(AbstractCard card, int energyOnUse, boolean dontExpendResources, BGXCostCardAction.XCostAction action) {
        this(card,energyOnUse,dontExpendResources,action,null);
    }

    public BGXCostChoice(AbstractCard card, int energyOnUse, boolean dontExpendResources, BGXCostCardAction.XCostAction action, AbstractCard doppelgangerCard) {
        super("BGXCostChoice", cardStrings.NAME, doppelgangerCard==null?card.assetUrl:doppelgangerCard.assetUrl, energyOnUse, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);

        //Important: original card class must check for energy/freeplay restrictions (see BGWhirlwind.java for example)
        if(cost==-1) {
            //do nothing; probably browsing the compendium
        }else{
            this.name=card.name;
            this.originalName=card.originalName;
        }
        this.originalXCostCard =card;
        this.baseMagicNumber=cost;
        this.magicNumber=cost;
        this.dontExpendResources=dontExpendResources;
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



    public void use(AbstractPlayer p, AbstractMonster m) {
//        //TODO: complain very loudly if we're not in the middle of a ModalChoice screen
//        onChoseThisOption();
    }



    public void onChoseThisOption() {
        if(action!=null) {
            if(this.originalXCostCard instanceof AbstractBGCard) {
//                if (((AbstractBGCard)this.card).copiedCard != null) {
//                    this.copiedCard.copiedCardEnergyOnUse = this.cost; //TODO: this line is *probably* deprecated
//                }
                this.originalXCostCard.energyOnUse = this.cost;
                action.execute(this.cost, this.dontExpendResources);
            }
        }
    }

//    @Override
//    public void optionSelected(AbstractPlayer p, AbstractMonster m, int i) {
//        //TODO: complain loudly
//    }


    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGXCostChoice();
    }
}

