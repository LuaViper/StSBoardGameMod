package BoardGame.actions;

import BoardGame.cards.AbstractAttackCardChoice;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class ChooseOneAttackAction
        extends AbstractGameAction {
    private ArrayList<AbstractCard> choices;
    private AbstractPlayer p;
    private AbstractMonster m;

    public ChooseOneAttackAction(ArrayList<AbstractAttackCardChoice> choices,AbstractPlayer p, AbstractMonster m) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.choices = new ArrayList<>();
        for(AbstractAttackCardChoice card : choices){
            card.setTargets(p,m);
            this.choices.add(card);
        }
        this.p=p;
        this.m=m;
    }


    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.chooseOneOpen(this.choices);
            tickDuration();

            return;
        }
        tickDuration();
    }
}


