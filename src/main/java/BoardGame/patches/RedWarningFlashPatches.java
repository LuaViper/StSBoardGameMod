package BoardGame.patches;

import BoardGame.characters.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class RedWarningFlashPatches {
    @SpirePatch2(clz = AbstractPlayer.class, method = "damage",
            paramtypez={DamageInfo.class})
    public static class Foo {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"damageAmount"}
        )
        public static void Bar(AbstractPlayer __instance, int damageAmount) {
            if(__instance instanceof AbstractBGPlayer) {
                //if(damageAmount>0) {  //following the switch to InsertPatch, we're already inside this if block
                    if (__instance.currentHealth > 1) { //if currenthealth was exactly 1, then we've already flashed
                        //if(__instance.maxHealth>6) {    //if player has mark of pain, never mind, maybe
                        //if (__instance.currentHealth <= 3) {
                        if (__instance.currentHealth <= 2) {
                            AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)));
                        }
                        //}
                    }
                //}
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "healthBarUpdatedEvent");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}
