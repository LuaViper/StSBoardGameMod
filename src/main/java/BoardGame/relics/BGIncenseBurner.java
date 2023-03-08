
package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.BGInvinciblePlayerPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGIncenseBurner extends AbstractBGRelic implements DieControlledRelic {
    public static final String ID = "BGIncense Burner";

    public BGIncenseBurner() {
        super("BGIncense Burner", "incenseBurner.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.CLINK);
    }

    public String getQuickSummary(){if(TheDie.monsterRoll==6)return "1 #yInvincible";
    else return "";}
    public int getPrice() {return 11;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new BGIncenseBurner();
    }


    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==6){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        //addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature) AbstractDungeon.player, AMT));
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGInvinciblePlayerPower((AbstractCreature)AbstractDungeon.player, 1), 0));
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


