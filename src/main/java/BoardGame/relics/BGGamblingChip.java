package BoardGame.relics;

import BoardGame.events.BGDeadAdventurer;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGGamblingChip extends AbstractBGRelic {
    public static final String ID = "BGGambling Chip";

    public BGGamblingChip() {
        super("BGGambling Chip", "gamblingChip.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
    }

    public int getPrice() {return 6;}

    public AbstractRelic makeCopy() {
        return new BGGamblingChip();
    }


    public boolean available = false;
    private boolean isPlayerTurn=false;

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
            available=false;



            TheDie.roll();
            /* Used Up (Combat) */ setUsedUp();
            //LockInRollButton.OverlayMenuDiceInterface.rerollbutton.get(AbstractDungeon.overlayMenu).visible = false;
        }
    }



    public void atPreBattle() {
        available = true;
        if(AbstractDungeon.getCurrRoom().event instanceof BGDeadAdventurer){
            if(((BGDeadAdventurer)AbstractDungeon.getCurrRoom().event).alreadyUsedGamblingChip)
            {setUsedUp();}
            //TODO: also call setUsedUp during events
        }
    }

    public void atTurnStart(){
        isPlayerTurn = true;
    }
    public void onPlayerEndTurn(){
        isPlayerTurn = false;
    }

    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        /* Unused Up */ { this.grayscale = false; this.usedUp=false; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
    }


    public void setUsedUp(){
        { available=false; this.grayscale = true; this.usedUp=true; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
    }
}


