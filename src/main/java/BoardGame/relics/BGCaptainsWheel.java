package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGCaptainsWheel
        extends AbstractBGRelic implements DieControlledRelic {
    public static final String ID = "BGCaptainsWheel";

    public BGCaptainsWheel() {
        super("BGCaptainsWheel", "captain_wheel.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 8;}

    //TODO: add Abacus/Toolbox support to getQuickSummary (i.e. without clicking on the button and mousing over Accept Roll) (requires passing a value in to the function instead of using monsterRoll)

    public String getQuickSummary(){if(TheDie.monsterRoll==3)return "3 #yBlock";
        else return "";}
    private static final int AMT = 3;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }




    public AbstractRelic makeCopy() {
        return new BGCaptainsWheel();
    }


    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==3){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature) AbstractDungeon.player, AMT));
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
}


