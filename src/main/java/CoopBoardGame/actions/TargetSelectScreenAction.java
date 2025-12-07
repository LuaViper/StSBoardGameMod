package CoopBoardGame.actions;

import CoopBoardGame.screen.TargetSelectScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class TargetSelectScreenAction extends AbstractGameAction {

    private TargetSelectScreen.TargetSelectAction action;
    private String description;
    private boolean allowCancel;

    public TargetSelectScreenAction(
        TargetSelectScreen.TargetSelectAction action,
        String description
    ) {
        this(action, description, false);
    }

    public TargetSelectScreenAction(
        TargetSelectScreen.TargetSelectAction action,
        String description,
        boolean allowCancel
    ) {
        this.action = action;
        this.description = description;
        this.allowCancel = allowCancel;
    }

    //TODO: make a ForcedWaitAction which applies even if settings are set to fastmode, but don't wait if there are no targets available

    public void update() {
        BaseMod.openCustomScreen(
            TargetSelectScreen.Enum.TARGET_SELECT,
            action,
            description,
            allowCancel
        );

        this.isDone = true;
    }
}
