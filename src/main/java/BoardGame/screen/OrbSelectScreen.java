
//TODO: don't show screen if there are less than 2 valid targets
//TODO: close screen if combat ends (otherwise cursor becomes invisible until map)
//TODO: extra safeguard for Juggernaut: once screen fades out, start autoclicking (otherwise screen stays black until player clicks several more times)

package BoardGame.screen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Objects;


public class OrbSelectScreen extends CustomScreen {

    public boolean isDone=false;
    public interface OrbSelectAction{
        void execute(int orbslot);
    }

    //public static boolean PAUSE_ACTION_QUEUE=false;

    final Logger logger = LogManager.getLogger(OrbSelectScreen.class.getName());
    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen ORB_SELECT;
    }
    @Override
    public AbstractDungeon.CurrentScreen curScreen()
    {
        return Enum.ORB_SELECT;
    }


    public OrbSelectAction action;

    public String description="Orb Select Screen.  Choose an Orb.";
    public boolean allowCancel=false;       //dummied out
    public OrbSelectAction cancelAction=null;    //dummied out
    public AbstractMonster finaltarget=null;


    private void open(OrbSelectAction action, String description) {
        this.description=description;
        this.action=action;
        //this.allowCancel=allowCancel;
        //this.cancelAction=cancelAction;
        this.isDone=false;

        //OrbSelectScreen.PAUSE_ACTION_QUEUE=true;

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE)
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        reopen();
    }


    @Override
    public void reopen()
    {
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.isScreenUp = true;
    }

    @Override
    public void openingSettings()
    {
        // Required if you want to reopen your screen when the settings screen closes
        AbstractDungeon.previousScreen = curScreen();
    }

    @Override public void close()
    {
       // OrbSelectScreen.PAUSE_ACTION_QUEUE=false;

        //logger.info("CLOSE TARGETSELECTSCREEN "+AbstractDungeon.screen+" "+AbstractDungeon.previousScreen);
        genericScreenOverlayReset();
        //AbstractDungeon.player.isUsingClickDragControl = false;
        AbstractDungeon.player.inSingleTargetMode = false;
        GameCursor.hidden = false;
        //___hoveredMonster[0] = null;
    }


    @Override
    public void update() {
        //logger.info("OSS: update");
        if(!((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT)) {
            isDone=true;
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        boolean moreThanOneOrbType=false;
        String lastOrbID=null;
        for(AbstractOrb o : AbstractDungeon.player.orbs){
            if(!(o instanceof EmptyOrbSlot)) {
                if (!Objects.equals(o.ID, "Empty")) {
                    if (!Objects.equals(o.ID, lastOrbID)) {
                        if (lastOrbID != null) {
                            moreThanOneOrbType = true;
                            break;
                        }
                        lastOrbID = o.ID;
                    }
                }
            }
        }

        if(AbstractDungeon.player.orbs.isEmpty()){
            isDone=true;
            AbstractDungeon.closeCurrentScreen();
            return;
        }else if(!moreThanOneOrbType){
            if(!isDone) {
                isDone = true;
                ((OrbSelectScreen) BaseMod.getCustomScreen(Enum.ORB_SELECT)).action.execute(0);
            }
            AbstractDungeon.closeCurrentScreen();
            return;
        }
    }

    @Override
    public void render(SpriteBatch sb){
        FontHelper.renderDeckViewTip(sb, this.description, 96.0F * Settings.scale, Settings.CREAM_COLOR);

    }

    @SpirePatch2(clz= AbstractDungeon.class,method="update",paramtypez={})
    public static class DungeonUpdatePatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Insert(AbstractDungeon __instance) {
            Logger logger = LogManager.getLogger(OrbSelectScreen.class.getName());
            //logger.info("Dungeon Update: " + AbstractDungeon.isScreenUp);
            if (__instance.screen.equals(Enum.ORB_SELECT)) {
                //TODO: maybe move some of these to update?
                __instance.overlayMenu.hideBlackScreen();
                //AbstractDungeon.player.inSingleTargetMode=true;
                //GameCursor.hidden = true;     //TODO: hide cursor once we're sure we can unhide it consistently
                __instance.currMapNode.room.update();
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class,"isScreenUp");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
    @SpirePatch2(clz= AbstractRoom.class,method="update",paramtypez={})
    public static class RoomInputPatch{
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static void Insert(AbstractRoom __instance) {
            Logger logger = LogManager.getLogger(OrbSelectScreen.class.getName());
            //logger.info("Insert: "+AbstractDungeon.isScreenUp);
            if (AbstractDungeon.isScreenUp) {
                if(AbstractDungeon.screen.equals(Enum.ORB_SELECT)){
                    if (!__instance.monsters.areMonstersBasicallyDead() && AbstractDungeon.player.currentHealth > 0) {
                        AbstractDungeon.player.updateInput();
                    }
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.CurrentScreen.class,"equals");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }



//    @SpirePatch2(clz= AbstractPlayer.class,method="updateSingleTargetInput",paramtypez={})
//    public static class temp{
//        @SpirePostfixPatch public static SpireReturn<Void> Prefix(AbstractMonster ___hoveredMonster){
//            Logger logger = LogManager.getLogger(OrbSelectScreen.class.getName());
//            //logger.info("updateinput: "+___hoveredMonster);
//            return SpireReturn.Continue();
//        }
//    }

    @SpirePatch2(clz= AbstractPlayer.class,method="updateInput",paramtypez={})
    public static class UpdateInputPatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(AbstractPlayer __instance){
            if(!AbstractDungeon.screen.equals(Enum.ORB_SELECT)){
                return SpireReturn.Continue();
            }
            if( InputHelper.justClickedLeft || InputActionSet.confirm.isJustPressed() || CInputActionSet.select.isJustPressed()) {
                //for(AbstractOrb o : __instance.orbs){
                for (int i=0;i<__instance.orbs.size();i+=1){
                    AbstractOrb o = __instance.orbs.get(i);
                    if(o.hb.hovered && !(o instanceof EmptyOrbSlot)){
                        //execute effect here
                        OrbSelectScreen screen=(OrbSelectScreen)BaseMod.getCustomScreen(OrbSelectScreen.Enum.ORB_SELECT);
                        if (!screen.isDone) {
                            screen.isDone=true;
                            screen.action.execute(i);
                        }
                        GameCursor.hidden = false;
                        AbstractDungeon.closeCurrentScreen();

                    }
                }
            }

            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class,"inSingleTargetMode");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


}
