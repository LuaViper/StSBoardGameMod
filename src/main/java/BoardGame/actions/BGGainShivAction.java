package BoardGame.actions;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

//TODO: apparently the game stops prompting for extra shivs if you kill Awakened One Phase 1 with one of them
//TODO: it is suspected that Cunning Potion will close BGEntropicBrew menu
public class BGGainShivAction extends AbstractGameAction {
    private int amount;


    public BGGainShivAction(int amount) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.amount=amount;
    }


    public void update() {
        if(!AbstractDungeon.player.hasRelic("BoardGame:BGShivs")) {
            AbstractRelic shivs = new BGShivs();
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), shivs);
            ((AbstractBGRelic)shivs).setupObtainedDuringCombat();
        }
        AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        for(int i=0;i<this.amount;i+=1){
            relic.counter=relic.counter+1;
        }
        //TODO: also check global token cap (5*number_of_Silents) (only relevant if prismatic shard)
        for(int i=relic.counter;i>5;i-=1){
            addToTop(new BGTooManyShivsAction());
        }




        this.isDone = true;


    }
}


