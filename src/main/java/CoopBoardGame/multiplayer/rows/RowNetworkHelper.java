package CoopBoardGame.multiplayer.rows;

// MultiCreature is in the same package
import CoopBoardGame.util.TogetherInSpireHelper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for synchronizing row assignments between host and clients
 * in TogetherInSpire multiplayer board game mode.
 */
public class RowNetworkHelper {

    private static final Logger logger = LogManager.getLogger(RowNetworkHelper.class.getName());

    // Message type identifiers for row synchronization
    public static final String MSG_ROW_ASSIGNMENTS = "BGRowAssignments";
    public static final String MSG_PLAYER_ROW_ASSIGNMENTS = "BGPlayerRowAssignments";

    // Cached reflection lookups (shared with VotingNetworkHelper pattern)
    private static Class<?> networkMessageClass = null;
    private static Class<?> p2pManagerClass = null;
    private static Method sendDataMethod = null;
    private static Constructor<?> networkMessageConstructor = null;
    private static boolean initialized = false;

    // Store pending row assignments for when monsters aren't loaded yet (timing issue with TogetherInSpire)
    private static String pendingMonsterRowAssignments = null;

    /**
     * Initialize reflection lookups for TogetherInSpire networking classes.
     */
    public static boolean initialize() {
        if (initialized) {
            return true;
        }

        if (!TogetherInSpireHelper.isTogetherInSpireLoaded()) {
            return false;
        }

        try {
            // Load classes
            networkMessageClass = Class.forName("spireTogether.util.NetworkMessage");
            p2pManagerClass = Class.forName("spireTogether.network.P2P.P2PManager");

            // NetworkMessage constructor: (String request, Object object, Integer senderID)
            networkMessageConstructor = networkMessageClass.getConstructor(
                String.class, Object.class, Integer.class
            );

            // Find P2PManager.SendData method
            for (Method method : p2pManagerClass.getMethods()) {
                if (method.getName().equals("SendData") && method.getParameterCount() == 1) {
                    sendDataMethod = method;
                    break;
                }
            }

            if (sendDataMethod == null) {
                logger.warn("Could not find P2PManager.SendData method");
                return false;
            }

            initialized = true;
            logger.info("RowNetworkHelper initialized successfully");
            return true;

        } catch (Exception e) {
            logger.error("Failed to initialize RowNetworkHelper: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends monster row assignments from host to all clients.
     * Called after spawning enemies in combat.
     *
     * Payload format: "monsterId:occurrence:row,monsterId:occurrence:row,..."
     * Uses monster ID + occurrence count for stable identification across host/client.
     * This is more reliable than index-based sync since TogetherInSpire may reorder monsters.
     */
    public static void sendMonsterRowAssignments() {
        if (!initialize()) {
            logger.warn("Cannot send row assignments - networking not initialized");
            return;
        }

        if (!TogetherInSpireHelper.isHost()) {
            logger.debug("Not host, skipping row assignment broadcast");
            return;
        }

        try {
            // Build payload: "id:occurrence:row,id:occurrence:row,..."
            // Track occurrence count per monster ID
            Map<String, Integer> occurrenceCounts = new HashMap<>();
            StringBuilder payloadBuilder = new StringBuilder();

            if (AbstractDungeon.getMonsters() != null) {
                boolean first = true;
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    // Skip CharacterEntities (remote players)
                    if (TogetherInSpireHelper.isCharacterEntity(m)) {
                        continue;
                    }

                    String monsterId = m.id;
                    int occurrence = occurrenceCounts.getOrDefault(monsterId, 0);
                    occurrenceCounts.put(monsterId, occurrence + 1);

                    int row = MultiCreature.Field.currentRow.get(m);

                    if (!first) {
                        payloadBuilder.append(",");
                    }
                    payloadBuilder.append(monsterId).append(":").append(occurrence).append(":").append(row);
                    first = false;

                    logger.debug("Sending row assignment: " + monsterId + ":" + occurrence + " -> row " + row);
                }
            }

            String payload = payloadBuilder.toString();
            sendMessage(MSG_ROW_ASSIGNMENTS, payload);
            logger.info("Sent monster row assignments: " + payload);

        } catch (Exception e) {
            logger.error("Failed to send monster row assignments: " + e.getMessage());
        }
    }

    /**
     * Sends player row assignments from host to all clients.
     *
     * Payload format: [playerId1, row1, playerId2, row2, ...]
     */
    public static void sendPlayerRowAssignments() {
        if (!initialize()) {
            logger.warn("Cannot send player row assignments - networking not initialized");
            return;
        }

        if (!TogetherInSpireHelper.isHost()) {
            logger.debug("Not host, skipping player row assignment broadcast");
            return;
        }

        try {
            // Use BG player IDs only, not all players
            List<Integer> playerIds = TogetherInSpireHelper.getBoardGamePlayerIds();
            List<Integer> payloadList = new ArrayList<>();

            // Assign rows based on player order (0-indexed)
            int row = 0;
            for (Integer playerId : playerIds) {
                payloadList.add(playerId);
                payloadList.add(row);
                row++;
            }

            int[] payload = payloadList.stream().mapToInt(Integer::intValue).toArray();
            sendMessage(MSG_PLAYER_ROW_ASSIGNMENTS, payload);
            logger.info("Sent player row assignments for " + playerIds.size() + " BG players");

        } catch (Exception e) {
            logger.error("Failed to send player row assignments: " + e.getMessage());
        }
    }

    /**
     * Creates and sends a NetworkMessage via P2PManager.SendData.
     */
    private static void sendMessage(String messageType, Object payload) throws Exception {
        // Get local player ID
        Integer senderId = TogetherInSpireHelper.getLocalPlayerId();

        // Create NetworkMessage(String request, Object object, Integer senderID)
        Object message = networkMessageConstructor.newInstance(messageType, payload, senderId);

        // Send via P2PManager.SendData(message)
        sendDataMethod.invoke(null, message);
    }

    /**
     * Processes received monster row assignments.
     * Called on clients when they receive MSG_ROW_ASSIGNMENTS from host.
     *
     * @param data String payload in format "monsterId:occurrence:row,monsterId:occurrence:row,..."
     */
    public static void onMonsterRowAssignmentsReceived(String data) {
        if (data == null || data.isEmpty()) {
            logger.warn("Received empty monster row assignments");
            return;
        }

        logger.info("Received monster row assignments: " + data);

        // Store for later - TogetherInSpire may not have synced monsters yet
        pendingMonsterRowAssignments = data;

        // Try to apply immediately if monsters exist
        applyPendingMonsterRowAssignments();
    }

    /**
     * Applies pending monster row assignments if monsters are available.
     * Called when row assignments are received and also after combat loads.
     */
    public static void applyPendingMonsterRowAssignments() {
        if (pendingMonsterRowAssignments == null) {
            return;
        }

        if (AbstractDungeon.getMonsters() == null || AbstractDungeon.getMonsters().monsters.isEmpty()) {
            logger.debug("Cannot apply pending row assignments - no monsters yet");
            return;
        }

        String data = pendingMonsterRowAssignments;

        // Build a map of (monsterId, occurrence) -> monster from local monsters
        Map<String, AbstractMonster> monsterByKey = new HashMap<>();
        Map<String, Integer> localOccurrenceCounts = new HashMap<>();

        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            // Skip CharacterEntities (remote players)
            if (TogetherInSpireHelper.isCharacterEntity(m)) {
                continue;
            }

            String monsterId = m.id;
            int occurrence = localOccurrenceCounts.getOrDefault(monsterId, 0);
            localOccurrenceCounts.put(monsterId, occurrence + 1);

            String key = monsterId + ":" + occurrence;
            monsterByKey.put(key, m);
        }

        // Parse payload and apply row assignments
        String[] entries = data.split(",");
        int applied = 0;
        int total = entries.length;
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length != 3) {
                logger.warn("Invalid row assignment entry: " + entry);
                continue;
            }

            String monsterId = parts[0];
            String occurrenceStr = parts[1];
            String rowStr = parts[2];

            try {
                int occurrence = Integer.parseInt(occurrenceStr);
                int row = Integer.parseInt(rowStr);

                String key = monsterId + ":" + occurrence;
                AbstractMonster m = monsterByKey.get(key);

                if (m != null) {
                    MultiCreature.Field.currentRow.set(m, row);
                    logger.debug("Set monster " + key + " (" + m.name + ") to row " + row);
                    applied++;
                } else {
                    logger.warn("Could not find monster for key: " + key);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid number in row assignment entry: " + entry);
            }
        }

        logger.info("Applied " + applied + "/" + total + " monster row assignments from host");

        // Only clear pending if we successfully applied ALL assignments
        // If some are missing, TogetherInSpire may still be syncing monsters
        if (applied == total) {
            pendingMonsterRowAssignments = null;
            logger.info("All monster row assignments applied successfully");
        } else {
            logger.debug("Still waiting for " + (total - applied) + " monsters to sync from TogetherInSpire");
        }
    }

