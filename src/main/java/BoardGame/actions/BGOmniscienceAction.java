
package BoardGame.actions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.cards.AbstractCard.CardType.ATTACK;
import static com.megacrit.cardcrawl.cards.AbstractCard.CardType.SKILL;

public class BGOmniscienceAction
        extends AbstractGameAction {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("WishAction")).TEXT;

    private AbstractPlayer player;

    private int playAmt;

    public BGOmniscienceAction(int playAmt) {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.player = AbstractDungeon.player;
        this.playAmt = playAmt;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            if (this.player.drawPile.isEmpty()) {
                this.isDone = true;
                return;
            }
            CardGroup temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : this.player.drawPile.group) {
                if(c.type==ATTACK || c.type==SKILL)
                temp.addToTop(c);
            }
            temp.sortAlphabetically(true);
            temp.sortByRarityPlusStatusCardType(false);
            AbstractDungeon.gridSelectScreen.open(temp, 1, TEXT[0], false);
            tickDuration();
            return;
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard originalCard : AbstractDungeon.gridSelectScreen.selectedCards) {
                originalCard.exhaust = true;
                AbstractDungeon.player.drawPile.group.remove(originalCard);
                (AbstractDungeon.getCurrRoom()).souls.remove(originalCard);

                AbstractCard copiedCard = originalCard.makeSameInstanceOf();
                //unlike DoubleAttackPower series, we haven't queued any cards yet

                AbstractDungeon.player.limbo.addToTop(copiedCard);
                copiedCard.current_x = originalCard.current_x;
                copiedCard.current_y = originalCard.current_y;
                copiedCard.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
                copiedCard.target_y = Settings.HEIGHT / 2.0F;
                copiedCard.purgeOnUse = true;
                if(originalCard instanceof AbstractBGCard){
                    ((AbstractBGCard)originalCard).copiedCard=(AbstractBGCard)copiedCard;
                }

                //((AbstractBGCard)copiedCard).followUpCardChain=new ArrayList<>(Arrays.asList(originalCard));
                ((AbstractBGCard)copiedCard).followUpCardChain=new ArrayList<>(Collections.singletonList(originalCard));

                if (copiedCard.target == AbstractCard.CardTarget.ENEMY || copiedCard.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
                    TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                        addToBot((AbstractGameAction) new NewQueueCardAction(copiedCard, target, false, true));
                    };
                    addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for " + copiedCard.name + "."));
                }else{
                    addToBot((AbstractGameAction) new NewQueueCardAction(copiedCard, target, false, true));
                }



//                if (c.target == AbstractCard.CardTarget.ENEMY || c.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
//                    TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
//                        addToBot((AbstractGameAction) new NewQueueCardAction(c, target, false, true));
//                    };
//                    addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for " + c.name + "."));
//                }else{
//                    addToBot((AbstractGameAction) new NewQueueCardAction(c, target, false, true));
//                }
//
//                for (int i = 0; i < this.playAmt - 1; i++) {
//                    if(!(c instanceof AbstractBGCard)) {
//                        AbstractCard tmp = c.makeStatEquivalentCopy();
//                        tmp.purgeOnUse = true;
//                        addToBot((AbstractGameAction) new NewQueueCardAction(tmp, target, false, true));
//                    }else{
//                        ArrayList<AbstractCard>followUp = new ArrayList<>();
//                        AbstractCard tmp = c.makeStatEquivalentCopy();
//                        tmp.purgeOnUse=true;
//                        followUp.add(tmp);
//                        ((AbstractBGCard) c).followUpCardChain=followUp;
//                    }
//                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.player.hand.refreshHandLayout();
        }
        tickDuration();
    }
}

