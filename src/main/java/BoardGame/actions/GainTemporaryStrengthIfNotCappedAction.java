package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static BoardGame.powers.StrengthCap.MAX_STRENGTH;

public class GainTemporaryStrengthIfNotCappedAction extends AbstractGameAction {

    public int magicNumber;
    public AbstractPlayer p;

    public GainTemporaryStrengthIfNotCappedAction(AbstractPlayer p, int magicNumber)
    {
        this.p=p;
        this.magicNumber=magicNumber;
    }

    @Override
    public void update() {
        AbstractPower pw = AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
        int amount=this.magicNumber;

        if (pw != null && pw.amount+amount>MAX_STRENGTH) {
            amount=MAX_STRENGTH-pw.amount;
        }
        if(amount>0){
            addToTop(new ApplyPowerAction(p, p, new LoseStrengthPower(p, amount), amount));
            addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, amount), amount));
        }
        this.isDone=true;
    }
}