    /**
     * Returns true if there are pending monster row assignments waiting to be applied.
     */
    public static boolean hasPendingMonsterRowAssignments() {
        return pendingMonsterRowAssignments != null;
    }

    /**
     * Processes received player row assignments.
     * Called on clients when they receive MSG_PLAYER_ROW_ASSIGNMENTS from host.
     */
    public static void onPlayerRowAssignmentsReceived(int[] data) {
        // Store player row assignments for later use
        // The actual player positioning is handled by PerspectiveSkewPatches
        // which reads from MultiCreature.Field.currentRow

        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();

        // Process pairs: [playerId, row, playerId, row, ...]
        for (int i = 0; i + 1 < data.length; i += 2) {
            int playerId = data[i];
            int row = data[i + 1];

            // If this is our row assignment, set it on the local player
            if (playerId == localPlayerId && AbstractDungeon.player != null) {
                MultiCreature.Field.currentRow.set(AbstractDungeon.player, row);
                logger.info("Set local player to row " + row);
            }

            // Store for other players too (for CharacterEntity rendering if needed)
            PlayerRowManager.setPlayerRow(playerId, row);
        }

        logger.info("Applied player row assignments from host");
    }

    /**
     * Resets the network helper state.
     */
    public static void reset() {
        pendingMonsterRowAssignments = null;
    }
}
