package BoardGame.patches.bestiary;

import Bestiary.database.MonsterInfo;
import Bestiary.ui.MonsterInfoRenderHelper;
import BoardGame.screen.OrbSelectScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class OverlayPatches {
    @SpirePatch2(clz= MonsterInfoRenderHelper.class,method="setCurrMonster",paramtypez={MonsterInfo.class})
    public static class CreateLabelsPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Foo(AbstractDungeon __instance) {
            Logger logger = LogManager.getLogger(OrbSelectScreen.class.getName());
            //logger.info("Dungeon Update: " + AbstractDungeon.isScreenUp);
            if (__instance.screen.equals(OrbSelectScreen.Enum.ORB_SELECT)) {
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
}
