package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;


public class BGTalkToTheHandAction extends AbstractGameAction {
    private AbstractPlayer p;

    private int block;


    public BGTalkToTheHandAction(AbstractPlayer p,int block) {
        this.p=p;
        this.block = block;
    }

    public void update() {
        AbstractRelic r = p.getRelic("BoardGame:BGMiracles");
        if (r != null) {
            for (int i = 0; i < r.counter; i += 1) {
                addToTop((AbstractGameAction) new GainBlockAction(p, block));
            }
        }
        this.isDone = true;
    }
}

