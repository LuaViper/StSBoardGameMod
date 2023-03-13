package BoardGame.actions;

import BoardGame.BoardGame;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGTheDiePower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.monsterRng;

public class CreateIntentAction extends AbstractGameAction {
    private AbstractMonster monster;

    public CreateIntentAction(AbstractMonster monster){
        this.monster=monster;
    }


    public void update() {
        monster.createIntent();
        this.isDone=true;
    }
}
