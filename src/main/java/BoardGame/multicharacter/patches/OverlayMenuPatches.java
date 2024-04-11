package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class OverlayMenuPatches {
    @SpirePatch2(clz = OverlayMenu.class, method = "update")
    public static class OverlayMenuUpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(){
            if(AbstractDungeon.player instanceof BGMultiCharacter){
                for(AbstractPlayer p : BGMultiCharacter.getSubcharacters()){
                    p.hand.update();
                }
            }
        }
    }


    private static final float ENERGY_ORB_SPACING=85F;
    @SpirePatch2(clz = OverlayMenu.class, method = "render", paramtypez={SpriteBatch.class})
    public static class OverlayMenuRenderPatch {
        @SpireInsertPatch(
                locator = OverlayMenuPatches.OverlayMenuRenderPatch.Locator.class,
                localvars = {}
        )
        public static void Postfix(OverlayMenu __instance, SpriteBatch sb){
            if(AbstractDungeon.player instanceof BGMultiCharacter){
                //energyPanel.render four times, drawn top to bottom
                for (AbstractPlayer c : BGMultiCharacter.getSubcharacters()) {
                    __instance.energyPanel.current_y += ENERGY_ORB_SPACING * Settings.scale;
                }
                for (int i = BGMultiCharacter.getSubcharacters().size() - 1; i >= 0; i -= 1) {
                    __instance.energyPanel.current_y -= ENERGY_ORB_SPACING * Settings.scale;
                    ContextPatches.pushPlayerContext(BGMultiCharacter.getSubcharacters().get(i));
                    __instance.energyPanel.render(sb);
                    ContextPatches.popPlayerContext();
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(EnergyPanel.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}
