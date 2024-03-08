package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGUseMiracleAction extends AbstractGameAction {
    private AbstractPlayer player;
    private String message;

    public BGUseMiracleAction() {
        this.duration = 0.0F;
        this.actionType = ActionType.WAIT;
    }

    public void update() {
        AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGMiracles");

        if(relic!=null){
            if(relic.counter>0) {
                relic.counter-=1;
                addToBot((AbstractGameAction) new GainEnergyAction(1));

                for(AbstractCard c : AbstractDungeon.player.hand.group){
                    c.applyPowers();
                }
                for(AbstractCard c : AbstractDungeon.player.drawPile.group){
                    c.applyPowers();
                }
                for(AbstractCard c : AbstractDungeon.player.discardPile.group){
                    c.applyPowers();
                }
                for(AbstractCard c : AbstractDungeon.player.exhaustPile.group){
                    c.applyPowers();
                }
            }
        }



        this.isDone = true;


    }


}


