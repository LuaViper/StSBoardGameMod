package BoardGame.ui;

import BoardGame.potions.BGGamblersBrew;
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

import javax.tools.Tool;

import static BoardGame.BoardGame.makeRelicPath;

public class LockInRollButton extends Button {

    //public boolean visible=false;

    public static String relicList="";

    public boolean visible=false;
    public boolean isDisabled=true;
    public LockInRollButton() {
        super((Settings.WIDTH / 2)-350*Settings.scale, (Settings.HEIGHT / 2),TextureLoader.getTexture("BoardGameResources/images/ui/dice/LockIn.png"));
    }

    public void update() {
        super.update();
        if (this.visible) {
            if(LockInRollButton.playerHasAnyDieChangingAbilities()) {
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
        if(relicListText.equals("")){
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

    public static boolean playerHasAnyDieChangingAbilities(){
        return AbstractDungeon.player.hasRelic("BGGambling Chip") || AbstractDungeon.player.hasRelic("BGTheAbacus") || AbstractDungeon.player.hasRelic("BGToolbox") || BGGamblersBrew.doesPlayerHaveGamblersBrew()>-1;
    }




}
