//TODO: technically we're not supposed to be able to use this before relicroll is locked in
//TODO: don't flash relic if draw pile is empty

package BoardGame.relics;

import BoardGame.cards.BGColorless.BGTheCourierPotion;
import BoardGame.cards.BGColorless.BGTheCourierRelic;
import BoardGame.thedie.TheDie;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;


public class BGTheCourier extends AbstractBGRelic implements ClickableRelic {
    public static final String ID = "BGTheCourier";

    public BGTheCourier() {
        super("BGTheCourier", "courier.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.FLAT);
    }

    public int getPrice() {return 6;}




    public AbstractRelic makeCopy() {
        return new BGTheCourier();
    }


    private boolean usedThisTurn = false; // You can also have a relic be only usable once per combat. Check out Hubris for more examples, including other StSlib things.
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.


    public String getUpdatedDescription() {
        String desc= this.DESCRIPTIONS[0];
        if(this.usedUp)desc+=DieControlledRelic.USED_THIS_COMBAT; else desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        return desc;
    }



    @Override
    public void onRightClick() {// On right click
        if (!isObtained || usedThisTurn || !isPlayerTurn ) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }

        //TODO: we use this in a few places; move to static function
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof BGTheDieRelic) {
                TheDie.forceLockInRoll = true;
                ((BGTheDieRelic) relic).lockRollAndActivateDieRelics();
            }
        }

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) { // Only if you're in combat
            usedThisTurn = true; // Set relic as "Used this turn"
            flash(); // Flash
            stopPulse(); // And stop the pulsing animation (which is started in atPreBattle() below)

            ArrayList<AbstractCard> stanceChoices = new ArrayList<>();
            if(AbstractDungeon.player.hasRelic("BGSozu")){
                AbstractDungeon.player.getRelic("BGSozu").flash();
            }else if(AbstractDungeon.player.hasRelic("Sozu")){
                AbstractDungeon.player.getRelic("Sozu").flash();
            }else {
                stanceChoices.add(new BGTheCourierPotion());
            }
            stanceChoices.add(new BGTheCourierRelic());
            addToBot((AbstractGameAction)new ChooseOneAction(stanceChoices));

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


