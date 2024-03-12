package BoardGame.screen;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.ui.FakeTradingRelic;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class RelicTradingScreen extends CustomScreen {

    //TODO: emergency cancel if no relics are available for whatever reason
    //TODO: right-click to cancel (but will make event logic rather more complicated)

    public boolean isDone = false;

    public interface RelicTradingAction {
        void execute(AbstractRelic relic);
    }

    final Logger logger = LogManager.getLogger(RelicTradingScreen.class.getName());

    public static class Enum {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen RELIC_TRADING;
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return RelicTradingScreen.Enum.RELIC_TRADING;
    }


    public RelicTradingScreen.RelicTradingAction action;

    public String description;
    public boolean displayRelicPrice=false;

    public ArrayList<FakeTradingRelic>relics;
    private void open(RelicTradingScreen.RelicTradingAction action, String description, boolean displayRelicPrice) {
        AbstractDungeon.overlayMenu.showBlackScreen();
        this.action = action;
        this.description=description;
        this.displayRelicPrice=displayRelicPrice;
        //this.cancelAction=cancelAction;
        this.isDone = false;

        relics=new ArrayList<>();
        float step=0;

        int total=AbstractBGRelic.getAllPayableRelics().size();
        int radius=0;
        if(total>1)radius=250;
        if(total==3)step+=.25;
        for(AbstractRelic r : AbstractBGRelic.getAllPayableRelics()){
            float t = (float) (step/total*Math.PI*2);
            int x= (int) (radius*Math.cos(t));
            int y= (int) (radius*2/3F*Math.sin(t));

            FakeTradingRelic ftr=new FakeTradingRelic(this,r,x,y);
            relics.add(ftr);
            step+=1;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE)
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        reopen();
    }


    @Override
    public void reopen() {
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.overlayMenu.showBlackScreen();
    }

    @Override
    public void openingSettings() {
        // Required if you want to reopen your screen when the settings screen closes
        AbstractDungeon.previousScreen = curScreen();
    }

    @Override
    public void close() {
        genericScreenOverlayReset();
        GameCursor.hidden = false;
        AbstractDungeon.overlayMenu.hideBlackScreen();
    }


    @Override
    public void update() {
        //logger.info("RTS: update");
        if (!((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.EVENT)) {
            isDone = true;
            AbstractDungeon.closeCurrentScreen();
            return;
        }
        for(FakeTradingRelic r : relics){
            r.update();
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        //FontHelper.renderDeckViewTip(sb, this.description, 96.0F * Settings.scale, Settings.CREAM_COLOR);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.description, (Settings.WIDTH / 2), Settings.HEIGHT - 180.0F * Settings.scale, Settings.CREAM_COLOR);
        for(FakeTradingRelic r : relics){
            r.render(sb);
        }
    }

    public void onRelicChosen(AbstractRelic relic){
        action.execute(relic);
        isDone = true;
        AbstractDungeon.closeCurrentScreen();
    }
}