package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.MultiCharacter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//TODO: option to have energy display rotate along with hands

public class OverlayMenuPatches {
    @SpirePatch2(clz = OverlayMenu.class, method = "update")
    public static class OverlayMenuUpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(){
            if(AbstractDungeon.player instanceof MultiCharacter){
                for(AbstractPlayer p : MultiCharacter.getSubcharacters()){
                    p.hand.update();
                }
            }
        }
    }


    //TODO: this causes the "fire nova" effect around the energy bubble to be skipped
    // -- do we want to try to restore it, even if it means drawing 4 at once?
    public static void renderSubcharacterEnergyInstead(OverlayMenu __instance, SpriteBatch sb){
        if(AbstractDungeon.player instanceof MultiCharacter){
            //energyPanel.render four times, drawn top to bottom
            for (AbstractPlayer c : MultiCharacter.getSubcharacters()) {
                __instance.energyPanel.current_y += ENERGY_ORB_SPACING * Settings.scale;
            }
            for (int i = MultiCharacter.getSubcharacters().size() - 1; i >= 0; i -= 1) {
                __instance.energyPanel.current_y -= ENERGY_ORB_SPACING * Settings.scale;
                ContextPatches.pushPlayerContext(MultiCharacter.getSubcharacters().get(i));
                __instance.energyPanel.render(sb);
                ContextPatches.popPlayerContext();
            }

        }else{
            __instance.energyPanel.render(sb);
        }
    }


    private static final float ENERGY_ORB_SPACING=85F;
    @SpirePatch2(clz = OverlayMenu.class, method = "render", paramtypez={SpriteBatch.class})
    public static class OverlayMenuRenderPatch {
        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(EnergyPanel.class.getName()) && m.getMethodName().equals("render")) {
                        m.replace("{ "+OverlayMenuPatches.class.getName() + ".renderSubcharacterEnergyInstead(this,sb); }");
                    }
                }
            };
        }
    }

}
