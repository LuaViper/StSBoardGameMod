package BoardGame.ui;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGGamblingChip;
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
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static BoardGame.BoardGame.makeRelicPath;

public class RerollButton extends Button {

    //public boolean visible=false;

    public static String relicList="";

    public boolean visible=false;
    public boolean isDisabled=true;
    public RerollButton() {
        super((Settings.WIDTH / 2)-50, (Settings.HEIGHT / 2),TextureLoader.getTexture("BoardGameResources/images/ui/dice/Reroll.png"));
    }
    private static Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    public void update() {
        super.update();
        if(this.visible) {
            if (AbstractDungeon.player.hasRelic("BGGambling Chip")) {
                AbstractRelic r = AbstractDungeon.player.getRelic("BGGambling Chip");
                if (this.visible) {
                    this.isDisabled = false;
                    if (AbstractDungeon.isScreenUp || AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode) {
                        //TODO: what is "inSingleTargetMode"?
                        this.isDisabled = true;
                    }
                    if (!((BGGamblingChip) r).isUsable()) {
                        this.isDisabled = true;
                        //this.visible = false;
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
                            if (AbstractDungeon.player.hasRelic("BGGambling Chip")) {

                                ((BGGamblingChip) r).activate();
                            }
                        }
                    }
                }
            } else {
                this.visible = false;
            }
        }
        if(this.pressed){this.pressed=false;}
    }


    public void render(SpriteBatch sb){
        AbstractRelic r;
        if (AbstractDungeon.player.hasRelic("BGGambling Chip")) {
           r = AbstractDungeon.player.getRelic("BGGambling Chip");
        }else return;

        if(!visible || !((BGGamblingChip) r).isUsable())return;
        super.render(sb);
        String desc="Use #yGambling #yChip to reroll the die.";

        if (this.hb.hovered && !AbstractDungeon.isScreenUp && !Settings.isTouchScreen) {
            TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y + 300.0F * Settings.scale,
                    "Reroll",
                    desc
            );
        }

    }




}
