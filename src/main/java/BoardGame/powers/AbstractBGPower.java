package BoardGame.powers;

import BoardGame.BoardGame;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

//at the moment, we're only using this class as a way to implement clickable powers
// Note that some cards use vanilla powers instead of BG powers and won't extend this class
//TODO: perhaps a warning on the mod page that controllers aren't supported, mouse cursor is required?
public class AbstractBGPower extends AbstractPower {

    //TODO: clickbox is not entirely correct size
    private static final float iconsize=48*1.17F;
    public Hitbox hb;

    public static final Logger logger = LogManager.getLogger(AbstractBGPower.class.getName());
    public AbstractBGPower(){
        super();
        hb=new Hitbox(iconsize,iconsize);
        //logger.info("AbstractBGPower.hb created");
    }



    public boolean clickable=false;
    public boolean onCooldown=false;
    public boolean autoActivate=false;
    public boolean isCurrentlyPlayerTurn=true; //TODO: this will break if Mayhem applies a power at end of turn
     public void onRightClick(){
        //override
     }
    public void onShuffle(){
         //override
    }

     public void atStartOfTurnPostDraw(){
         super.atStartOfTurnPostDraw();
         isCurrentlyPlayerTurn=true;
         onCooldown=false;
         updateDescription();
     }
    public void atEndTurnQueued(){
         //TODO: is this correct, or does it need to move to preendofturncards?  (note that this is a custom event called during endturnpatch)
        if (autoActivate) {
            onRightClick();
        }
        updateDescription();
    }

    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);
        isCurrentlyPlayerTurn=false;
        updateDescription();
    }



    @SpirePatch2(clz= AbstractPlayer.class, method="updateInput",
            paramtypez={})
    public static class EndTurnPatch{
        @SpireInsertPatch(
                locator= AbstractBGPower.EndTurnPatch.Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> updateInput(AbstractPlayer __instance) {
            //BoardGame.logger.info("EndTurn is queued, run a BG power check...");
            for(AbstractPower power : __instance.powers){
                if(power instanceof AbstractBGPower){
                    ((AbstractBGPower)power).atEndTurnQueued();
                }
            }
            //BoardGame.logger.info("BG power check is done, call actionManager.update...");
            AbstractDungeon.actionManager.update();
            //BoardGame.logger.info("Now look at the action list again...");
            if (AbstractDungeon.actionManager.cardQueue.isEmpty() && !AbstractDungeon.actionManager.hasControl) {
                //BoardGame.logger.info("Actions are empty, proceed");
                return SpireReturn.Continue();
            }
            else {
                //BoardGame.logger.info("Queue is not empty, abort");
                return SpireReturn.Return();
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class,"endTurnQueued");
                return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[1]};
            }
        }
    }

     public String getRightClickDescriptionText(){
        if(!clickable)
         return "";
        else if (onCooldown)
            return " (On cooldown.)";
        else if (!autoActivate)
            return " #bRight-click to activate.";
        else
            return " #bRight-click to activate. (Auto-activates at end of turn.)";

     }

    public void update(int slot) {
         //TODO: can we add a tooltip like with FakeTradingRelic to make it clear when the hitbox is mousedover?
         super.update(slot);

        hb.update();
        if(clickable) {
            if (hb.hovered) {
                //logger.info("AbstractBGPower.hb hovered");
                if (InputHelper.justClickedRight) {
                    //logger.info("AbstractBGPower.hb clicked");
                    if (isCurrentlyPlayerTurn && !onCooldown) {
                        for (AbstractRelic relic : AbstractDungeon.player.relics) {
                            if (relic instanceof BGTheDieRelic) {
                                TheDie.forceLockInRoll = true;
                                ((BGTheDieRelic) relic).lockRollAndActivateDieRelics();
                            }
                        }
                        //TODO: also call all AbstractBGPower.onRightclickPower (NYI) to trigger wildcard powers
                        onRightClick();
                        updateDescription();

                    }
                }
            }
        }
    }

     public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb,x,y,c);
         hb.move(x - this.region48.packedWidth / 2.0F, y - this.region48.packedHeight / 2.0F);
     }


     public void onAboutToUseCard(AbstractCard c,AbstractCreature target){}
}
