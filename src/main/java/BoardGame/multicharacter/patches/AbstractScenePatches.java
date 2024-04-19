package BoardGame.multicharacter.patches;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.multicharacter.MultiCharacterRowBoxes;
import BoardGame.multicharacter.grid.GridBackground;
import BoardGame.relics.AbstractBGRelic;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class AbstractScenePatches {

    private static Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    @SpirePatch(clz= AbstractScene.class, method=SpirePatch.CLASS)
    public static class AbstractSceneExtraInterface
    {
        public static SpireField<GridBackground> gridBackground = new SpireField<>(()->new GridBackground());
    }

    @SpirePatch2(clz= AbstractScene.class, method="update",
            paramtypez={})
    public static class AbstractSceneDiceInterfaceUpdatePatch
    {
        @SpirePostfixPatch
        public static void Foo(AbstractScene __instance){
            //logger.info("AbstractSceneDiceInterfaceUpdatePatch postfix");
            AbstractSceneExtraInterface.gridBackground.get(__instance).update();

        }
    }

    @SpirePatch2(clz=AbstractDungeon.class, method="render",
            paramtypez={SpriteBatch.class})
    public static class AbstractSceneDiceInterfaceRenderPatch
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Foo(AbstractDungeon __instance, SpriteBatch sb) {
            AbstractSceneExtraInterface.gridBackground.get(AbstractDungeon.scene).render(sb);
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "effectList");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

//AbstractScene does not have a show/hide combat panels method!
//    @SpirePatch2(clz= AbstractScene.class, method="showCombatPanels",
//            paramtypez={})
//    public static class showCombatPanelsPatch
//    {
//        @SpirePostfixPatch
//        public static void Postfix(AbstractScene __instance){
//            AbstractSceneExtraInterface.gridBackground.get(__instance).visible=true;
//
//        }
//    }
//
//    @SpirePatch2(clz= AbstractScene.class, method="hideCombatPanels",
//            paramtypez={})
//    public static class hideCombatPanelsPatch
//    {
//        @SpirePostfixPatch
//        public static void Postfix(AbstractScene __instance){
//            AbstractSceneExtraInterface.gridBackground.get(__instance).visible=false;
//
//        }
//    }
}
