package BoardGame.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BGBuffAllEnemiesAction extends AbstractGameAction {
    public static final Logger logger = LogManager.getLogger(BGBuffAllEnemiesAction.class.getName());
    public static final float[] POSX = new float[] { -366.0F, -170.0F };
    public static final float[] POSY = new float[] { -4.0F, 6.0F };
    public AbstractCreature source;
    private final int strAmt=1;

    public BGBuffAllEnemiesAction(AbstractCreature source) {
        this.source=source;
    }



    public void update() {
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (!m.isDying && !m.isEscaping) {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)source, (AbstractPower)new StrengthPower((AbstractCreature)m, this.strAmt), this.strAmt));
            }
        }

        this.isDone = true;
    }
}

