package BoardGame.ui;

import BoardGame.potions.BGEntropicBrew;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.Button;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class EntropicBrewPotionButton extends Button {
    public AbstractPotion realPotion;
    boolean thisIsACourierPreview=false;

    //TODO: localization
    public String[] descriptions = {"You're carrying too many potions! ",
            "You can use or discard existing potions to make room for others,",
            "but this must be done before taking any other actions."};

    public static EntropicBrewPotionButton SetupButton(AbstractPotion realPotion, boolean thisIsACourierPreview){
        ArrayList<Button> buttons = TopPanelEntropicInterface.entropicBrewPotionButtons.get(AbstractDungeon.topPanel);
        //TODO LATER: button placement will break if Courier and Entropic Brew are activated at the same time
        //TODO: button placement isn't all that great anyway
        int xoffset=0;
        int yoffset=0;
        if(!thisIsACourierPreview){
            xoffset=-64+(64*2*buttons.size());
            yoffset=0;
        }
        EntropicBrewPotionButton button=new EntropicBrewPotionButton(xoffset,yoffset, realPotion, thisIsACourierPreview);
        buttons.add(button);
        return button;
    }

    public EntropicBrewPotionButton(int xoffset, int yoffset, AbstractPotion realPotion, boolean thisIsACourierPreview) {
        //img isn't actually used but we need something to pass to Button constructor
        super((Settings.WIDTH / 2)+xoffset*Settings.scale, Settings.HEIGHT/2+yoffset*Settings.scale, TextureLoader.getTexture("BoardGameResources/images/icons/pot.png"));
        this.realPotion=realPotion;
        this.hb=new Hitbox(64,64);
        hb.x=x-64/2;hb.y=y-64/2;
        this.thisIsACourierPreview=thisIsACourierPreview;
    }

    public void update() {
        //do NOT call super

        this.hb.update();
        if (this.hb.hovered && InputHelper.justClickedLeft) {
            this.pressed = true;
            InputHelper.justClickedLeft = false;
        }

        if (this.hb.hovered) {
            if (this.hb.justHovered) {
                CardCrawlGame.sound.play("UI_HOVER");
            }
        }
        if (this.pressed) {
            if(!thisIsACourierPreview) {
                if (BGEntropicBrew.countOpenPotionSlots() >= 1) {
                    if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ObtainPotionAction(realPotion));
                    } else {
                        AbstractDungeon.effectsQueue.add(new ObtainPotionEffect(realPotion));
                    }
                    this.die();
                } else {
                    BoardGame.BoardGame.logger.info("EntropicBrewPotionButton: NOT ENOUGH POTION SLOTS");
                    AbstractDungeon.topPanel.flashRed();
                }
            }
        }
        if(this.pressed){this.pressed=false;}
    }


    public void render(SpriteBatch sb){
        //do NOT call super

        {
            AbstractPotion p = this.realPotion;
            float angle=0.0F;
            float scale=1.0F;
            Texture liquidImg=ReflectionHacks.getPrivate(p,AbstractPotion.class,"liquidImg");
            Texture hybridImg=ReflectionHacks.getPrivate(p,AbstractPotion.class,"hybridImg");
            Texture spotsImg=ReflectionHacks.getPrivate(p,AbstractPotion.class,"spotsImg");
            Texture containerImg=ReflectionHacks.getPrivate(p,AbstractPotion.class,"containerImg");

            sb.setColor(p.liquidColor);
            sb.draw(liquidImg, this.x - 32.0F, this.y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, angle, 0, 0, 64, 64, false, false);
            if (p.hybridColor != null) {
                sb.setColor(p.hybridColor);
                sb.draw(hybridImg, this.x - 32.0F, this.y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, angle, 0, 0, 64, 64, false, false);
            }

            if (p.spotsColor != null) {
                sb.setColor(p.spotsColor);
                sb.draw(spotsImg, this.x - 32.0F, this.y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, angle, 0, 0, 64, 64, false, false);
            }

            sb.setColor(Color.WHITE);
            sb.draw(containerImg, this.x - 32.0F, this.y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, angle, 0, 0, 64, 64, false, false);
        }

        if (this.hb.hovered && !Settings.isTouchScreen) {
            if(!AbstractDungeon.isScreenUp || thisIsACourierPreview ) {
                String description = realPotion.description;
                if (!thisIsACourierPreview) {
                    //TODO: localization
                    description += " NL NL Click to add to inventory. NL NL You can't use this without adding it to your inventory first.";
                }

                TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y - 90.0F * Settings.scale,
                        realPotion.name,
                        description);
            }
        }

        if(!thisIsACourierPreview) {
            //TODO: this is rendering the notice once for each button on screen
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.descriptions[0], (Settings.WIDTH / 2),
                    Settings.HEIGHT - (1 * 180.0F + 30) * Settings.scale, Settings.CREAM_COLOR);
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.descriptions[1], (Settings.WIDTH / 2),
                    Settings.HEIGHT - (1 * 180.0F + 90) * Settings.scale, Settings.CREAM_COLOR);
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.descriptions[2], (Settings.WIDTH / 2),
                    Settings.HEIGHT - (1 * 180.0F + 125) * Settings.scale, Settings.CREAM_COLOR);
        }
    }


    private static Logger logger = LogManager.getLogger(EntropicBrewPotionButton.class.getName());
    @SpirePatch(clz= TopPanel.class, method=SpirePatch.CLASS)
    public static class TopPanelEntropicInterface
    {
        public static SpireField<ArrayList<Button>> entropicBrewPotionButtons = new SpireField<>(() -> new ArrayList<>());
    }

    @SpirePatch2(clz= TopPanel.class, method="update",
                    paramtypez={})
    public static class TopPanelEntropicInterfaceUpdatePatch
    {
        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance){
            ArrayList<Button> buttons = TopPanelEntropicInterface.entropicBrewPotionButtons.get(__instance);
            for(Button button : buttons){
                button.update();
            }
            buttons.removeIf(b -> b.x<=-9999);
        }
    }

    @SpirePatch2(clz= TopPanel.class, method="render",
            paramtypez={SpriteBatch.class})
    public static class TopPanelEntropicInterfaceRenderPatch
    {
        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance, SpriteBatch sb){
            ArrayList<Button> buttons = TopPanelEntropicInterface.entropicBrewPotionButtons.get(__instance);
            for(Button button : buttons){
                button.render(sb);
            }
        }
    }

    public void die(){
        //this.dying=true;      //TODO LATER: patch "dying" into base Button class
        this.x=-9999;
    }


}
