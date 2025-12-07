//TODO: see if we can reuse BGXCostCardAction without cloning it outright

package CoopBoardGame.actions;

import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGCopyAndPlayCardAction extends AbstractGameAction {

    AbstractCard card;
    int energySpent;
    boolean dontExpendResources;

    public BGCopyAndPlayCardAction(
        AbstractCard card,
        int energySpent,
        boolean dontExpendResources
    ) {
        super();
        this.card = card;
        this.energySpent = energySpent;
        this.dontExpendResources = dontExpendResources;
    }

    public void update() {
        AbstractCard tmp = this.card.makeSameInstanceOf();
        AbstractDungeon.player.limbo.addToBottom(tmp);
        tmp.current_x = this.card.current_x;
        tmp.current_y = this.card.current_y;
        tmp.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        tmp.target_y = Settings.HEIGHT / 2.0F;

        tmp.purgeOnUse = true;

        if (this.card instanceof AbstractBGCard) {
            ((AbstractBGCard) this.card).copiedCard = (AbstractBGCard) tmp;
        }

        if (
            this.card.target == AbstractCard.CardTarget.ENEMY ||
            this.card.target == AbstractCard.CardTarget.SELF_AND_ENEMY
        ) {
            TargetSelectScreen.TargetSelectAction tssAction = target -> {
                if (target != null) {
                    tmp.calculateCardDamage(target);
                }
                addToBot((AbstractGameAction) new NewQueueCardAction(tmp, target, true, true));
            };
            addToBot(
                (AbstractGameAction) new TargetSelectScreenAction(
                    tssAction,
                    "Choose a target for the copy of " + card.name + "."
                )
            );
        } else {
            addToBot((AbstractGameAction) new NewQueueCardAction(tmp, null, true, true));
        }
        if (!this.dontExpendResources) {
            AbstractDungeon.player.energy.use(this.energySpent);
        }

        tickDuration();
        this.isDone = true;
    }
}
