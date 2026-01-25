package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.dungeons.BGExordium;
import CoopBoardGame.multicharacter.MultiCharacter;
import CoopBoardGame.multicharacter.MultiCreature;
import CoopBoardGame.multiplayer.rows.PlayerRowManager;
import CoopBoardGame.multiplayer.rows.RowNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiCombatEncounterPatches {

    private static final Logger logger = LogManager.getLogger(MultiCombatEncounterPatches.class.getName());

    @SpirePatch2(clz = MonsterRoom.class, method = "onPlayerEntry")
    public static class AddAdditionalEnemiesPatch {

        @SpirePostfixPatch
        public static void Foo() {
            // Determine number of player rows for enemy spawning
            int numRows = getNumberOfPlayerRows();

            // Initialize the CombatRowManager for multiplayer mode
            // (In MultiCharacter mode, this is done in MultiCharacter.preBattlePrep())
            if (numRows > 1 && !(AbstractDungeon.player instanceof MultiCharacter)) {
                logger.info("Initializing CombatRowManager for multiplayer mode with " + numRows + " rows");
                MultiCharacter.combatRowManager.resetForCombat();
            }

            if (numRows > 1) {
                logger.info("Spawning enemies for " + numRows + " player rows");

                // monsters need to end up in left-to-right, top-to-bottom order
                // we're assembling the rows from bottom-to-top, so we'll add each row right-to-left then reverse
                Collections.reverse(AbstractDungeon.getMonsters().monsters);

                for (int i = 1; i < numRows; i += 1) {
                    //TODO: need to add lastCombatMetricKey for each row
                    //TODO: better first encounter check, maybe
                    if (!AbstractDungeon.monsterList.isEmpty()) {
                        AbstractDungeon.monsterList.remove(0);
                    }
                    MonsterGroup newGroup = CardCrawlGame.dungeon.getMonsterForRoomCreation();
                    Collections.reverse(newGroup.monsters);
                    for (AbstractMonster m : newGroup.monsters) {
                        MultiCreature.Field.currentRow.set(m, i);
                        AbstractDungeon.getMonsters().add(m);
                        m.init();
                    }
                    logger.info("Spawned enemy group for row " + i);
                }
                Collections.reverse(AbstractDungeon.getMonsters().monsters);

                if (CardCrawlGame.dungeon instanceof BGExordium && AbstractDungeon.floorNum == 1) {
                    // if this was the first encounter of multiplayer board game,
                    // force switch to the strong enemies list by clearing entire list
                    AbstractDungeon.monsterList.clear();
                }
            } else if (CardCrawlGame.dungeon instanceof BGExordium && AbstractDungeon.floorNum == 1) {
                // if this was the first encounter of solo board game,
                // force switch to the strong enemies list by clearing all but index 0
                if (AbstractDungeon.monsterList.size() > 1) {
                    AbstractDungeon.monsterList
                        .subList(1, AbstractDungeon.monsterList.size())
                        .clear();
                }
            }

            // For multiplayer board game mode, assign player rows and broadcast all row assignments
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                // Assign rows to players (both host and clients need local assignment)
                PlayerRowManager.assignPlayerRows();

                // Host broadcasts row assignments to all clients
                if (TogetherInSpireHelper.isHost()) {
                    logger.info("Host broadcasting row assignments to clients");
                    RowNetworkHelper.sendPlayerRowAssignments();
                    RowNetworkHelper.sendMonsterRowAssignments();
                }
            }
        }

        /**
         * Determines the number of player rows for spawning enemies.
         * This supports both:
         * 1. Single player MultiCharacter mode (one player controlling multiple characters)
         * 2. TogetherInSpire multiplayer mode (multiple players each controlling a BG character)
         *
         * @return number of player rows (1 for standard single player)
         */
        private static int getNumberOfPlayerRows() {
            // Check for single player MultiCharacter mode
            if (AbstractDungeon.player instanceof MultiCharacter) {
                return MultiCharacter.getSubcharacters().size();
            }

            // Check for TogetherInSpire multiplayer with multiple BG players
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return TogetherInSpireHelper.getBoardGamePlayerCount();
            }

            // Standard single player
            return 1;
        }
    }
}
