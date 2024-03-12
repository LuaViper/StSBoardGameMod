
// copy RetainCardPower (99 stacks)
//but don't actually use RetainCardPower itself, since it doesn't wear off at end of turn

package BoardGame.relics;

import BoardGame.powers.BGOneTurnRetainCardPower;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.SharpHidePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;


public class BGRunicPyramid extends AbstractBGRelic implements ClickableRelic {
    public static final String ID = "BGRunic Pyramid";

    public BGRunicPyramid() {
        super("BGRunic Pyramid", "runicPyramid.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.FLAT);
    }

    public int getPrice() {return 6;}



    public AbstractRelic makeCopy() {
        return new BGRunicPyramid();
    }


    private boolean usedThisTurn = false; // You can also have a relic be only usable once per combat. Check out Hubris for more examples, including other StSlib things.
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.


    public String getUpdatedDescription() {
        String desc=this.DESCRIPTIONS[0];
        if(this.usedUp)desc+=DieControlledRelic.USED_THIS_COMBAT; else desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        return desc;
    }


    @Override
    public void onRightClick() {// On right click
        if (!isObtained || usedThisTurn || !isPlayerTurn ) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) { // Only if you're in combat
            usedThisTurn = true; // Set relic as "Used this turn"
            flash(); // Flash
            stopPulse(); // And stop the pulsing animation (which is started in atPreBattle() below)

            addToBot((AbstractGameAction)new ApplyPowerAction(AbstractDungeon.player,AbstractDungeon.player,(AbstractPower)new BGOneTurnRetainCardPower((AbstractCreature)AbstractDungeon.player)));

            /* Used Up (Combat) */ {this.grayscale = true; this.usedUp=true; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
        }
    }

    @Override
    public void atPreBattle() {
        usedThisTurn = false; // Make sure usedThisTurn is set to false at the start of each combat.
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
        if(!usedThisTurn)beginLongPulse();     // Pulse while the player can click on it.
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }


    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        /* Unused Up */ { this.grayscale = false; this.usedUp=false; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
    }

}


