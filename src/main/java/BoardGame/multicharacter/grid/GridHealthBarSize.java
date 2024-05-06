package BoardGame.multicharacter.grid;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.dungeons.AbstractBGDungeon;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GridHealthBarSize {
    //TODO: also healthBarRevivedEvent

    @SpirePatch(
            clz=AbstractCreature.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<Float> originalHbWidth = new SpireField<>(()->0f);
    }

    //@SpirePatch2(clz = AbstractCreature.class, method = "healthBarUpdatedEvent",
    @SpirePatch2(clz = AbstractCreature.class, method = "renderHealth",
            paramtypez = {SpriteBatch.class})
    public static class HealthUpdatePrefix {
        @SpirePrefixPatch
        private static void Foo(AbstractCreature __instance) {
            Field.originalHbWidth.set(__instance,__instance.hb.width);
            //cancel if no tile exists
            if(GridTile.Field.gridTile.get(__instance)==null)return;
            //cancel if enemy is on floor rather than grid
            if(GridTile.Field.tileLerpTarget.get(__instance)==0)return;
            //set hb size to smaller than grid, (only) if larger than grid
            if(true){
                __instance.hb.width=0.9f*GridTile.Field.gridTile.get(__instance).width* Settings.scale;
                //healthBarUpdatedEvent without healthBarAnimTimer
                ReflectionHacks.setPrivate(__instance,AbstractCreature.class,"targetHealthBarWidth",__instance.hb.width * __instance.currentHealth / __instance.maxHealth);
                if (__instance.maxHealth == __instance.currentHealth) {
                    ReflectionHacks.setPrivate(__instance,AbstractCreature.class,"healthBarWidth",ReflectionHacks.getPrivate(__instance,AbstractCreature.class,"targetHealthBarWidth"));
                } else if (__instance.currentHealth == 0) {
                    ReflectionHacks.setPrivate(__instance,AbstractCreature.class,"healthBarWidth",0.0f);
                    ReflectionHacks.setPrivate(__instance,AbstractCreature.class,"targetHealthBarWidth",0.0f);
                }
                if ((float)ReflectionHacks.getPrivate(__instance,AbstractCreature.class,"targetHealthBarWidth") > (float)ReflectionHacks.getPrivate(__instance,AbstractCreature.class,"healthBarWidth"))
                    ReflectionHacks.setPrivate(__instance,AbstractCreature.class,"healthBarWidth", ReflectionHacks.getPrivate(__instance,AbstractCreature.class,"targetHealthBarWidth"));
            }
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "renderHealth",
            paramtypez = {SpriteBatch.class})
    public static class HealthUpdatePostfix {
        @SpirePostfixPatch
        private static void Foo(AbstractCreature __instance) {
            //restore hb size
            __instance.hb.width=Field.originalHbWidth.get(__instance);
        }
    }

//    @SpirePatch2(clz = AbstractCreature.class, method = "renderHealth",
//            paramtypez = {SpriteBatch.class})
//    public static class HealthPositionInsert {
//        @SpireInsertPatch(
//                locator = Locator.class,
//                localvars = {"y"}
//        )
//        public static void Insert(AbstractCreature __instance, @ByRef float[] ___y) {
//            //TODO: cancel if creature is not in grid tile
//            ___y[0]+=20*Settings.scale;
//        }
//        private static class Locator extends SpireInsertLocator {
//            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "renderHealthBg");
//                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
//            }
//        }
//    }


}
