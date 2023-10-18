package BoardGame.powers;

import BoardGame.BoardGame;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.relics.DieControlledRelic;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

//at the moment, we're only using this class as a way to implement clickable powers
// Note that some cards use vanilla powers instead of BG powers and won't extend this class
public class AbstractBGPower extends AbstractPower {

    //TODO: clickbox is too large atm
    private static final float iconsize=48*1.17F;
    public Hitbox hb;

    public static final Logger logger = LogManager.getLogger(AbstractBGPower.class.getName());
    public AbstractBGPower(){
        super();
        hb=new Hitbox(iconsize,iconsize);
        //logger.info("AbstractBGPower.hb created");
    }



    public boolean clickable=false;
    public boolean onCooldown=false;
    public boolean autoActivate=false;
    public boolean isCurrentlyPlayerTurn=true; //TODO: this will break if Mayhem applies a power at end of turn
     public void onRightClick(){
        //override
     }

     public void atStartOfTurnPostDraw(){
         super.atStartOfTurnPostDraw();
         isCurrentlyPlayerTurn=true;
         onCooldown=false;
         updateDescription();
     }
    public void atEndOfTurn(boolean isPlayer){
        super.atEndOfTurn(isPlayer);
        if(autoActivate){
            onRightClick();
        }
        isCurrentlyPlayerTurn=false;
        updateDescription();

    }

     public String getRightClickDescriptionText(){
        if(!clickable)
         return "";
        else if (onCooldown)
            return " (On cooldown.)";
        else if (!autoActivate)
            return " #bRight-click to activate.";
        else
            return " #bRight-click to activate. (Auto-activates at end of turn.)";

     }

    public void update(int slot) {
         super.update(slot);

        hb.update();
        if(clickable) {
            if (hb.hovered) {
                //logger.info("AbstractBGPower.hb hovered");
                if (InputHelper.justClickedRight) {
                    //logger.info("AbstractBGPower.hb clicked");
                    if (isCurrentlyPlayerTurn && !onCooldown) {
                        for (AbstractRelic relic : AbstractDungeon.player.relics) {
                            if (relic instanceof BGTheDieRelic) {
                                TheDie.forceLockInRoll = true;
                                ((BGTheDieRelic) relic).lockRollAndActivateDieRelics();
                            }
                        }
                        //TODO: also call all AbstractBGPower.onRightclickPower (NYI) to trigger wildcard powers
                        onRightClick();
                        updateDescription();

                    }
                }
            }
        }
    }

     public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb,x,y,c);
         hb.move(x - this.region48.packedWidth / 2.0F, y - this.region48.packedHeight / 2.0F);
     }
}
