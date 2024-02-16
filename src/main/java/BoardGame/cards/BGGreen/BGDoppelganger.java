package BoardGame.cards.BGGreen;

import BoardGame.BoardGame;
import BoardGame.actions.BGDoppelgangerAction;
import BoardGame.actions.BGWhirlwindAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.ui.BGGameTips;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameTips;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.Collections;

public class BGDoppelganger extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDoppelganger");
    public static final String ID = "BGDoppelganger";

    public BGDoppelganger() {
        super("BGDoppelganger", cardStrings.NAME, "green/skill/doppelganger", -1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.NONE);
        this.exhaust=true;
    }

    //TODO LATER: ...can we just use ActionManager's card list and skip uncopyable cards?
    public static ArrayList<AbstractCard> cardsPlayedThisTurn=new ArrayList<AbstractCard>();
    public void atTurnStart(){
        cardsPlayedThisTurn=new ArrayList<AbstractCard>();
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
        //BoardGame.logger.info("Doppelganger energyOnUse: "+this.energyOnUse);
        addToTop((AbstractGameAction)new BGDoppelgangerAction(this, minEnergy, this.energyOnUse, (e)-> {}));
    }

    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) {
            return false;
        }
        this.cantUseMessage=cardStrings.EXTENDED_DESCRIPTION[0];
        int minimumEnergyToPlayDoppelganger=99999;
        canUse=false;
        for(int i=cardsPlayedThisTurn.size()-1;i>=0;i-=1){
            AbstractCard c=cardsPlayedThisTurn.get(i);
            if (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) {
                //TODO: pretty sure we want c.cost here, but maybe doublecheck at some point
                //TODO: so we probably need an AbstractBGCard.calculateCost() function for multiplayer
                if(c.cost>=0){
                    if(c.hasEnoughEnergy()) {
                        //TODO: not sure if this is checking the correct number here
                        return true;
                    }else{
                        if(c.cost<minimumEnergyToPlayDoppelganger && c.cost>=1) {
                            minimumEnergyToPlayDoppelganger=c.cost;
                            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[1]+Integer.toString(minimumEnergyToPlayDoppelganger)+cardStrings.EXTENDED_DESCRIPTION[2];
                        }
                    }
                }
            }
        }
        return false;
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust=false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGDoppelganger();
    }




    @SpirePatch2(clz = UseCardAction.class,method= SpirePatch.CONSTRUCTOR,paramtypez={AbstractCard.class, AbstractCreature.class})
    public static class UseCardActionPatch {
        @SpirePrefixPatch public static void Prefix(AbstractCard card, AbstractCreature target) {
            //TODO: most uncopyable cards haven't been flagged cannotBeCopied yet (only BGFakeShiv atm)
            if(card instanceof AbstractBGCard)
                if(((AbstractBGCard)card).cannotBeCopied)
                    return;
            //TODO: copies cannot be copied
            BoardGame.logger.info("Adding "+card.name+" to the Doppelganger stack...");
            cardsPlayedThisTurn.add(card);
        }
    }



}


