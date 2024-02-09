package BoardGame.actions;
import BoardGame.BoardGame;
import BoardGame.monsters.MixedAttacks;
import BoardGame.relics.BGShivs;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGGainShivAction extends AbstractGameAction {
    private int amount;
    private AbstractPlayer player;

    public BGGainShivAction(int amount) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.amount=amount;
    }


    public void update() {
        if(!AbstractDungeon.player.hasRelic("BoardGame:BGShivs"))
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), new BGShivs());
        AbstractRelic relic = AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        for(int i=0;i<this.amount;i+=1){
            relic.counter=relic.counter+1;
        }
        //TODO: also check global token cap (5*number_of_Silents) (only relevant if prismatic shard)
        for(int i=relic.counter;i>5;i-=1){
            addToTop(new BGUseShivAction(true,true,0));
        }




        this.isDone = true;


    }
}


