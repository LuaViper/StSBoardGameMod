package CoopBoardGame.multiplayer.rows;

// MultiCreature is in the same package
import CoopBoardGame.util.TogetherInSpireHelper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages player row assignments for multiplayer board game mode.
 * Tracks which player is in which combat row.
 */
public class PlayerRowManager {

    private static final Logger logger = LogManager.getLogger(PlayerRowManager.class.getName());

    // Map of player ID to their assigned row
    private static final Map<Integer, Integer> playerRows = new HashMap<>();
    // Authoritative local row snapshot from host assignment.
    private static Integer localAssignedRow = null;

    /**
     * Assigns rows to all players based on their order in the player list.
     * Should only be called by the host when combat starts.
     */
    public static void assignPlayerRows() {
        playerRows.clear();
        localAssignedRow = null;

        // Use BG player IDs only, not all players
        // Preserve host join order (first joined = bottom row).
        List<Integer> playerIds = new ArrayList<>(TogetherInSpireHelper.getBoardGamePlayerIds());
        logger.info("Assigning rows using host BG join order: " + playerIds);

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
            setLocalPlayerRow(localRow);
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
     * Sets authoritative local player row (from host assignment).
     */
    public static void setLocalPlayerRow(int row) {
        localAssignedRow = row;
        if (AbstractDungeon.player != null) {
            MultiCreature.Field.currentRow.set(AbstractDungeon.player, row);
        }
    }

    /**
     * Returns true if a row assignment already exists for the given player.
     */
    public static boolean hasPlayerRow(int playerId) {
        return playerRows.containsKey(playerId);
    }

    /**
     * Ensures all specified players have row assignments without clearing existing rows.
     * Missing players are assigned to the next available row in sorted player-id order.
     */
    public static void ensurePlayerRowsForIds(List<Integer> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return;
        }

        List<Integer> sortedIds = new ArrayList<>(playerIds);
        Collections.sort(sortedIds);

        Set<Integer> usedRows = new HashSet<>();
        for (Integer playerId : sortedIds) {
            if (playerRows.containsKey(playerId)) {
                usedRows.add(playerRows.get(playerId));
            }
        }

        int nextRow = 0;
        for (Integer playerId : sortedIds) {
            if (playerRows.containsKey(playerId)) {
                continue;
            }

            while (usedRows.contains(nextRow)) {
                nextRow++;
            }

            playerRows.put(playerId, nextRow);
            usedRows.add(nextRow);
            logger.info("Assigned missing BG player " + playerId + " to row " + nextRow);
        }

        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        if (localAssignedRow == null && playerRows.containsKey(localPlayerId) && AbstractDungeon.player != null) {
            int localRow = playerRows.get(localPlayerId);
            setLocalPlayerRow(localRow);
        }
    }

    /**
     * Gets the row for a specific player ID.
     */
    public static int getPlayerRow(int playerId) {
        return playerRows.getOrDefault(playerId, 0);
    }

    /**
     * Gets the player ID assigned to a specific row.
     *
     * @param row row index
     * @return player ID for that row, or null if no player is assigned
     */
    public static Integer getPlayerIdForRow(int row) {
        for (Map.Entry<Integer, Integer> entry : playerRows.entrySet()) {
            if (entry.getValue() == row) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Gets the row for the local player.
     */
    public static int getLocalPlayerRow() {
        if (localAssignedRow != null) {
            return localAssignedRow;
        }
        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        return getPlayerRow(localPlayerId);
    }

    /**
     * Returns true when authoritative local row is known.
     */
    public static boolean hasLocalPlayerRow() {
        return localAssignedRow != null;
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
        localAssignedRow = null;
    }

    /**
     * Checks if row assignments have been made.
     */
    public static boolean hasAssignments() {
        return !playerRows.isEmpty();
    }
}
