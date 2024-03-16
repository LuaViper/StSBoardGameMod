package BoardGame.relics;

import BoardGame.powers.BGTheDiePower;
import BoardGame.thedie.TheDie;
import BoardGame.ui.LockInRollButton;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGTheAbacus extends AbstractBGRelic {
    public static final String ID = "BGTheAbacus";

    public BGTheAbacus() {
        super("BGTheAbacus", "abacus.png", AbstractRelic.RelicTier.SHOP, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 6;}

    public boolean available = false;
    private boolean isPlayerTurn=false;
    public boolean pendingUse=false;

    public String getUpdatedDescription() {
        String desc= this.DESCRIPTIONS[0];
        if(this.usedUp){
            desc+=DieControlledRelic.USED_THIS_COMBAT;
        }
        return desc;
    }


    public boolean isUsable(){
        if(TheDie.finalRelicRoll>0 || !available || !isPlayerTurn)
            return false;
        return true;
    }


    public void activate(){
        if(available && isPlayerTurn){
            int abacus=TheDie.initialRoll+1;if(abacus>6)abacus=1;
            int toolbox=TheDie.initialRoll-1;if(toolbox<1)toolbox=6;

            if(TheDie.monsterRoll!=abacus) {
                if(AbstractDungeon.player.hasRelic("BGToolbox")){
                    ((BGToolbox)AbstractDungeon.player.getRelic("BGToolbox")).pendingUse=false;
                }
                int r = TheDie.initialRoll + 1;
                if (r > 6) r = 1;
                TheDie.tentativeRoll(r);
            }else{
                int r = TheDie.initialRoll;
                TheDie.tentativeRoll(r);
            }
        }
    }

    public void setUsedUp(){
        available=false;
        /* Used Up (Combat) */ {this.grayscale = true; this.usedUp=true; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}

    }

    public void atPreBattle() {
        available = true;pendingUse=false;
    }

    public void atTurnStart(){
        isPlayerTurn = true;pendingUse=false;
    }
    public void onPlayerEndTurn(){
        isPlayerTurn = false;pendingUse=false;
    }

    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        /* Unused Up */ { this.pendingUse = false; this.grayscale = false; this.usedUp=false; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
    }




    public AbstractRelic makeCopy() {
        return new BGTheAbacus();
    }
}


