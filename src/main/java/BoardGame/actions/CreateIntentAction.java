package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

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
