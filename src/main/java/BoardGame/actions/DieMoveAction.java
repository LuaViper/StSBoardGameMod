package BoardGame.actions;

import BoardGame.monsters.DieControlledMoves;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DieMoveAction extends AbstractGameAction {
    private AbstractMonster monster;

    public DieMoveAction(DieControlledMoves monster) {
        this.monster = (AbstractMonster) monster;
    }


    public void update() {
        ((DieControlledMoves) this.monster).dieMove(TheDie.monsterRoll);
        this.monster.createIntent();
        this.isDone = true;
    }
}
