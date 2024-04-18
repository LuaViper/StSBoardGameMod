package BoardGame.ui;

import BoardGame.multicharacter.MultiCharacterRowBoxes;
import BoardGame.multicharacter.grid.GridBackground;
import BoardGame.relics.AbstractBGRelic;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.OverlayMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlayMenuPatches {

    private static Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    @SpirePatch(clz= OverlayMenu.class, method=SpirePatch.CLASS)
    public static class OverlayMenuExtraInterface
    {
        public static SpireField<GridBackground> gridBackground = new SpireField<>(()->new GridBackground());
        public static SpireField<LockInRollButton> lockinrollbutton = new SpireField<>(() -> new LockInRollButton());
        public static SpireField<RerollButton> rerollbutton = new SpireField<>(() -> new RerollButton());
        public static SpireField<TheAbacusButton> theabacusbutton = new SpireField<>(() -> new TheAbacusButton());
        public static SpireField<ToolboxButton> toolboxbutton = new SpireField<>(() -> new ToolboxButton());
        public static SpireField<PotionButton> potionbutton = new SpireField<>(() -> new PotionButton());
        public static SpireField<MultiCharacterRowBoxes> multiCharacterRowBoxes = new SpireField<>(() -> new MultiCharacterRowBoxes());
    }

    @SpirePatch2(clz= OverlayMenu.class, method="update",
            paramtypez={})
    public static class OverlayMenuDiceInterfaceUpdatePatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance){
            //logger.info("OverlayMenuDiceInterfaceUpdatePatch postfix");
            OverlayMenuExtraInterface.gridBackground.get(__instance).update();
            OverlayMenuExtraInterface.lockinrollbutton.get(__instance).update();
            OverlayMenuExtraInterface.rerollbutton.get(__instance).update();
            OverlayMenuExtraInterface.theabacusbutton.get(__instance).update();
            OverlayMenuExtraInterface.toolboxbutton.get(__instance).update();
            OverlayMenuExtraInterface.potionbutton.get(__instance).update();
            OverlayMenuExtraInterface.multiCharacterRowBoxes.get(__instance).update();

        }
    }

    @SpirePatch2(clz= OverlayMenu.class, method="render",
            paramtypez={SpriteBatch.class})
    public static class OverlayMenuDiceInterfaceRenderPatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance, SpriteBatch sb){
            OverlayMenuExtraInterface.gridBackground.get(__instance).render(sb);
            OverlayMenuExtraInterface.lockinrollbutton.get(__instance).render(sb);
            OverlayMenuExtraInterface.rerollbutton.get(__instance).render(sb);
            OverlayMenuExtraInterface.theabacusbutton.get(__instance).render(sb);
            OverlayMenuExtraInterface.toolboxbutton.get(__instance).render(sb);
            OverlayMenuExtraInterface.potionbutton.get(__instance).render(sb);
            OverlayMenuExtraInterface.multiCharacterRowBoxes.get(__instance).render(sb);
        }
    }

    @SpirePatch2(clz= OverlayMenu.class, method="showCombatPanels",
            paramtypez={})
    public static class showCombatPanelsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance){
            OverlayMenuExtraInterface.gridBackground.get(__instance).visible=true;
            OverlayMenuExtraInterface.lockinrollbutton.get(__instance).visible=true;
            OverlayMenuExtraInterface.rerollbutton.get(__instance).visible=true;
            OverlayMenuExtraInterface.theabacusbutton.get(__instance).visible=true;
            OverlayMenuExtraInterface.toolboxbutton.get(__instance).visible=true;
            OverlayMenuExtraInterface.potionbutton.get(__instance).visible=true;
        }
    }

    @SpirePatch2(clz= OverlayMenu.class, method="hideCombatPanels",
            paramtypez={})
    public static class hideCombatPanelsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance){
            OverlayMenuExtraInterface.gridBackground.get(__instance).visible=false;
            OverlayMenuExtraInterface.lockinrollbutton.get(__instance).visible=false;
            OverlayMenuExtraInterface.rerollbutton.get(__instance).visible=false;
            OverlayMenuExtraInterface.theabacusbutton.get(__instance).visible=false;
            OverlayMenuExtraInterface.toolboxbutton.get(__instance).visible=false;
            OverlayMenuExtraInterface.potionbutton.get(__instance).visible=false;
        }
    }
}
