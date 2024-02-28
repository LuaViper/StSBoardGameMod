package BoardGame.actions;

import BoardGame.screen.OrbSelectScreen;
import BoardGame.screen.TargetSelectScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrbSelectScreenAction extends AbstractGameAction {
    private OrbSelectScreen.OrbSelectAction action;
    private String description;
    private boolean allowCancel;
    private OrbSelectScreen.OrbSelectAction cancelAction;

    public OrbSelectScreenAction(OrbSelectScreen.OrbSelectAction action, String description) {
        this.action=action;
        this.description=description;
    }

    //TODO: make a ForcedWaitAction which applies even if settings are set to fastmode, but don't wait if there are no targets available

    public void update() {
        final Logger logger = LogManager.getLogger(OrbSelectScreenAction.class.getName());
        //logger.info("OSSAction update");
        BaseMod.openCustomScreen(OrbSelectScreen.Enum.ORB_SELECT,action, description);
        //logger.info("Opened OrbSelectScreen with desc "+description);
        //logger.info("For the record, the current screen is "+ AbstractDungeon.screen);

        this.isDone = true;
    }
}


