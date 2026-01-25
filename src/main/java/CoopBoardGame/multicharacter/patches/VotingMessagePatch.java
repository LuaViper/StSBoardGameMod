package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multiplayer.voting.VotingNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * Patches TogetherInSpire's message analyzer to intercept voting-related messages.
 * This allows us to receive custom message types for the room voting system.
 */
public class VotingMessagePatch {

    private static final Logger logger = LogManager.getLogger(VotingMessagePatch.class.getName());

    /**
     * Patch to intercept incoming network messages and handle voting-related ones.
     *
     * We patch P2PMessageAnalyzer.AnalyzeMessage to check for our custom message types
     * before they're processed by the normal TogetherInSpire handlers.
     *
     * Using SpirePatch (not SpirePatch2) for more flexible parameter handling.
     */
    @SpirePatch(
        cls = "spireTogether.network.P2P.P2PMessageAnalyzer",
        method = "AnalyzeMessage",
        requiredModId = "spireTogether"
    )
    public static class InterceptVotingMessagesPatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(Object msg) {
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return SpireReturn.Continue();
            }

            try {
                // Get the message request type
                Class<?> messageClass = msg.getClass();
                Field requestField = messageClass.getDeclaredField("request");
                requestField.setAccessible(true);
                String request = (String) requestField.get(msg);

                // Check if this is one of our voting messages
                if (request == null) {
                    return SpireReturn.Continue();
                }

                switch (request) {
                    case VotingNetworkHelper.MSG_VOTE_CAST:
                        handleVoteCast(msg);
                        return SpireReturn.Return(); // We handled it, skip normal processing

                    case VotingNetworkHelper.MSG_VOTE_CLEAR:
                        handleVoteClear(msg);
                        return SpireReturn.Return();

                    case VotingNetworkHelper.MSG_VOTING_RESOLVED:
                        handleVotingResolved(msg);
                        return SpireReturn.Return();

                    case VotingNetworkHelper.MSG_ENTER_ROOM:
                        handleEnterRoom(msg);
                        return SpireReturn.Return();

                    default:
                        // Not our message, let TogetherInSpire handle it
                        return SpireReturn.Continue();
                }

            } catch (Exception e) {
                logger.debug("Error checking message type: " + e.getMessage());
                return SpireReturn.Continue();
            }
        }

        private static void handleVoteCast(Object message) {
            try {
                Class<?> messageClass = message.getClass();

                // Get the payload (int array)
                Field objectField = messageClass.getDeclaredField("object");
                objectField.setAccessible(true);
                Object payload = objectField.get(message);

                if (payload instanceof int[]) {
                    int[] data = (int[]) payload;
                    if (data.length >= 5) {
                        int playerId = data[0];
                        int nodeX = data[1];
                        int nodeY = data[2];
                        // Reconstruct timestamp from two ints
                        long timestampLow = data[3] & 0xFFFFFFFFL;
                        long timestampHigh = data[4] & 0xFFFFFFFFL;
                        long timestamp = timestampLow | (timestampHigh << 32);

                        logger.info("Received vote cast from player " + playerId + " for node (" + nodeX + ", " + nodeY + ")");
                        VotingNetworkHelper.onVoteCastReceived(playerId, nodeX, nodeY, timestamp);
                    }
                }
            } catch (Exception e) {
                logger.error("Error handling vote cast message: " + e.getMessage());
            }
        }

        private static void handleVoteClear(Object message) {
            try {
                Class<?> messageClass = message.getClass();

                Field objectField = messageClass.getDeclaredField("object");
                objectField.setAccessible(true);
                Object payload = objectField.get(message);

                if (payload instanceof Integer) {
                    int playerId = (Integer) payload;
                    logger.info("Received vote clear from player " + playerId);
                    VotingNetworkHelper.onVoteClearReceived(playerId);
                }
            } catch (Exception e) {
                logger.error("Error handling vote clear message: " + e.getMessage());
            }
        }

        private static void handleVotingResolved(Object message) {
            try {
                Class<?> messageClass = message.getClass();

                Field objectField = messageClass.getDeclaredField("object");
                objectField.setAccessible(true);
                Object payload = objectField.get(message);

                if (payload instanceof int[]) {
                    int[] data = (int[]) payload;
                    if (data.length >= 2) {
                        int nodeX = data[0];
                        int nodeY = data[1];
                        logger.info("Received voting resolved: winning node (" + nodeX + ", " + nodeY + ")");
                        VotingNetworkHelper.onVotingResolvedReceived(nodeX, nodeY);
                    }
                }
            } catch (Exception e) {
                logger.error("Error handling voting resolved message: " + e.getMessage());
            }
        }

        private static void handleEnterRoom(Object message) {
            try {
                Class<?> messageClass = message.getClass();

                Field objectField = messageClass.getDeclaredField("object");
                objectField.setAccessible(true);
                Object payload = objectField.get(message);

                if (payload instanceof int[]) {
                    int[] data = (int[]) payload;
                    if (data.length >= 2) {
                        int nodeX = data[0];
                        int nodeY = data[1];
                        logger.info("Received enter room command for node (" + nodeX + ", " + nodeY + ")");
                        VotingNetworkHelper.onEnterRoomReceived(nodeX, nodeY);
                    }
                }
            } catch (Exception e) {
                logger.error("Error handling enter room message: " + e.getMessage());
            }
        }
    }
}
