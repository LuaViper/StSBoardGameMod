package CoopBoardGame.actions;

import CoopBoardGame.cards.AbstractBGAttackCardChoice;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.ArrayList;

public class BGChooseOneAttackAction extends AbstractGameAction {

    private ArrayList<AbstractCard> choices;
    boolean cardSelected = false;

    public BGChooseOneAttackAction(
        ArrayList<AbstractBGAttackCardChoice> choices,
        AbstractPlayer player,
        AbstractMonster monster
    ) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.choices = new ArrayList<>();
        for (AbstractBGAttackCardChoice card : choices) {
            card.setTargets(player, monster);
            this.choices.add(card);
        }
    }

    public void update() {
        tickDuration();
        
        if (!this.cardSelected) {
            AbstractDungeon.cardRewardScreen.chooseOneOpen(this.choices);

            this.cardSelected = true;

            return;
        }
    }
}
