package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGInkBottle
        extends AbstractBGRelic implements DieControlledRelic {
    public static final String ID = "BGInkBottle";

    public BGInkBottle() {
        super("BGInkBottle", "ink_bottle.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
    }
    private static final int AMT = 1;
    public int getPrice() {return 6;}


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



//    public void atTurnStart() {
//
//            flash();
//            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
//            addToBot((AbstractGameAction)new GainEnergyAction(ENERGY_AMT));
//
//    }


    public AbstractRelic makeCopy() {
        return new BGInkBottle();
    }

    public String getQuickSummary(){if(TheDie.monsterRoll==5||TheDie.monsterRoll==6)return "Draw 1";
    else return "";}
    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==5 || TheDie.finalRelicRoll==6){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction) new DrawCardAction(AMT) );

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


