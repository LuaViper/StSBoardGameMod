package BoardGame.ui;

import BoardGame.screen.RelicTradingScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.Button;

public class FakeTradingRelic extends Button {
    RelicTradingScreen screen;
    AbstractRelic realRelic;

    public FakeTradingRelic(RelicTradingScreen screen, AbstractRelic realRelic,int xoffset,int yoffset) {
        super((Settings.WIDTH / 2)+xoffset*Settings.scale, (Settings.HEIGHT / 2)+yoffset*Settings.scale, realRelic.img);
        this.screen=screen;
        this.realRelic=realRelic;
        this.hb=new Hitbox(96,96);
        hb.x=x-96/2;hb.y=y-96/2;
        //this.hb.cY=-64;
    }

    public void update() {
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
            screen.onRelicChosen(this.realRelic);
        }
        if(this.pressed){this.pressed=false;}
    }


    public void render(SpriteBatch sb) {
        //this.renderOutline(sb, false);

        sb.setColor(Color.WHITE);
        sb.draw(realRelic.img, this.x - 64.0F, this.y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, 1, 1, 0, 0, 0, 128, 128, false, false);
        //this.renderCounter(sb, false);

        this.hb.render(sb);

        if (this.hb.hovered && !Settings.isTouchScreen) {
            TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y - 90.0F * Settings.scale,
                    realRelic.name,
                    realRelic.description + " NL Shop price "+realRelic.getPrice()+" gold"
            );
        }

    }





}