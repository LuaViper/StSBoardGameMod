package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.ChooseOneAttackAction;
import BoardGame.cards.BGColorless.BGGremlinHornDrawACard;
import BoardGame.cards.BGColorless.BGGremlinHornGainEnergy;
import BoardGame.cards.AbstractAttackCardChoice;
import BoardGame.cards.BGColorless.BGSneckoEyeDrawTwoCards;
import BoardGame.cards.BGColorless.BGSneckoEyeGainDazed;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class BGSneckoEye extends AbstractBGRelic implements DieControlledRelic, NilrysCodexCompatible {
    public BGSneckoEye() {
        super("BGSnecko Eye", "sneckoEye.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
        this.energyBased = true;
    }
    public int getPrice() {return 8;}

    public String getQuickSummary(){if(TheDie.monsterRoll==1||TheDie.monsterRoll==2)return "Draw 2";
    else if(TheDie.monsterRoll==3||TheDie.monsterRoll==4)return "[E]";
    else if(TheDie.monsterRoll==5||TheDie.monsterRoll==6)return "1 #rDazed";
    else return "";}

    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }
    public static final String ID = "BGSnecko Eye";

    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[0];
    }


    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }



    public AbstractRelic makeCopy() {
        return new BGSneckoEye();
    }




    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==1 || TheDie.finalRelicRoll==2){
            flash();
            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 2));
        }else if(TheDie.finalRelicRoll==3 || TheDie.finalRelicRoll==4){
            flash();
            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToBot((AbstractGameAction)new GainEnergyAction(1));
        }else if(TheDie.finalRelicRoll==5 || TheDie.finalRelicRoll==6){
            flash();
            addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        ArrayList<AbstractAttackCardChoice> attackChoices = new ArrayList<>();
        attackChoices.add(new BGSneckoEyeDrawTwoCards());
        attackChoices.add(new BGGremlinHornGainEnergy());
        attackChoices.add(new BGSneckoEyeGainDazed());
        addToBot((AbstractGameAction)new ChooseOneAttackAction(attackChoices,null,null));
        stopPulse();
    }

    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn ) {
            return;
        }
        addToBot((AbstractGameAction)new BGActivateDieAbilityAction(this));
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }

    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
    }

    @Override
    public void Trigger2Ability() {
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 2));
    }
}


