package CoopBoardGame.actions;

import CoopBoardGame.dungeons.BGExordium;
import CoopBoardGame.monsters.bgexordium.BGGremlinAngry;
import CoopBoardGame.monsters.bgexordium.BGGremlinFat;
import CoopBoardGame.monsters.bgexordium.BGGremlinSneaky;
import CoopBoardGame.monsters.bgexordium.BGGremlinWizard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSpawnTwoGremlinsAction extends AbstractGameAction {

    private String[] gremlins = new String[2];
    public static final float[] POSX = new float[] { -160.0F, -10.0F, 200.0F };
    public static final float[] POSY = new float[] { -4.0F, 6.0F, 0.0F };

    public BGSpawnTwoGremlinsAction() {}

    public void update() {
        this.gremlins = BGExordium.pickTwoGremlins();
        for (int i = 0; i <= 1; i++) {
            AbstractMonster gremlin;
            if (this.gremlins[i].equals("BGGremlinAngry")) {
                gremlin = new BGGremlinAngry(POSX[i + 1], POSY[i + 1], false);
            } else if (this.gremlins[i].equals("BGGremlinSneaky")) {
                gremlin = new BGGremlinSneaky(POSX[i + 1], POSY[i + 1], false);
            } else if (this.gremlins[i].equals("BGGremlinFat")) {
                gremlin = new BGGremlinFat(POSX[i + 1], POSY[i + 1]);
            } else if (this.gremlins[i].equals("BGGremlinWizard")) {
                gremlin = new BGGremlinWizard(POSX[i + 1], POSY[i + 1]);
            } else {
                gremlin = new BGGremlinSneaky(POSX[i + 1], POSY[i + 1], false);
            }
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new SpawnMonsterAction(gremlin, false)
            );
        }
        AbstractDungeon.actionManager.addToBottom(
            (AbstractGameAction) new BGApplyAngerToGremlinsAction()
        );
        this.isDone = true;
    }
}
