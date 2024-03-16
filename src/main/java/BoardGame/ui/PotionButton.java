package BoardGame.ui;

import BoardGame.potions.BGGamblersBrew;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGTheAbacus;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionButton extends Button {

    //public boolean visible=false;

    public static String relicList="";

    public boolean visible=false;
    public boolean isDisabled=true;
    public PotionButton() {
        super((Settings.WIDTH / 2)-350*Settings.scale, (Settings.HEIGHT / 2)+200*Settings.scale,TextureLoader.getTexture("BoardGameResources/images/ui/dice/Potion.png"));
    }

    public void update() {
        super.update();
        if (this.visible) {
            if(BGGamblersBrew.doesPlayerHaveGamblersBrew()>-1) {
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
                        TheDie.monsterRoll+=1;
                        if(TheDie.monsterRoll>6)TheDie.monsterRoll=1;
                        TheDie.tentativeRoll(TheDie.monsterRoll);

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


        if (this.hb.hovered && !AbstractDungeon.isScreenUp && !Settings.isTouchScreen) {
            //TODO: localization
            String body="Freely change the die to any number.";
            String body2="(The new roll is #b"+TheDie.monsterRoll+". Gambler's Brew will be consumed.)";
            if(TheDie.monsterRoll==TheDie.initialRoll)
                body2="(The roll is currently unchanged.)";
            int abacus=TheDie.initialRoll+1;if(abacus>6)abacus=1;
            int toolbox=TheDie.initialRoll-1;if(toolbox<1)toolbox=6;
            if(TheDie.monsterRoll==abacus && AbstractDungeon.player.hasRelic("BGTheAbacus")){
                body2="(The new roll is #b"+TheDie.monsterRoll+". The Abacus will be used instead.)";
            }
            if(TheDie.monsterRoll==toolbox && AbstractDungeon.player.hasRelic("BGToolbox")){
                body2="(The new roll is #b"+TheDie.monsterRoll+". Toolbox will be used instead.)";
            }
            body+=" NL "+body2;


            TipHelper.renderGenericTip(this.x - 90.0F * Settings.scale, this.y + 300.0F * Settings.scale,
                    "Gambler's Brew",
                    body
                    );
        }

    }


}
