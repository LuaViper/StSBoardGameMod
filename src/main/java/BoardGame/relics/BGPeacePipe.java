//Peace Pipe is NOT hardcoded
//but BGPeacePipe will probably need overrides to RestOption constructor and CampfireSleepEffect constructor
//and very likely add a skip button to remove-a-card

package BoardGame.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireTokeEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGPeacePipe
        extends AbstractBGRelic  {
    public static final String ID = "BGPeace Pipe";

    public BGPeacePipe() {
        super("BGPeace Pipe", "peacePipe.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
    }

    public int getPrice() {return 8;}
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public static boolean menuActive=false;




    public AbstractRelic makeCopy() {
        return new BGPeacePipe();
    }

    static final Logger logger = LogManager.getLogger(BGPeacePipe.class.getName());

    @SpirePatch2(clz = RestOption.class, method = "useOption",
            paramtypez = {})
    public static class PeacePipeCampfirePatch {
        @SpirePostfixPatch
        public static void Postfix(){
            if(AbstractDungeon.player.hasRelic("BGPeace Pipe")) {
                AbstractDungeon.effectList.add(new CampfireTokeEffect());
                logger.info("mark Peace Pipe as active");
                BGPeacePipe.menuActive=true;
            }
        }
    }



    @SpirePatch2(clz = CancelButton.class, method = "update",
            paramtypez = {})
    public static class CancelButtonDontReopenCampfireUIPatch {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(){
            logger.info("Don't Reopen Campfire UI Patch");
            if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                if(BGPeacePipe.menuActive){
                    AbstractDungeon.closeCurrentScreen();
                    AbstractDungeon.dungeonMapScreen.open(false);
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CampfireUI.class,"reopen");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

    }


    @SpirePatch2(clz = CampfireTokeEffect.class, method = "update",
            paramtypez = {})
    public static class CampfireTokeEffectTypecastingPatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(){
            logger.info("mark Peace Pipe as inactive");
            BGPeacePipe.menuActive=false;
            if(!(AbstractDungeon.getCurrRoom() instanceof RestRoom)){
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(RestRoom.class,"cutFireSound");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}


