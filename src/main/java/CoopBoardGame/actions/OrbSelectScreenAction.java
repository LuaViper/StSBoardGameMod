package CoopBoardGame.actions;

import CoopBoardGame.screen.OrbSelectScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class OrbSelectScreenAction extends AbstractGameAction {

    private OrbSelectScreen.OrbSelectAction action;
    private String description;
    private boolean prohibitDarkOrbs;

    public OrbSelectScreenAction(
        OrbSelectScreen.OrbSelectAction action,
        String description,
        boolean prohibitDarkOrbs
    ) {
        this.action = action;
        this.description = description;
        this.prohibitDarkOrbs = prohibitDarkOrbs;
    }

    //TODO: make a ForcedWaitAction which applies even if settings are set to fastmode, but don't wait if there are no targets available

    public void update() {
        BaseMod.openCustomScreen(
            OrbSelectScreen.Enum.ORB_SELECT,
            action,
            description,
            prohibitDarkOrbs
        );

        this.isDone = true;
    }
}
