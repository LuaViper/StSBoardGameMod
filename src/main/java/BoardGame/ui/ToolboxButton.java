package BoardGame.ui;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGToolbox;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToolboxButton extends Button {

    //public boolean visible=false;

    public static String relicList="";

    public boolean visible=false;
    public boolean isDisabled=true;
    public ToolboxButton() {
        super((Settings.WIDTH / 2)-150*Settings.scale, (Settings.HEIGHT / 2)-200*Settings.scale,TextureLoader.getTexture("BoardGameResources/images/ui/dice/Toolbox.png"));
    }
    private static Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    public void update() {
        super.update();
        if(this.visible) {
            if (AbstractDungeon.player.hasRelic("BGToolbox")) {
                AbstractRelic r = AbstractDungeon.player.getRelic("BGToolbox");
                if (this.visible) {
                    this.isDisabled = false;
                    if (AbstractDungeon.isScreenUp || AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode) {
                        //TODO: what is "inSingleTargetMode"?
                        this.isDisabled = true;
                    }
                    if (!((BGToolbox) r).isUsable()) {
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
                            if (AbstractDungeon.player.hasRelic("BGToolbox")) {
                                ((BGToolbox) r).activate();
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
        if (AbstractDungeon.player.hasRelic("BGToolbox")) {
           r = AbstractDungeon.player.getRelic("BGToolbox");
        }else return;

        if(!visible || !((BGToolbox) r).isUsable())return;
        super.render(sb);
        String desc="Activate #yToolbox. NL You can undo this before locking in the roll.";
        String desc2="Deactivate #yToolbox.";

        int abacus= TheDie.initialRoll+1;if(abacus>6)abacus=1;
        int toolbox=TheDie.initialRoll-1;if(toolbox<1)toolbox=6;

        if (this.hb.hovered && !AbstractDungeon.isScreenUp && !Settings.isTouchScreen) {
            TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y + 300.0F * Settings.scale,
                    "-1",
                    (TheDie.monsterRoll==toolbox) ? desc2 : desc
            );
        }
    }




}
