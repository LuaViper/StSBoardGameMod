package BoardGame.multicharacter.grid;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.patches.ContextPatches;
import BoardGame.multicharacter.patches.OverlayMenuPatches;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GridHealthBarPosition {

    ////
    //This patch affects the drawn position of the health bar, but not its hitbox
    ////
    @SpirePatch2(clz= AbstractCreature.class,method="renderHealth",paramtypez={SpriteBatch.class})
    public static class RenderHealthPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"x","y"}
        )
        public static void Foo(AbstractCreature __instance, SpriteBatch sb,
                              @ByRef float[] x, @ByRef float[] y ) {
            //cancel if no tile exists
            if(GridTile.Field.gridTile.get(__instance)==null)return;
            //cancel if enemy is on floor rather than grid
            if(GridTile.Field.tileLerpTarget.get(__instance)==0)return;

            y[0]=y[0]+25*Settings.scale;
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "renderHealthBg");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    ////
    //This patch affects the position of the health bar hitbox, which affects where the monster name is drawn
    ////
//    @SpirePatch2(clz = AbstractMonster.class, method = "updateHitbox",
//            paramtypez = {float.class,float.class,float.class,float.class})
//    public static class updateHitboxPatch {
//        @SpirePostfixPatch
//        private static void Foo(AbstractMonster __instance) {
//            //cancel if no tile exists
//            if(GridTile.Field.gridTile.get(__instance)==null)return;
//            //cancel if enemy is on floor rather than grid
//            if(GridTile.Field.tileLerpTarget.get(__instance)==0)return;
//
//            float offset=75.0f*Settings.scale;
//            __instance.healthHb.move(__instance.hb.cX, offset+__instance.hb.cY - __instance.hb_h / 2.0F - __instance.healthHb.height / 2.0F);
//
//        }
//    }


}
