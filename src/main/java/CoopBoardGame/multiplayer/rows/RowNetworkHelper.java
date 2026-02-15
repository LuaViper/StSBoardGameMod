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
import java.util.List;

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
     * Payload format: [monsterId1, row1, monsterId2, row2, ...]
     * Uses monster index in the monster list as ID since monsters don't have unique IDs.
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
            // Build payload: [index, row, index, row, ...]
            List<Integer> payloadList = new ArrayList<>();

            if (AbstractDungeon.getMonsters() != null) {
                int index = 0;
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    int row = MultiCreature.Field.currentRow.get(m);
                    payloadList.add(index);
                    payloadList.add(row);
                    index++;
                }
            }

            int[] payload = payloadList.stream().mapToInt(Integer::intValue).toArray();
            sendMessage(MSG_ROW_ASSIGNMENTS, payload);
            logger.info("Sent monster row assignments for " + (payload.length / 2) + " monsters");

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
            List<Integer> playerIds = TogetherInSpireHelper.getAllPlayerIds();
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
            logger.info("Sent player row assignments for " + playerIds.size() + " players");

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
     */
    public static void onMonsterRowAssignmentsReceived(int[] data) {
        if (AbstractDungeon.getMonsters() == null) {
            logger.warn("Received monster row assignments but no monsters exist yet");
            return;
        }

        List<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;

        // Process pairs: [index, row, index, row, ...]
        for (int i = 0; i + 1 < data.length; i += 2) {
            int monsterIndex = data[i];
            int row = data[i + 1];

            if (monsterIndex >= 0 && monsterIndex < monsters.size()) {
                AbstractMonster m = monsters.get(monsterIndex);
                MultiCreature.Field.currentRow.set(m, row);
                logger.debug("Set monster " + monsterIndex + " (" + m.name + ") to row " + row);
            }
        }

        logger.info("Applied monster row assignments from host");
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
        // Nothing to reset currently
    }
}
