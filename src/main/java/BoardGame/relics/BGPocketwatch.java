package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGPocketwatch
        extends AbstractBGRelic implements DieControlledRelic {
    public static final String ID = "BGPocketwatch";

    public BGPocketwatch() {
        super("BGPocketwatch", "pocketwatch.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 9;}
    private static final int AMT = 3;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }




    public AbstractRelic makeCopy() {
        return new BGPocketwatch();
    }


    public String getQuickSummary(){if(TheDie.monsterRoll==3)return "Draw 3";
    else return "";}
    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==3){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new DrawCardAction(AMT));
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


