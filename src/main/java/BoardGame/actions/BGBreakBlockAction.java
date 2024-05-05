
package BoardGame.actions;

import BoardGame.monsters.AbstractBGMonster;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGBreakBlockAction
        extends AbstractGameAction {

    private DamageInfo info = null;

    private AbstractCreature target;
    private int extrahits;
    public BGBreakBlockAction(AbstractCreature m, AbstractPlayer p) {
        target=m;
    }

    public void update() {
        if(target instanceof AbstractBGMonster) {
            ((AbstractBGMonster)target).publicBrokeBlock();
        }
        this.isDone = true;
    }
}


