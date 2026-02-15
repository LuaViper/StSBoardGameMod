package CoopBoardGame.multiplayer.rows;

// MultiCreature is in the same package
import CoopBoardGame.util.TogetherInSpireHelper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages player row assignments for multiplayer board game mode.
 * Tracks which player is in which combat row.
 */
public class PlayerRowManager {

    private static final Logger logger = LogManager.getLogger(PlayerRowManager.class.getName());

    // Map of player ID to their assigned row
    private static final Map<Integer, Integer> playerRows = new HashMap<>();

    /**
     * Assigns rows to all players based on their order in the player list.
     * Should only be called by the host when combat starts.
     */
    public static void assignPlayerRows() {
        playerRows.clear();

        // Use BG player IDs only, not all players
        // Create a mutable copy to ensure sorting works (original list might be immutable)
        List<Integer> playerIds = new ArrayList<>(TogetherInSpireHelper.getBoardGamePlayerIds());
        logger.info("Player IDs before sorting: " + playerIds);

        // Sort player IDs to ensure consistent row assignments across all machines
        // Without sorting, each machine may iterate players in different order
        Collections.sort(playerIds);
        logger.info("Player IDs after sorting: " + playerIds);

        int row = 0;
        for (Integer playerId : playerIds) {
            playerRows.put(playerId, row);
            logger.info("Assigned BG player " + playerId + " to row " + row);
            row++;
        }

        // Set the local player's row
        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        if (playerRows.containsKey(localPlayerId) && AbstractDungeon.player != null) {
            int localRow = playerRows.get(localPlayerId);
            MultiCreature.Field.currentRow.set(AbstractDungeon.player, localRow);
            logger.info("Set local player (ID " + localPlayerId + ") to row " + localRow);
        }
    }

    /**
     * Sets a specific player's row assignment.
     * Used when receiving row assignments from the host.
     */
    public static void setPlayerRow(int playerId, int row) {
        playerRows.put(playerId, row);
    }

    /**
     * Gets the row for a specific player ID.
     */
    public static int getPlayerRow(int playerId) {
        return playerRows.getOrDefault(playerId, 0);
    }

    /**
     * Gets the row for the local player.
     */
    public static int getLocalPlayerRow() {
        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        return getPlayerRow(localPlayerId);
    }

    /**
     * Gets the total number of player rows.
     */
    public static int getRowCount() {
        return playerRows.size();
    }

    /**
     * Resets all row assignments.
     * Called at the start of a new combat or when leaving multiplayer.
     */
    public static void reset() {
        playerRows.clear();
    }

    /**
     * Checks if row assignments have been made.
     */
    public static boolean hasAssignments() {
        return !playerRows.isEmpty();
    }
}
