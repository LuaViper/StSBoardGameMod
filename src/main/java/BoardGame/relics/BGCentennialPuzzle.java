package BoardGame.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

//TODO: Centennial Puzzle and Self-Forming Clay don't track HP lost before relic was gained (breaks Courier functionality)

public class BGCentennialPuzzle extends AbstractBGRelic implements ClickableRelic {
    public static final String ID = "BGCentennialPuzzle";

    public BGCentennialPuzzle() {
        super("BGCentennialPuzzle", "centennialPuzzle.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.HEAVY);
    }
    public int getPrice() {return 8;}
    private static final int DRAW_AMT = 3;










    public AbstractRelic makeCopy() {
        return new BGCentennialPuzzle();
    }

    private boolean usedThisTurn = false; // You can also have a relic be only usable once per combat. Check out Hubris for more examples, including other StSlib things.
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    private boolean lostHPThisCombat=false;

    public String getUpdatedDescription() {
        String desc=this.DESCRIPTIONS[0] + DRAW_AMT + this.DESCRIPTIONS[1];
        if(this.usedUp)desc+=DieControlledRelic.USED_THIS_COMBAT; else desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        return desc;
    }

    public void wasHPLost(int damageAmount) {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                damageAmount > 0) {
            lostHPThisCombat=true;
            if(!usedThisTurn && lostHPThisCombat)beginLongPulse();     // Pulse while the player can click on it.
        }
    }

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || usedThisTurn || !isPlayerTurn || !lostHPThisCombat) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) { // Only if you're in combat
            usedThisTurn = true; // Set relic as "Used this turn"
            flash(); // Flash
            stopPulse(); // And stop the pulsing animation (which is started in atPreBattle() below)

            addToTop((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, DRAW_AMT));

            /* Used Up (Combat) */ {this.grayscale = true; this.usedUp=true; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
        }
    }

    @Override
    public void atPreBattle() {
        usedThisTurn = false; // Make sure usedThisTurn is set to false at the start of each combat.
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
        if(!usedThisTurn && lostHPThisCombat)beginLongPulse();     // Pulse while the player can click on it.
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }


    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        lostHPThisCombat=false;
        /* Unused Up */ { this.grayscale = false; this.usedUp=false; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
    }

}



