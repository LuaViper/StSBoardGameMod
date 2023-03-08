package BoardGame.ui;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static BoardGame.BoardGame.makeRelicPath;

public class LockInRollButton extends Button {

    //public boolean visible=false;

    public static String relicList="";

    public boolean visible=false;
    public boolean isDisabled=true;
    public LockInRollButton() {
        super((Settings.WIDTH / 2)-200, (Settings.HEIGHT / 2),TextureLoader.getTexture("BoardGameResources/images/ui/dice/LockIn.png"));
    }

    public void update() {
        super.update();
        if (this.visible) {
            if(AbstractDungeon.player.hasRelic("BGGambling Chip")) {
                this.isDisabled = false;
                if (AbstractDungeon.isScreenUp || AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode) {
                    this.isDisabled = true;
                }
                if (TheDie.finalRelicRoll > 0) {
                    this.isDisabled = true;
                    this.visible = false;
                }

                if (AbstractDungeon.player.hoveredCard == null) {
                    this.hb.update();
                }

                if (this.hb.hovered && !this.isDisabled && !AbstractDungeon.isScreenUp) {
                    if (this.hb.justHovered && AbstractDungeon.player.hoveredCard == null) {
                        CardCrawlGame.sound.play("UI_HOVER");
                    }
                }
                if (this.pressed && !this.isDisabled) {
                    if (AbstractDungeon.player.hasRelic("BoardGame:BGTheDieRelic")) {
                        AbstractRelic thedie = AbstractDungeon.player.getRelic("BoardGame:BGTheDieRelic");
                        ((BGTheDieRelic) thedie).lockRollAndActivateDieRelics();
                    }
                }
            }else{
                this.visible=false;
            }
        }
        if(this.pressed){this.pressed=false;}
    }


    public void render(SpriteBatch sb){
        if(!visible)return;
        super.render(sb);

        String relicListText=relicList;
        if(relicListText==""){
            relicListText=" NL (No relics activate on a "+TheDie.monsterRoll+".)";
        }
        if (this.hb.hovered && !AbstractDungeon.isScreenUp && !Settings.isTouchScreen) {
            TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y + 300.0F * Settings.scale,
                    "Lock In",
                    "Accept the current die roll #b("+TheDie.monsterRoll+") and activate relics."
                    +relicListText
                    );
        }

    }


    private static Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    @SpirePatch(clz= OverlayMenu.class, method=SpirePatch.CLASS)
    public static class OverlayMenuDiceInterface
    {
        public static SpireField<LockInRollButton> lockinrollbutton = new SpireField<>(() -> new LockInRollButton());
        public static SpireField<RerollButton> rerollbutton = new SpireField<>(() -> new RerollButton());
    }

    @SpirePatch2(clz= OverlayMenu.class, method="update",
                    paramtypez={})
    public static class OverlayMenuDiceInterfaceUpdatePatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance){
            //logger.info("OverlayMenuDiceInterfaceUpdatePatch postfix");
            OverlayMenuDiceInterface.lockinrollbutton.get(__instance).update();
            OverlayMenuDiceInterface.rerollbutton.get(__instance).update();

        }
    }

    @SpirePatch2(clz= OverlayMenu.class, method="render",
            paramtypez={SpriteBatch.class})
    public static class OverlayMenuDiceInterfaceRenderPatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance, SpriteBatch sb){
            OverlayMenuDiceInterface.lockinrollbutton.get(__instance).render(sb);
            OverlayMenuDiceInterface.rerollbutton.get(__instance).render(sb);
        }
    }

    @SpirePatch2(clz= OverlayMenu.class, method="showCombatPanels",
            paramtypez={})
    public static class showCombatPanelsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance){
            OverlayMenuDiceInterface.lockinrollbutton.get(__instance).visible=true;
            OverlayMenuDiceInterface.rerollbutton.get(__instance).visible=true;
        }
    }

    @SpirePatch2(clz= OverlayMenu.class, method="hideCombatPanels",
            paramtypez={})
    public static class hideCombatPanelsPatch
    {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance){
            OverlayMenuDiceInterface.lockinrollbutton.get(__instance).visible=false;
            OverlayMenuDiceInterface.rerollbutton.get(__instance).visible=false;
        }
    }

}
