package CoopBoardGame.actions;

import CoopBoardGame.monsters.bgexordium.BGGremlinAngry;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AngryPower;

public class BGApplyAngerToGremlinsAction extends AbstractGameAction {

    private static final int ATTACK_AMOUNT = 1;
    private static final int STACK_AMOUNT = 1;

    public void update() {
        MonsterGroup dungeonMonsterGroup = AbstractDungeon.getMonsters();

        for (AbstractMonster monster : dungeonMonsterGroup.monsters) {
            if (monster instanceof BGGremlinAngry) {
                if (!monster.hasPower(AngryPower.POWER_ID)) {
                    addToBot(
                        (AbstractGameAction) new ApplyPowerAction(
                            monster,
                            monster,
                            new AngryPower(monster, ATTACK_AMOUNT),
                            STACK_AMOUNT
                        )
                    );
                }
            }
        }

        this.isDone = true;
    }
}
