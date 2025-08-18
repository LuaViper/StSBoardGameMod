package BoardGame.patches.bestiary;

import Bestiary.ui.Label;
import Bestiary.utils.RenderingUtils;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.mod.stslib.patches.TipBoxCustomIcons;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

public class RenderingPatches {


//    @SpirePatch(clz = ExtraColors.class, method = "stringToColor",
//            paramtypez = {String.class})
//    public static class ColorPatch {
//        @SpirePrefixPatch
//        public static SpireReturn<Color> Foo(String s) {
//            if(s.equals("CREAM")){
//                return SpireReturn.Return(Settings.CREAM_COLOR);
//            }
//            return SpireReturn.Continue();
//        }
//    }


        @SpirePatch(clz = Label.class, method = "render",
            paramtypez = {SpriteBatch.class},requiredModId="ojb_Bestiary")
    public static class LabelIconPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Foo(Label __instance, SpriteBatch sb) {
            FontHelper.renderSmartText(sb,
                    ReflectionHacks.getPrivate(__instance, Label.class,"font"),
                    ReflectionHacks.getPrivate(__instance, Label.class,"text"),
                    (float)ReflectionHacks.getPrivate(__instance, Label.class,"x")* Settings.scale,
                    (float)ReflectionHacks.getPrivate(__instance, Label.class,"y")* Settings.scale,
                    ReflectionHacks.getPrivate(__instance, Label.class,"color"));
            return SpireReturn.Return();
            //return SpireReturn.Continue();
        }
    }

//    @SpirePatch(clz = SmartLabel.class, method = "render",
//            paramtypez = {SpriteBatch.class})
//    public static class DescIconPatch {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> Foo(SmartLabel __instance, SpriteBatch sb) {
//            FontHelper.renderSmartText(sb,
//                    ReflectionHacks.getPrivate(__instance, Label.class,"font"),
//                    ReflectionHacks.getPrivate(__instance, Label.class,"text"),
//                    (float)ReflectionHacks.getPrivate(__instance, Label.class,"x")* Settings.scale,
//                    (float)ReflectionHacks.getPrivate(__instance, Label.class,"y")* Settings.scale,
//                    ReflectionHacks.getPrivate(__instance, Label.class,"color"));
//            return SpireReturn.Return();
//            //return SpireReturn.Continue();
//        }
//    }


    @SpirePatch(clz = RenderingUtils.class, method = "renderSmartText", paramtypez = {SpriteBatch.class, BitmapFont.class, String.class, float.class, float.class, float.class, float.class, Color.class},requiredModId="ojb_Bestiary")
    public static class FontHelpFixes {
        @SpireInsertPatch(locator = Locator.class, localvars = {"word"})
        public static void DrawIconsPls(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor, @ByRef float[] ___curWidth, @ByRef float[] ___curHeight, @ByRef String[] word) {
            if (word[0].length() > 0 && word[0].charAt(0) == '[') {
                String key = word[0].trim();
                if (key.endsWith("Icon]"))
                    key = key.replace("*d", "D").replace("*b", "B").replace("*m", "M");
                AbstractCustomIcon icon = CustomIconHelper.getIcon(key);
                if (icon != null) {
                    if (___curWidth[0] + (float)ReflectionHacks.getPrivateStatic(TipBoxCustomIcons.class,"CARD_ENERGY_IMG_WIDTH")
                            > lineWidth) {
                        ___curHeight[0] = ___curHeight[0] - lineSpacing;
                        icon.render(sb, x, y + ___curHeight[0], icon.region.getRegionWidth() / 2.0F, -icon.region.getRegionHeight() / 4.0F, Settings.scale, 0.0F);
                        ___curWidth[0] = (float)ReflectionHacks.getPrivateStatic(TipBoxCustomIcons.class,"CARD_ENERGY_IMG_WIDTH");
                    } else {
                        icon.render(sb, x + ___curWidth[0], y + ___curHeight[0], icon.region.getRegionWidth() / 2.0F, -icon.region.getRegionHeight() / 4.0F, Settings.scale, 0.0F);
                        ___curWidth[0] = ___curWidth[0] + (float)ReflectionHacks.getPrivateStatic(TipBoxCustomIcons.class,"CARD_ENERGY_IMG_WIDTH");
                    }
                    word[0] = "";
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(RenderingUtils.class, "identifyColor");
                return LineFinder.findInOrder(ctBehavior, (Matcher)methodCallMatcher);
            }
        }
    }


}
