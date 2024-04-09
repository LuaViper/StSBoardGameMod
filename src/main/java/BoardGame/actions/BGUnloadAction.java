package BoardGame.actions;

import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGUnloadAction extends AbstractGameAction {
    private int bonusDamage;
    public BGUnloadAction(int bonusDamage) {
        this.actionType = ActionType.SPECIAL;
        this.attackEffect = AttackEffect.NONE;
        this.bonusDamage=bonusDamage;
    }

    public void update() {
        BGShivs relic = (BGShivs) AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        if (relic != null) {
            for(int i=0;i<relic.counter;i+=1){
                addToTop(new BGUseShivAction(true,false,bonusDamage,"Choose a target for Shiv (+"+Integer.toString(bonusDamage)+" damage)."));
            }
        }
        this.isDone = true;
    }
}