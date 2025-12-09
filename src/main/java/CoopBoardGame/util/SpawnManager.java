package CoopBoardGame.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * Utility class for managing monster spawning in a multiplayer-safe way.
 *
 * In TogetherInSpire multiplayer, when a client loads a room from network sync,
 * the spawned monsters are already included in the room data. However, the
 * leader monster's usePreBattleAction() still gets called, which would cause
 * duplicate spawns if not handled properly.
 *
 * This class provides a way to check if spawning should occur by counting
 * the current number of alive monsters in the encounter.
 */
public class SpawnManager {

    /**
     * Checks if the current monster group has only a single alive monster.
     * This is useful for leader monsters that spawn minions - if there are
     * already multiple monsters (from network sync), we shouldn't spawn more.
     *
     * @return true if there is only one alive monster (the leader), false otherwise
     */
    public static boolean shouldSpawnMinions() {
        int aliveMonsterCount = 0;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDying && !m.isEscaping) {
                aliveMonsterCount++;
            }
        }
        return aliveMonsterCount <= 1;
    }

    /**
     * Adds a spawn action to the action manager only if spawning should occur.
     * This is a convenience method that combines the check and action queueing.
     *
     * @param spawnAction The spawn action to add (typically a BGSpawnTwoGremlinsAction or similar)
     */
    public static void spawnIfNeeded(AbstractGameAction spawnAction) {
        if (shouldSpawnMinions()) {
            AbstractDungeon.actionManager.addToBottom(spawnAction);
        }
    }
}
