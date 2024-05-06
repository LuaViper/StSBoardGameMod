package BoardGame.actions;

import com.megacrit.cardcrawl.actions.utility.WaitAction;

//Forces the game to pause even if FAST MODE is enabled.
public class BGForcedWaitAction extends WaitAction {
    public BGForcedWaitAction(float setDur){
        super(setDur);
        this.duration=setDur;
    }

}
