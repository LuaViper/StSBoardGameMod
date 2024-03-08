//TODO: see if we can reuse BGXCostCardAction without cloning it outright

package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.powers.BGDoubleTapPower_DEPRECATED;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGCopyAndPlayCardAction extends AbstractGameAction {

    AbstractCard card;
    int energySpent;
    boolean dontExpendResources;
    public BGCopyAndPlayCardAction(AbstractCard card, int energySpent, boolean dontExpendResources) {
        super();
        this.card=card;
        this.energySpent=energySpent;
        this.dontExpendResources=dontExpendResources;
    }

    public void update() {
        AbstractMonster m = null;
        AbstractCard tmp = card.makeSameInstanceOf();
        AbstractDungeon.player.limbo.addToBottom(tmp);
        tmp.current_x = card.current_x;
        tmp.current_y = card.current_y;
        tmp.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        tmp.target_y = Settings.HEIGHT / 2.0F;

        tmp.purgeOnUse = true;

        Logger logger = LogManager.getLogger(BGDoubleTapPower_DEPRECATED.class.getName());
        if(card instanceof AbstractBGCard){
            ((AbstractBGCard)card).copiedCard=(AbstractBGCard)tmp;
        }

        if(card.target== AbstractCard.CardTarget.ENEMY || card.target== AbstractCard.CardTarget.SELF_AND_ENEMY) {
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                if (target != null) {
                    tmp.calculateCardDamage(target);
                }
                addToBot((AbstractGameAction) new NewQueueCardAction(tmp, target, true, true));
            };
            addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for the copy of "+card.name+"."));
        }else{
            addToBot((AbstractGameAction) new NewQueueCardAction(tmp, null, true, true));
        }
        if (!this.dontExpendResources) {
            AbstractDungeon.player.energy.use(this.energySpent);
        }

        tickDuration();
        this.isDone=true;


    }


}
