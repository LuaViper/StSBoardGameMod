package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGHornCleat extends AbstractBGRelic implements DieControlledRelic, NilrysCodexCompatible {
    public static final String ID = "BGHornCleat";

    public BGHornCleat() {
        super("BGHornCleat", "horn_cleat.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.HEAVY);
    }
    public int getPrice() {return 6;}

    public AbstractRelic makeCopy() {
        return new BGHornCleat();
    }



    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==1 || TheDie.finalRelicRoll==2){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction) new GainBlockAction(AbstractDungeon.player, 1) );
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


