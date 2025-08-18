//Calipers is handled in line 460 of GameActionManager
// we don't need to patch it exactly there, but it's probably a good place to check for leftover block

package BoardGame.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.BlurPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

//TODO: in a solo game, Calipers changes to "keep your leftover Block at the END of THIS turn." (vanilla Blur power)

public class BGCalipers
        extends AbstractBGRelic implements ClickableRelic {
    public static final String ID = "BGCalipers";
    public static int blockLastTurn=0;

    public BGCalipers() {
        super("BGCalipers", "calipers.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.CLINK);
    }
    public int getPrice() {return 6;}




    public AbstractRelic makeCopy() {
        return new BGCalipers();
    }

    public boolean usedThisTurn = false; // You can also have a relic be only usable once per combat. Check out Hubris for more examples, including other StSlib things.
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    private static Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());

    public String getUpdatedDescription() {
        logger.info("BGCalipers.getUpdatedDescription...");
        //String desc = this.DESCRIPTIONS[0] + blockLastTurn + this.DESCRIPTIONS[1];
        String desc = this.DESCRIPTIONS[2];
        if(this.usedUp)desc+=DieControlledRelic.USED_THIS_COMBAT; else desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        return desc;
    }



    @Override
    public
    void onRightClick() {// On right click
        if (!isObtained || usedThisTurn || !isPlayerTurn) {
        //if (!isObtained || usedThisTurn || !isPlayerTurn || !(blockLastTurn>0)) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) { // Only if you're in combat
            usedThisTurn = true; // Set relic as "Used this turn"
            flash(); // Flash
            stopPulse(); // And stop the pulsing animation (which is started in atPreBattle() below)

            //addToBot((AbstractGameAction)new GainBlockAction(AbstractDungeon.player,blockLastTurn));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BlurPower(AbstractDungeon.player, 1), 1));

            /* Used Up (Combat) */ {this.grayscale = true; this.usedUp=true; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
        }
    }

    @Override
    public void atPreBattle() {
        usedThisTurn = false; // Make sure usedThisTurn is set to false at the start of each combat.
        blockLastTurn=0;
        description=getUpdatedDescription(); tips.clear();tips.add(new PowerTip(name, description));initializeTips();
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
        //if(!usedThisTurn)beginLongPulse();
            //don't pulse if blockLastTurn==0.  however, this is determined AFTER atTurnStart
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



    @SpirePatch(clz = GameActionManager.class, method = "getNextAction",
            paramtypez = {})
    public static class GameActionManagerCalipersPatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars = {}
        )
        public static SpireReturn<Void> Insert(){
            //TODO: with Barricade + Metallicize, Calipers displays "gain 1 block" instead of 0.
            BGCalipers.blockLastTurn=0;
            //logger.info("BGCalipers: insertpatch...");
            if (!AbstractDungeon.player.hasPower("Barricade") && !AbstractDungeon.player.hasPower("Blur")) {
                BGCalipers.blockLastTurn=AbstractDungeon.player.currentBlock;
                if (AbstractDungeon.player.hasRelic("BGCalipers")) {
                    BGCalipers r = (BGCalipers) AbstractDungeon.player.getRelic("BGCalipers");
                    r.description=r.getUpdatedDescription();
                    r.tips.clear();r.tips.add(new PowerTip(r.name, r.description));r.initializeTips();
                    if(BGCalipers.blockLastTurn>0) {
                        if (!r.usedThisTurn) r.beginLongPulse();
                    }
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class,"hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}


