package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multiplayer.rows.RowNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * Patches TogetherInSpire's message analyzer to intercept row assignment messages.
 * This allows clients to receive row assignments from the host for proper
 * player and monster positioning in board game multiplayer mode.
 */
public class RowMessagePatch {

    private static final Logger logger = LogManager.getLogger(RowMessagePatch.class.getName());

    /**
     * Patch to intercept incoming network messages and handle row assignment ones.
     *
     * We patch P2PMessageAnalyzer.AnalyzeMessage to check for our custom message types
     * before they're processed by the normal TogetherInSpire handlers.
     */
    @SpirePatch(
        cls = "spireTogether.network.P2P.P2PMessageAnalyzer",
        method = "AnalyzeMessage",
        requiredModId = "spireTogether"
    )
    public static class InterceptRowMessagesPatch {

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

                // Check if this is one of our row messages
                if (request == null) {
                    return SpireReturn.Continue();
                }

                switch (request) {
                    case RowNetworkHelper.MSG_ROW_ASSIGNMENTS:
                        handleMonsterRowAssignments(msg);
                        return SpireReturn.Return(); // We handled it, skip normal processing

                    case RowNetworkHelper.MSG_PLAYER_ROW_ASSIGNMENTS:
                        handlePlayerRowAssignments(msg);
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

        private static void handleMonsterRowAssignments(Object message) {
            try {
                Class<?> messageClass = message.getClass();

                // Get the payload (int array)
                Field objectField = messageClass.getDeclaredField("object");
                objectField.setAccessible(true);
                Object payload = objectField.get(message);

                if (payload instanceof int[]) {
                    int[] data = (int[]) payload;
                    logger.info("Received monster row assignments: " + data.length / 2 + " monsters");
                    RowNetworkHelper.onMonsterRowAssignmentsReceived(data);
                }
            } catch (Exception e) {
                logger.error("Error handling monster row assignments message: " + e.getMessage());
            }
        }

        private static void handlePlayerRowAssignments(Object message) {
            try {
                Class<?> messageClass = message.getClass();

                Field objectField = messageClass.getDeclaredField("object");
                objectField.setAccessible(true);
                Object payload = objectField.get(message);

                if (payload instanceof int[]) {
                    int[] data = (int[]) payload;
                    logger.info("Received player row assignments: " + data.length / 2 + " players");
                    RowNetworkHelper.onPlayerRowAssignmentsReceived(data);
                }
            } catch (Exception e) {
                logger.error("Error handling player row assignments message: " + e.getMessage());
            }
        }
    }
}
