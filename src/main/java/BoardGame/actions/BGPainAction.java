package BoardGame.actions;

import BoardGame.BoardGame;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGPainAction extends AbstractGameAction {
    private int cardsInHand=0;
    private AbstractPlayer player;
    public BGPainAction(AbstractPlayer player, int cardsInHand) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.player=player;
        this.cardsInHand=cardsInHand;
        //this.targetMonster = m;
    }
    public void update() {
        Logger logger = LogManager.getLogger(BGPainAction.class.getName());
        //logger.info("HAND SIZE CHECK: "+cardsInHand);
        if(cardsInHand<=2){
            addToTop((AbstractGameAction)new LoseHPAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 1));
        }
        this.isDone = true;
    }
}
