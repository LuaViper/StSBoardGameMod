package BoardGame.actions;

import BoardGame.powers.BGAkabekoPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static BoardGame.powers.StrengthCap.MAX_STRENGTH;

public class CheckAkabekoStrengthCapAction extends AbstractGameAction {

    public BGAkabekoPower p;

    public CheckAkabekoStrengthCapAction(BGAkabekoPower p)
    {
        this.p=p;
    }

    @Override
    public void update() {
        AbstractPower pw = p.owner.getPower(StrengthPower.POWER_ID);

        if (pw != null && pw.amount==MAX_STRENGTH) {
            p.gainedStrengthSuccessfully=false;
        }else{
            p.gainedStrengthSuccessfully=true;
        }
        this.isDone=true;
    }
}
