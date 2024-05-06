package BoardGame.actions;

import BoardGame.screen.OrbSelectScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrbSelectScreenAction extends AbstractGameAction {
    private OrbSelectScreen.OrbSelectAction action;
    private String description;
    private boolean prohibitDarkOrbs;
    private boolean allowCancel;
    private OrbSelectScreen.OrbSelectAction cancelAction;

    public OrbSelectScreenAction(OrbSelectScreen.OrbSelectAction action, String description, boolean prohibitDarkOrbs) {
        this.action=action;
        this.description=description;
        this.prohibitDarkOrbs=prohibitDarkOrbs;
    }

    //TODO: make a ForcedWaitAction which applies even if settings are set to fastmode, but don't wait if there are no targets available

    public void update() {
        final Logger logger = LogManager.getLogger(OrbSelectScreenAction.class.getName());
        //logger.info("OSSAction update");
        BaseMod.openCustomScreen(OrbSelectScreen.Enum.ORB_SELECT,action, description, prohibitDarkOrbs);
        //logger.info("Opened OrbSelectScreen with desc "+description);
        //logger.info("For the record, the current screen is "+ AbstractDungeon.screen);

        this.isDone = true;
    }
}


