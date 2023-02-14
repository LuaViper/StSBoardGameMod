package BoardGame.actions;

import BoardGame.monsters.bgexordium.BGGremlinAngry;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AngryPower;

public class ApplyAngerToGremlinsAction extends AbstractGameAction {
    @Override
    public void update() {
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if(m instanceof BGGremlinAngry){
                if(!m.hasPower("Angry")){
                    addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new AngryPower((AbstractCreature)m, 1), 1));
                }
            }
        }

        this.isDone = true;
    }
}
