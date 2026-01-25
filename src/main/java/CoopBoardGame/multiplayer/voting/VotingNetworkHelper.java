package CoopBoardGame.multiplayer.voting;

import CoopBoardGame.util.TogetherInSpireHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Helper class for sending and receiving voting-related network messages
 * through TogetherInSpire's networking infrastructure via reflection.
 */
public class VotingNetworkHelper {

    private static final Logger logger = LogManager.getLogger(VotingNetworkHelper.class.getName());

    // Message type identifiers for our custom voting messages
    public static final String MSG_VOTE_CAST = "BGRoomVoteCast";
    public static final String MSG_VOTE_CLEAR = "BGRoomVoteClear";
    public static final String MSG_VOTING_RESOLVED = "BGVotingResolved";
    public static final String MSG_ENTER_ROOM = "BGEnterRoom";

    // Cached reflection lookups
    private static Class<?> networkMessageClass = null;
    private static Class<?> p2pManagerClass = null;
    private static Class<?> integrationClass = null;
    private static Method sendDataMethod = null;
    private static Constructor<?> networkMessageConstructor = null;
    private static boolean initialized = false;

    // Polling interval for checking incoming messages (milliseconds)
    private static long lastPollTime = 0;
    private static final long POLL_INTERVAL = 100;

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
            integrationClass = Class.forName("spireTogether.network.Integration");

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
            logger.info("VotingNetworkHelper initialized successfully");
            return true;

        } catch (Exception e) {
            logger.error("Failed to initialize VotingNetworkHelper: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends a vote cast message to all other players.
     */
    public static void sendVoteCast(RoomVote vote) {
        if (!initialize()) {
            logger.warn("Cannot send vote - networking not initialized");
            return;
        }

        try {
            // Serialize vote data as int array: [playerId, nodeX, nodeY, timestamp (low), timestamp (high)]
            int[] payload = new int[] {
                vote.getPlayerId(),
                vote.getNodeX(),
                vote.getNodeY(),
                (int) (vote.getTimestamp() & 0xFFFFFFFF),
                (int) (vote.getTimestamp() >> 32)
            };

            sendMessage(MSG_VOTE_CAST, payload);
            logger.info("Sent vote cast message: " + vote);

        } catch (Exception e) {
            logger.error("Failed to send vote cast: " + e.getMessage());
        }
    }

    /**
     * Sends a vote clear message to all other players.
     */
    public static void sendVoteClear(int playerId) {
        if (!initialize()) {
            return;
        }

        try {
            sendMessage(MSG_VOTE_CLEAR, playerId);
            logger.info("Sent vote clear message for player " + playerId);

        } catch (Exception e) {
            logger.error("Failed to send vote clear: " + e.getMessage());
        }
    }

    /**
     * Sends a voting resolved message from host to all players.
     * @param winningNodeX X coordinate of winning node
     * @param winningNodeY Y coordinate of winning node
     */
    public static void sendVotingResolved(int winningNodeX, int winningNodeY) {
        if (!initialize()) {
            return;
        }

        try {
            int[] payload = new int[] { winningNodeX, winningNodeY };
            sendMessage(MSG_VOTING_RESOLVED, payload);
            logger.info("Sent voting resolved message: winning node (" + winningNodeX + ", " + winningNodeY + ")");

        } catch (Exception e) {
            logger.error("Failed to send voting resolved: " + e.getMessage());
        }
    }

    /**
     * Sends a room entry command from host to all players.
     */
    public static void sendEnterRoom(int nodeX, int nodeY) {
        if (!initialize()) {
            return;
        }

        try {
            int[] payload = new int[] { nodeX, nodeY };
            sendMessage(MSG_ENTER_ROOM, payload);
            logger.info("Sent enter room message: (" + nodeX + ", " + nodeY + ")");

        } catch (Exception e) {
            logger.error("Failed to send enter room: " + e.getMessage());
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
     * Polls for incoming messages and processes any voting-related ones.
     * Should be called periodically from update().
     *
     * Note: This is a workaround since we can't easily hook into TogetherInSpire's
     * callback system without modifying it. In a real implementation, you'd want
     * to add proper callback hooks in TogetherInSpire itself.
     */
    public static void pollForMessages() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPollTime < POLL_INTERVAL) {
            return;
        }
        lastPollTime = currentTime;

        if (!initialize()) {
            return;
        }

        try {
            // Try to get pending messages from the message queue
            // This approach depends on TogetherInSpire's internal structure

            // Alternative: Check P2PPlayer fields for vote data that was broadcast
            // by other players using ChangedPlayerData or a custom field

            checkForRemoteVotes();

        } catch (Exception e) {
            logger.debug("Error polling for messages: " + e.getMessage());
        }
    }

    /**
     * Checks P2PPlayer objects for any vote data that was broadcast.
     * This is a polling-based approach to receive votes from other players.
     */
    private static void checkForRemoteVotes() {
        try {
            // Get all P2PPlayer objects from P2PManager.players
            Field playersField = p2pManagerClass.getDeclaredField("players");
            playersField.setAccessible(true);
            Object players = playersField.get(null);

            if (!(players instanceof List)) {
                return;
            }

            List<?> playerList = (List<?>) players;

            // Get P2PPlayer class for field access
            Class<?> p2pPlayerClass = Class.forName("spireTogether.network.P2P.P2PPlayer");

            // Check each player's location to infer their "vote"
            // In the actual implementation, we would need a custom field or message handling
            // For now, we'll use a different approach - see below

        } catch (Exception e) {
            logger.debug("Error checking for remote votes: " + e.getMessage());
        }
    }

    /**
     * Processes a received vote cast message.
     * Called when we detect an incoming BGRoomVoteCast message.
     */
    public static void onVoteCastReceived(int playerId, int nodeX, int nodeY, long timestamp) {
        RoomVote vote = new RoomVote(playerId, nodeX, nodeY, timestamp);
        RoomVotingManager.getInstance().receiveRemoteVote(vote);
    }

    /**
     * Processes a received vote clear message.
     */
    public static void onVoteClearReceived(int playerId) {
        RoomVotingManager.getInstance().clearVote(playerId);
    }

    /**
     * Processes a received voting resolved message.
     */
    public static void onVotingResolvedReceived(int nodeX, int nodeY) {
        RoomVotingManager.getInstance().setWinningNode(nodeX, nodeY);
    }

    /**
     * Processes a received enter room message.
     */
    public static void onEnterRoomReceived(int nodeX, int nodeY) {
        RoomVotingManager.getInstance().receiveEnterRoom(nodeX, nodeY);
    }

    /**
     * Resets the network helper state.
     */
    public static void reset() {
        lastPollTime = 0;
    }
}
