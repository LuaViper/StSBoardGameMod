package BoardGame.ui;

import BoardGame.potions.BGEntropicBrew;
import BoardGame.relics.AbstractBGRelic;
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

public class EntropicBrewCourierRelicButton extends Button {
    public AbstractRelic realRelic;
    boolean dying=false;



    public static EntropicBrewCourierRelicButton SetupButton(AbstractRelic realRelic){
        ArrayList<Button> buttons = EntropicBrewPotionButton.TopPanelEntropicInterface.entropicBrewPotionButtons.get(AbstractDungeon.topPanel);
        //TODO LATER: button placement will break if Courier and Entropic Brew are activated at the same time
        //TODO: button placement isn't all that great anyway
        int xoffset=0;
        int yoffset=0;
        EntropicBrewCourierRelicButton button=new EntropicBrewCourierRelicButton(xoffset,yoffset, realRelic);
        buttons.add(button);
        return button;
    }

    public EntropicBrewCourierRelicButton(int xoffset, int yoffset, AbstractRelic realRelic) {
        //img isn't actually used but we need something to pass to Button constructor
        super((Settings.WIDTH / 2)+xoffset*Settings.scale, Settings.HEIGHT/2+yoffset*Settings.scale, TextureLoader.getTexture("BoardGameResources/images/icons/pot.png"));
        this.realRelic=realRelic;
        this.hb=new Hitbox(96,96);
        hb.x=x-96/2;hb.y=y-96/2;

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
        if(this.pressed){this.pressed=false;}
    }


    public void render(SpriteBatch sb){
        //do NOT call super

        sb.setColor(Color.WHITE);
        sb.draw(realRelic.img, this.x - 64.0F, this.y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, 1, 1, 0, 0, 0, 128, 128, false, false);
        //this.renderCounter(sb, false);

        this.hb.render(sb);

        if (this.hb.hovered && !Settings.isTouchScreen) {
            TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y - 90.0F * Settings.scale,
                    realRelic.name,
                    realRelic.description
            );
        }
    }



    public void die(){
        //this.dying=true;
        this.x=-9999;
    }


}
