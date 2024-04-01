
//TODO: don't show screen if there are less than 2 valid targets
//TODO: close screen if combat ends (otherwise cursor becomes invisible until map)
//TODO: extra safeguard for Juggernaut: once screen fades out, start autoclicking (otherwise screen stays black until player clicks several more times)

package BoardGame.screen;

import BoardGame.BoardGame;
import BoardGame.actions.BGSpawnTwoGremlinsForGremlinLeaderAction;
import BoardGame.relics.BGIceCream;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class TargetSelectScreen extends CustomScreen {

    public boolean isDone=false;
    public interface TargetSelectAction{
        void execute(AbstractMonster target);
    }
    final Logger logger = LogManager.getLogger(TargetSelectScreen.class.getName());
    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen TARGET_SELECT;
    }
    @Override
    public AbstractDungeon.CurrentScreen curScreen()
    {
        return Enum.TARGET_SELECT;
    }
    public TargetSelectAction action;
    public String description="(DNT) Target Select Screen.  Choose a target.";
    public boolean allowCancel=false;
    public TargetSelectAction cancelAction=null;    //dummied out
    public AbstractMonster finaltarget=null;
    private void open(TargetSelectAction action, String description, boolean allowCancel) {
        this.description=description;
        this.action=action;
        this.allowCancel=allowCancel;
        this.isDone=false;
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
        //logger.info("CLOSE TARGETSELECTSCREEN "+AbstractDungeon.screen+" "+AbstractDungeon.previousScreen);
        genericScreenOverlayReset();
        //AbstractDungeon.player.isUsingClickDragControl = false;
        AbstractDungeon.player.inSingleTargetMode = false;
        GameCursor.hidden = false;
        //___hoveredMonster[0] = null;
    }
    @Override
    public void update() {
        //logger.info("TSS: update");
        if(!((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT)) {
            isDone=true;
            AbstractDungeon.closeCurrentScreen();
            return;
        }
        AbstractMonster firstmonster=null;
        int monstercount=0;
        for(AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters){
            if(!m.isDeadOrEscaped()){
                monstercount+=1;
                if(monstercount==1) firstmonster=m;
            }
        }
        if(monstercount==1){
           // logger.info("TSS: just one monster left: "+firstmonster);
            if(!isDone) {
                //logger.info("aaaaaand go");
                isDone = true;
                ((TargetSelectScreen) BaseMod.getCustomScreen(Enum.TARGET_SELECT)).action.execute(firstmonster);
            }
            AbstractDungeon.closeCurrentScreen();
            return;
        }else if(monstercount==0){
            isDone=true;
            AbstractDungeon.closeCurrentScreen();
            return;
        }
    }

    @Override
    public void render(SpriteBatch sb){
        //FontHelper.renderDeckViewTip(sb, this.description, 96.0F * Settings.scale, Settings.CREAM_COLOR);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.description, (Settings.WIDTH / 2), Settings.HEIGHT - 180.0F * Settings.scale, Settings.CREAM_COLOR);
    }

    @SpirePatch2(clz= AbstractDungeon.class,method="update",paramtypez={})
    public static class DungeonUpdatePatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Insert(AbstractDungeon __instance) {
            Logger logger = LogManager.getLogger(TargetSelectScreen.class.getName());
            //logger.info("Dungeon Update: " + AbstractDungeon.isScreenUp);
            if (__instance.screen.equals(Enum.TARGET_SELECT)) {
                //TODO: maybe move some of these to update?
                __instance.overlayMenu.hideBlackScreen();
                AbstractDungeon.player.inSingleTargetMode=true;
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
            Logger logger = LogManager.getLogger(TargetSelectScreen.class.getName());
            //logger.info("Insert: "+AbstractDungeon.isScreenUp);
            if (AbstractDungeon.isScreenUp) {
                if(AbstractDungeon.screen.equals(Enum.TARGET_SELECT)){
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

    @SpirePatch2(clz= AbstractPlayer.class,method="renderTargetingUi",paramtypez={SpriteBatch.class})
    public static class CardlessTargetingArrowPatch{
        @SpirePrefixPatch public static SpireReturn<Void> Insert(AbstractPlayer __instance, SpriteBatch sb,
                                                                 @ByRef float[] ___arrowX, @ByRef float[] ___arrowY, Vector2 ___controlPoint, AbstractMonster ___hoveredMonster,
                                                                 @ByRef float[] ___arrowScale, @ByRef float[] ___arrowScaleTimer, Color ___ARROW_COLOR, Vector2 ___arrowTmp,
                                                                 Vector2 ___startArrowVector, Vector2 ___endArrowVector){

            if(__instance.hoveredCard==null){
                //TODO: implement support for orb arrows and relic arrows, maybe
                ___arrowX[0] = MathHelper.mouseLerpSnap(___arrowX[0], InputHelper.mX);
                ___arrowY[0] = MathHelper.mouseLerpSnap(___arrowY[0], InputHelper.mY);
                ___controlPoint.x = __instance.hb.cX - (___arrowX[0] - __instance.hb.cX) / 4.0F;
                ___controlPoint.y = ___arrowY[0] + (___arrowY[0] - __instance.hb.cY) / 2.0F;
                Logger logger = LogManager.getLogger(TargetSelectScreen.class.getName());
                //logger.info("hoveredMonster: "+___hoveredMonster);
                if (___hoveredMonster == null) {
                    ___arrowScale[0] = Settings.scale;
                    ___arrowScaleTimer[0] = 0.0F;
                    sb.setColor(Color.WHITE);
                } else {
                    ___arrowScaleTimer[0] += Gdx.graphics.getDeltaTime();
                    if (___arrowScaleTimer[0] > 1.0F) {
                        ___arrowScaleTimer[0] = 1.0F;
                    }

                    ___arrowScale[0] = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, ___arrowScaleTimer[0]);
                    sb.setColor(___ARROW_COLOR);
                }


                ___controlPoint.x=(__instance.hb.cX+___arrowX[0])/2;
                ___controlPoint.y=Settings.HEIGHT;
                ___arrowTmp.nor();

                ___startArrowVector.x = __instance.hb.cX;
                ___startArrowVector.y = __instance.hb.cY;
                ___endArrowVector.x = ___arrowX[0];
                ___endArrowVector.y = ___arrowY[0];


                ReflectionHacks.RMethod drawcurvedline=ReflectionHacks.privateMethod(AbstractPlayer.class,"drawCurvedLine",
                    SpriteBatch.class,Vector2.class,Vector2.class,Vector2.class);
                drawcurvedline.invoke(__instance, sb, ___startArrowVector, ___endArrowVector, ___controlPoint);

                sb.draw(ImageMaster.TARGET_UI_ARROW, ___arrowX[0] - 128.0F, ___arrowY[0] - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, ___arrowScale[0], ___arrowScale[0], ___arrowTmp
                        .angle() + 90.0F, 0, 0, 256, 256, false, false);

                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


//    @SpirePatch2(clz= AbstractPlayer.class,method="updateSingleTargetInput",paramtypez={})
//    public static class temp{
//        @SpirePostfixPatch public static SpireReturn<Void> Prefix(AbstractMonster ___hoveredMonster){
//            Logger logger = LogManager.getLogger(TargetSelectScreen.class.getName());
//            //logger.info("updateinput: "+___hoveredMonster);
//            return SpireReturn.Continue();
//        }
//    }

    @SpirePatch2(clz= AbstractPlayer.class,method="updateSingleTargetInput",paramtypez={})
    public static class UpdateSingleTargetInputPatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(AbstractPlayer __instance, @ByRef AbstractMonster[] ___hoveredMonster,
                                               float ___hoverStartLine, @ByRef boolean[] ___isUsingClickDragControl){
            if(AbstractDungeon.screen.equals(Enum.TARGET_SELECT)){
                if (__instance.isInKeyboardMode) {
                    //if (InputActionSet.releaseCard.isJustPressed() || CInputActionSet.cancel.isJustPressed()) {
                    if(false){ //TODO: check getCustomScreen(TargetSelectScreen).allowCancel
                        __instance.inSingleTargetMode = false;
                        ___hoveredMonster[0] = null;
                        AbstractDungeon.closeCurrentScreen();
                    } else {
                        ReflectionHacks.RMethod updateTargetArrowWithKeyboard=ReflectionHacks.privateMethod(AbstractPlayer.class,"updateTargetArrowWithKeyboard",
                                Boolean.class);
                        updateTargetArrowWithKeyboard.invoke(__instance, true);
                    }
                } else {
                    ___hoveredMonster[0] = null;
                    for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                        m.hb.update();
                        //currently, the only card that allowsCancel is Carve Reality, which targets "one or two" enemies
                        //so we're allowing the cancel by clicking the same monster twice -- even if the first hit kills it
                        //TODO: this DOES mean that the player can pick a dead enemy as their second target, regardless of whether it was the first target.  fix maybe.
                        //TODO: maybe pass a 4th arg along with AllowCancel being the originally-targeted enemy?
                        TargetSelectScreen screen=(TargetSelectScreen)BaseMod.getCustomScreen(Enum.TARGET_SELECT);
                        if (m.hb.hovered && ((!m.isDying && !m.isEscaping && m.currentHealth > 0) || screen.allowCancel)) {
                            ___hoveredMonster[0] = m;
                            break;
                        }
                    }
                }

                //if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead() || InputHelper.justClickedRight || InputHelper.mY < 50.0F * Settings.scale || InputHelper.mY < ___hoverStartLine - 400.0F * Settings.scale) {
                if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead()) {
                    if (Settings.isTouchScreen) {
                        InputHelper.moveCursorToNeutralPosition();
                    }
                    ReflectionHacks.RMethod releaseCard=ReflectionHacks.privateMethod(AbstractPlayer.class,"updateTargetArrowWithKeyboard",
                            Boolean.class);
                    releaseCard.invoke(__instance);
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    ___isUsingClickDragControl[0] = false;
                    __instance.inSingleTargetMode = false;
                    GameCursor.hidden = false;
                    ___hoveredMonster[0] = null;
                    AbstractDungeon.closeCurrentScreen();
                    return SpireReturn.Return();
                }

                //skip over "cardFromHotkey" entirely...

                if (InputHelper.justClickedLeft || InputActionSet.confirm.isJustPressed() || CInputActionSet.select
                        .isJustPressed()) {
                    InputHelper.justClickedLeft = false;

                    if (___hoveredMonster[0] == null) {
                        CardCrawlGame.sound.play("UI_CLICK_1");
                        return SpireReturn.Return();
                    }

                    //execute effect here
                    TargetSelectScreen screen=(TargetSelectScreen)BaseMod.getCustomScreen(Enum.TARGET_SELECT);
                    if (!screen.isDone) {
                        screen.isDone=true;
                        screen.action.execute(___hoveredMonster[0]);
                    }


                    ___isUsingClickDragControl[0] = false;
                    __instance.inSingleTargetMode = false;
                    GameCursor.hidden = false;
                    ___hoveredMonster[0] = null;
                    AbstractDungeon.closeCurrentScreen();

                    return SpireReturn.Return();
                }

//                TargetSelectScreen screen=(TargetSelectScreen)BaseMod.getCustomScreen(Enum.TARGET_SELECT);
//                if(screen.allowCancel) {
//                    if (InputHelper.justClickedRight || InputActionSet.cancel.isJustPressed()) {
//                        CardCrawlGame.sound.play("UI_CLICK_1");
//                        if (!screen.isDone) {
//                            screen.isDone = true;
////                            if (screen.cancelAction != null) {
////                                screen.cancelAction.execute(___hoveredMonster[0]);
////                            }
//                        }
//                        ___isUsingClickDragControl[0] = false;
//                        __instance.inSingleTargetMode = false;
//                        GameCursor.hidden = false;
//                        ___hoveredMonster[0] = null;
//                        AbstractDungeon.closeCurrentScreen();
//                    }
//                }

                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class,"isInKeyboardMode");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


}
