package BoardGame.actions;

import BoardGame.cards.BGColorless.BGDistilledChaosChoice;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class BGPlayThreeDrawnCardsAction extends AbstractGameAction {
    public BGPlayThreeDrawnCardsAction() {
        if (!Settings.FAST_MODE) {
            this.duration=Settings.ACTION_DUR_MED;
        } else {
            this.duration=Settings.ACTION_DUR_FASTER;
        }
        this.actionType = ActionType.WAIT;
        this.source = (AbstractCreature) AbstractDungeon.player;
    }


    public void update() {
        //AbstractDungeon.actionManager.addToTop((AbstractGameAction) new WaitAction(0.4F));

        tickDuration();

        if (this.isDone) {
            if(DrawCardAction.drawnCards.size()==1){
                addToBot(new BGPlayDrawnCardAction(false));
                return;
            }

            if(DrawCardAction.drawnCards.size()==2) {
                AbstractCard card1=DrawCardAction.drawnCards.get(0);
                AbstractCard card2=DrawCardAction.drawnCards.get(1);
                ArrayList<AbstractCard> choices=new ArrayList<>();
                choices.add(new BGDistilledChaosChoice(card1,card2,null));
                choices.add(new BGDistilledChaosChoice(card2,card1,null));
                AbstractDungeon.cardRewardScreen.chooseOneOpen(choices);
                return;
            }

            if(DrawCardAction.drawnCards.size()>=3) {
                AbstractCard card1=DrawCardAction.drawnCards.get(0);
                AbstractCard card2=DrawCardAction.drawnCards.get(1);
                AbstractCard card3=DrawCardAction.drawnCards.get(2);
                ArrayList<AbstractCard> choices=new ArrayList<>();
                choices.add(new BGDistilledChaosChoice(card1,card2,card3));
                choices.add(new BGDistilledChaosChoice(card1,card3,card2));
                choices.add(new BGDistilledChaosChoice(card2,card1,card3));
                choices.add(new BGDistilledChaosChoice(card2,card3,card1));
                choices.add(new BGDistilledChaosChoice(card3,card1,card2));
                choices.add(new BGDistilledChaosChoice(card3,card2,card1));
                AbstractDungeon.cardRewardScreen.chooseOneOpen(choices);
                return;
            }

        }
    }


}



