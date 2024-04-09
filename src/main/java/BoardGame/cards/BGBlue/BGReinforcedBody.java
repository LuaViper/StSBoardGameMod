package BoardGame.cards.BGBlue;

import BoardGame.actions.BGReinforcedBodyEnergyCheckAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class BGReinforcedBody extends AbstractBGCard {
    //TODO: there are almost certainly uncaught bugs related to this card; playtest thoroughly
    //TODO: "I can't play *that* card for 0 Energy", played it anyway

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGReinforcedBody");
    public static final String ID = "BGReinforcedBody";

    public BGReinforcedBody() {
        super("BGReinforcedBody", cardStrings.NAME, "blue/skill/reinforced_body", -1, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseBlock=0;   //used for Dexterity tracking
        this.baseMagicNumber=0;
        this.magicNumber=this.baseMagicNumber;
    }

    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        //this will detect most cases, but not if we draw the card with e.g. BGHavoc
        boolean canUse = super.canUse(p, m);
        if(!this.upgraded) {
            if (this.costForTurn == 0 || this.cost==0) {
                this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
            if (this.copiedCardEnergyOnUse == 0) {
                this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
            if ((this.ignoreEnergyOnUse || this.isInAutoplay) && (this.copiedCardEnergyOnUse<=0)){
                this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
            if (this.freeToPlay()){
                this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
            if(EnergyPanel.totalCount<=0){
                this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
        }

        return canUse;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        BGXCostCardAction.XCostInfo info = BGXCostCardAction.preProcessCard(this);
        if (!upgraded) {
            info.minEnergy = 1;
        }

        addToBot((AbstractGameAction)new BGReinforcedBodyEnergyCheckAction(p,this,info,upgraded,this.block));
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
        return new BGReinforcedBody();
    }
}
