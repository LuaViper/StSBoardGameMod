package BoardGame.actions;

import BoardGame.BoardGame;
import BoardGame.powers.BGTheDiePower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.monsterRng;

public class SpaghettiRerollDieAction extends AbstractGameAction {
    private AbstractMonster monster;

    public SpaghettiRerollDieAction(){

    }


    public void update() {
        int r=monsterRng.random(1,6);
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("ROLL THE DIE (WRITHING MASS): "+r);
        TheDie.monsterRoll=r;
        AbstractDungeon.actionManager.addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTheDiePower((AbstractCreature)AbstractDungeon.player, TheDie.monsterRoll), TheDie.monsterRoll));
        TheDie.setMonsterMoves(TheDie.monsterRoll);
        AbstractDungeon.player.hand.refreshHandLayout();
        this.isDone = true;
    }
}
