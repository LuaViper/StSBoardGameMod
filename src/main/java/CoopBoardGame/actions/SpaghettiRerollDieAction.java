package CoopBoardGame.actions;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.monsterRng;

import CoopBoardGame.CoopBoardGame;
import CoopBoardGame.powers.BGTheDiePower;
import CoopBoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpaghettiRerollDieAction extends AbstractGameAction {

    public SpaghettiRerollDieAction() {}

    public void update() {
        int r = monsterRng.random(1, 6);
        final Logger logger = LogManager.getLogger(CoopBoardGame.class.getName());
        logger.info("ROLL THE DIE (WRITHING MASS): " + r);
        TheDie.monsterRoll = r;
        AbstractDungeon.actionManager.addToTop(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) AbstractDungeon.player,
                (AbstractCreature) AbstractDungeon.player,
                (AbstractPower) new BGTheDiePower(
                    (AbstractCreature) AbstractDungeon.player,
                    TheDie.monsterRoll
                ),
                TheDie.monsterRoll
            )
        );
        TheDie.setMonsterMoves(TheDie.monsterRoll);
        AbstractDungeon.player.hand.refreshHandLayout();
        this.isDone = true;
    }
}
