package BoardGame.actions;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.BGColorless.BGShivsDiscardExtraShiv;
import BoardGame.cards.BGColorless.BGShivsUseExtraShiv;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

//TODO: apparently the game stops prompting for extra shivs if you kill Awakened One Phase 1 with one of them
//TODO: it is suspected that Cunning Potion will close BGEntropicBrew menu
public class BGTooManyShivsAction extends AbstractGameAction {
    private int amount;


    public BGTooManyShivsAction() {
        this.duration = 0.0F;
        this.actionType = ActionType.WAIT;
    }


    public void update() {
        //TODO: some of our existing "are-monsters-alive" checks can be replaced with getRandomMonster!=null
        if (AbstractDungeon.getMonsters().getRandomMonster(true)!=null) {
            ArrayList<AbstractBGAttackCardChoice> attackChoices = new ArrayList<>();
            attackChoices.add(new BGShivsUseExtraShiv());
            attackChoices.add(new BGShivsDiscardExtraShiv());
            addToTop((AbstractGameAction) new BGChooseOneAttackAction(attackChoices, null, null));
        }else{
            //if monsters ARE dead, then we're in the middle of a boss phase change
            // and the targetselectscreen will fail silently (and if useextrashiv was chosen, it won't decrement the shiv count)
            AbstractRelic relic= AbstractDungeon.player.getRelic("BoardGame:BGShivs");
            if(relic!=null){
                relic.counter=relic.counter-1;
            }
        }
        this.isDone = true;
    }
}


