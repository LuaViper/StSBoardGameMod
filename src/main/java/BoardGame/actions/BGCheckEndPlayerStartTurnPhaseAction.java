package BoardGame.actions;

import BoardGame.characters.AbstractBGPlayer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class BGCheckEndPlayerStartTurnPhaseAction
        extends AbstractGameAction {

    public BGCheckEndPlayerStartTurnPhaseAction() {

    }




    public void update() {
        AbstractBGPlayer.checkEndPlayerStartTurnPhase();
        isDone=true;
    }




}


