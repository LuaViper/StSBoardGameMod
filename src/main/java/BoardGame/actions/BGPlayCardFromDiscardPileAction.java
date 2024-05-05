package BoardGame.actions;

import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGPlayCardFromDiscardPileAction extends AbstractGameAction {
    private AbstractCard card;
    private boolean exhaustCards;

    public BGPlayCardFromDiscardPileAction(AbstractCard weave) {

        if (!Settings.FAST_MODE) {
            this.duration=Settings.ACTION_DUR_MED;
        } else {
            this.duration=Settings.ACTION_DUR_FASTER;
        }
        this.actionType = ActionType.WAIT;
        this.source = (AbstractCreature) AbstractDungeon.player;
        this.card = weave;
    }


    public void update() {
        //AbstractDungeon.actionManager.addToTop((AbstractGameAction) new WaitAction(0.4F));

        tickDuration();

        if (this.isDone) {
//                   if (c.costForTurn != 0 && !c.freeToPlayOnce) {
//                       AbstractDungeon.player.hand.moveToDiscardPile(c);
//                       c.triggerOnManualDiscard();
//                       GameActionManager.incrementDiscard(false);
//                   }
            card.exhaustOnUseOnce = this.exhaustCards;
            AbstractDungeon.player.limbo.group.add(card);
            card.current_y = -200.0F * Settings.scale;
            card.target_x = Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
            card.target_y = Settings.HEIGHT / 2.0F;
            card.targetAngle = 0.0F;
            card.lighten(false);
            card.drawScale = 0.12F;
            card.targetDrawScale = 0.75F;

            card.applyPowers();

            if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                    if (target != null) {
                        card.calculateCardDamage(target);
                    }
                    addToTop((AbstractGameAction) new NewQueueCardAction(card, target, false, true));
                    addToTop((AbstractGameAction) new UnlimboAction(card));
//                        if (!Settings.FAST_MODE) {
//                            addToTop((AbstractGameAction) new WaitAction(Settings.ACTION_DUR_MED));
//                        } else {
//                            addToTop((AbstractGameAction) new WaitAction(Settings.ACTION_DUR_FASTER));
//                        }
                };
                addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for " + card.name + "."));
            } else {
                addToTop((AbstractGameAction) new NewQueueCardAction(card, null, false, true));
                addToTop((AbstractGameAction) new UnlimboAction(card));
            }
        }
    }


}



