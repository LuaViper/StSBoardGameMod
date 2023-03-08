package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGSundial
        extends AbstractBGRelic implements DieControlledRelic, NilrysCodexCompatible {
    public static final String ID = "BGSundial";

    public BGSundial() {
        super("BGSundial", "sundial.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 8;}

    public String getQuickSummary(){if(TheDie.monsterRoll==2)return "[E] [E]";
    else return "";}
    private static final int ENERGY_AMT = 2;

    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }


    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[0];
    }


    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }



//    public void atTurnStart() {
//
//            flash();
//            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
//            addToBot((AbstractGameAction)new GainEnergyAction(ENERGY_AMT));
//
//    }


    public AbstractRelic makeCopy() {
        return new BGSundial();
    }


    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==2){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new GainEnergyAction(ENERGY_AMT));
        stopPulse();
    }

    public void Trigger2Ability(){
        activateDieAbility();
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
}


