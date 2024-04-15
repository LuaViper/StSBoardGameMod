
package BoardGame.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class BGMelterAction
        extends AbstractGameAction {

    private DamageInfo info = null;

    private AbstractCreature target;
    private AbstractPlayer source;
    private int extrahits;
    public BGMelterAction(AbstractCreature m, AbstractPlayer p) {
        source=p;
        target=m;
    }

    public void update() {
        int block=target.currentBlock;
        if(block>0) {
            addToTop((AbstractGameAction) new BGBreakBlockAction((AbstractCreature) target, source));
        }
        addToTop((AbstractGameAction) new RemoveAllBlockAction((AbstractCreature) target, (AbstractCreature) source));
        this.isDone = true;
    }
}


