package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.powers.BGWeakPower;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGRedMask extends AbstractBGRelic implements DieControlledRelic {
    public BGRedMask() {
        super("BGRedMask", "redMask.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 7;}

    public String getQuickSummary(){
        if(TheDie.monsterRoll==5||TheDie.monsterRoll==6)return "1 Weak";
    else return "";}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



    public AbstractRelic makeCopy() {
        return new BGRedMask();
    }




    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==5 || TheDie.finalRelicRoll==6){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        //TODO LATER: vanilla Red Mask is relic above monster instead; do we adjust?
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));

        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            if(target==null)return;
            addToBot((AbstractGameAction) new ApplyPowerAction(target,AbstractDungeon.player,new BGWeakPower(target,1,false),1 ));
        };
        addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Red Mask (1 Weak)."));
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


