package CoopBoardGame.actions;

import CoopBoardGame.monsters.bgcity.BGGremlinLeader;
import CoopBoardGame.monsters.bgexordium.BGGremlinAngry;
import CoopBoardGame.monsters.bgexordium.BGGremlinFat;
import CoopBoardGame.monsters.bgexordium.BGGremlinSneaky;
import CoopBoardGame.monsters.bgexordium.BGGremlinWizard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSpawnTwoGremlinsForGremlinLeaderAction extends AbstractGameAction {

    public static final Logger logger = LogManager.getLogger(
        BGSpawnTwoGremlinsForGremlinLeaderAction.class.getName()
    );
    public static final float[] POSX = new float[] { -366.0F, -170.0F };
    public static final float[] POSY = new float[] { -4.0F, 6.0F };
    public BGGremlinLeader leader;

    public BGSpawnTwoGremlinsForGremlinLeaderAction(BGGremlinLeader m) {
        this.leader = m;
    }

    private String getGremlinInSlot(int slot) {
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
        logger.info("checking gremlin slot " + slot);
        if (slot >= monsters.size() || monsters.get(slot) == null || (monsters.get(slot)).isDying) {
            logger.info("return " + this.leader.gremlintypes[slot]);
            return this.leader.gremlintypes[slot];
        }
        logger.info("...it's apparently occupied");
        return "";
    }

    public void update() {
        AbstractMonster gremlin;
        for (int i = 0; i <= 1; i++) {
            String type = getGremlinInSlot(i);

            gremlin = null;
            if (type == "BGGremlinAngry") {
                gremlin = new BGGremlinAngry(POSX[i], POSY[i], false);
            } else if (type == "BGGremlinSneaky") {
                gremlin = new BGGremlinSneaky(POSX[i], POSY[i], false);
            } else if (type == "BGGremlinFat") {
                gremlin = new BGGremlinFat(POSX[i], POSY[i]);
            } else if (type == "BGGremlinWizard") {
                gremlin = new BGGremlinWizard(POSX[i], POSY[i]);
            } else {
                //gremlin not dead; don't resummon
                logger.info("Not dead, don't resummon");
            }
            logger.info("Gremlin type: " + type);
            logger.info("Gremlin: " + gremlin);
            if (gremlin != null) AbstractDungeon.actionManager.addToTop(
                (AbstractGameAction) new SpawnMonsterAction(gremlin, false)
            );
        }
        AbstractDungeon.actionManager.addToBottom(
            (AbstractGameAction) new BGApplyAngerToGremlinsAction()
        );
        this.isDone = true;
    }
}
