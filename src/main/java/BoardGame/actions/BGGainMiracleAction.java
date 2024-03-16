package BoardGame.actions;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.BGColorless.BGShivsDiscardExtraShiv;
import BoardGame.cards.BGColorless.BGShivsUseExtraShiv;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGMiracles;
import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class BGGainMiracleAction extends AbstractGameAction {
    private int amount;
    private AbstractPlayer player;

    public BGGainMiracleAction(int amount) {
        this.duration = 0.0F;
        this.actionType = ActionType.WAIT;
        this.amount=amount;
    }


    public void update() {
        if(!AbstractDungeon.player.hasRelic("BoardGame:BGMiracles")) {
            AbstractRelic miracles = new BGMiracles();
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), miracles);
            ((AbstractBGRelic) miracles).setupObtainedDuringCombat();
        }

        AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGMiracles");
        for(int i=0;i<this.amount;i+=1){
            relic.counter=relic.counter+1;
        }
        if(relic.counter>5)relic.counter=5;
        //TODO: also check global token cap (5*number_of_Watchers) (only relevant if prismatic shard)

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


        this.isDone = true;


    }
}


