package CoopBoardGame.actions;

import CoopBoardGame.CoopBoardGame;
import CoopBoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.HandCheckAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckAfterUseCardAction extends AbstractGameAction {

    private AbstractBGCard fakeCard = null;
    private UseCardAction fakeCardAction = null;

    public CheckAfterUseCardAction(AbstractBGCard fakeCard, UseCardAction fakeCardAction) {
        this.fakeCard = fakeCard;
        this.fakeCardAction = fakeCardAction;
    }

    public void update() {
        final Logger logger = LogManager.getLogger(CoopBoardGame.class.getName());
        logger.info("CheckAfterUseCardAction activates");

        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (!fakeCard.dontTriggerOnUseCard) p.onAfterUseCard(fakeCard, fakeCardAction);
        }
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            for (AbstractPower p : m.powers) {
                if (!fakeCard.dontTriggerOnUseCard) p.onAfterUseCard(fakeCard, fakeCardAction);
            }
        }
        addToBot(new HandCheckAction());
        this.isDone = true;
    }
}
