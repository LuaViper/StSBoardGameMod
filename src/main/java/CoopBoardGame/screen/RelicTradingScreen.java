package CoopBoardGame.screen;

import CoopBoardGame.relics.AbstractBGRelic;
import CoopBoardGame.ui.FakeTradingRelic;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public String description = "(DNT) Relic Select Screen.  Choose a Relic.";
    public boolean displayRelicPrice = false;

    public ArrayList<FakeTradingRelic> relics;

    public void open(RelicTradingAction action, String description, boolean displayRelicPrice) {
        AbstractDungeon.overlayMenu.showBlackScreen();
        this.action = action;
        this.description = description;
        this.displayRelicPrice = displayRelicPrice;
        this.isDone = false;

        // Initialize relics list with FakeTradingRelic objects
        this.relics = new ArrayList<>();
        ArrayList<AbstractRelic> payableRelics = AbstractBGRelic.getAllPayableRelics();

        // Position relics in a grid pattern (5 per row, centered)
        int relicsPerRow = 5;
        int spacing = 160; // spacing between relics

        for (int i = 0; i < payableRelics.size(); i++) {
            int row = i / relicsPerRow;
            int col = i % relicsPerRow;
            int numInThisRow = Math.min(relicsPerRow, payableRelics.size() - row * relicsPerRow);

            // Center each row
            int xOffset = (col - numInThisRow / 2) * spacing + (numInThisRow % 2 == 0 ? spacing / 2 : 0);
            int yOffset = (1 - row) * spacing; // rows go downward

            this.relics.add(new FakeTradingRelic(this, payableRelics.get(i), xOffset, yOffset));
        }

        if (
            AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE
        ) AbstractDungeon.previousScreen = AbstractDungeon.screen;
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
        for (FakeTradingRelic r : relics) {
            r.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        //FontHelper.renderDeckViewTip(sb, this.description, 96.0F * Settings.scale, Settings.CREAM_COLOR);
        FontHelper.renderFontCentered(
            sb,
            FontHelper.buttonLabelFont,
            this.description,
            (Settings.WIDTH / 2),
            Settings.HEIGHT - 180.0F * Settings.scale,
            Settings.CREAM_COLOR
        );
        for (FakeTradingRelic r : relics) {
            r.render(sb);
        }
    }

    public void onRelicChosen(AbstractRelic relic) {
        action.execute(relic);
        isDone = true;
        AbstractDungeon.closeCurrentScreen();
    }
}
