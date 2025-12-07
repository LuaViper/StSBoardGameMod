package CoopBoardGame.actions;

import CoopBoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class BGBreakBlockAction extends AbstractGameAction {

    private AbstractCreature target;

    public BGBreakBlockAction(AbstractCreature m, AbstractPlayer p) {
        target = m;
    }

    public void update() {
        if (target instanceof AbstractBGMonster) {
            ((AbstractBGMonster) target).publicBrokeBlock();
        }
        this.isDone = true;
    }
}
