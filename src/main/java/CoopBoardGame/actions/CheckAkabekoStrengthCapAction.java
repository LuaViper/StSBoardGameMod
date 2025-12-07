package CoopBoardGame.actions;

import static CoopBoardGame.powers.StrengthCap.MAX_STRENGTH;

import CoopBoardGame.powers.BGAkabekoPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class CheckAkabekoStrengthCapAction extends AbstractGameAction {

    public BGAkabekoPower p;

    public CheckAkabekoStrengthCapAction(BGAkabekoPower p) {
        this.p = p;
    }

    @Override
    public void update() {
        AbstractPower pw = p.owner.getPower(StrengthPower.POWER_ID);

        if (pw != null && pw.amount == MAX_STRENGTH) {
            p.gainedStrengthSuccessfully = false;
        } else {
            p.gainedStrengthSuccessfully = true;
        }
        this.isDone = true;
    }
}
