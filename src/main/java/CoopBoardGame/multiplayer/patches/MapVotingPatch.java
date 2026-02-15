package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.voting.RoomVotingManager;
import CoopBoardGame.ui.map.MapVoteRenderer;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Patches for the voting-based room selection system.
 * Intercepts map node clicks to cast votes instead of entering rooms,
 * and renders vote markers on the map.
 */
public class MapVotingPatch {

    private static final Logger logger = LogManager.getLogger(MapVotingPatch.class.getName());

    /**
     * Patch to intercept when AbstractDungeon.nextRoom is about to be set.
     * This is called when a player clicks on a valid map node to enter it.
     * We intercept this to cast a vote instead.
     */
    @SpirePatch2(clz = AbstractDungeon.class, method = "nextRoomTransitionStart")
    public static class InterceptRoomTransitionPatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix() {
            // Only intercept in multiplayer board game mode
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return SpireReturn.Continue();
            }

            // Check if voting system should block normal room entry
            RoomVotingManager manager = RoomVotingManager.getInstance();
            if (!manager.shouldBlockRoomEntry()) {
                return SpireReturn.Continue();
            }

            // Get the node that was clicked (stored in AbstractDungeon.nextRoom)
            MapRoomNode clickedNode = AbstractDungeon.nextRoom;
            if (clickedNode != null) {
                logger.info("Intercepted room transition, casting vote for node (" + clickedNode.x + ", " + clickedNode.y + ")");
                manager.castLocalVote(clickedNode);
            }

            // Block the normal room entry - clear the nextRoom so transition doesn't happen
            AbstractDungeon.nextRoom = null;
            return SpireReturn.Return();
        }
    }

    /**
     * Alternative approach: Patch the MapRoomNode.update method at the prefix level
     * to track when a node is about to be selected.
     */
    @SpirePatch2(clz = MapRoomNode.class, method = "update")
    public static class TrackNodeSelectionPatch {

        // Track if we're in the process of handling a click
        private static boolean handlingClick = false;

        @SpirePrefixPatch
        public static void Prefix(MapRoomNode __instance) {
            // Only active in multiplayer board game mode with voting
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            RoomVotingManager manager = RoomVotingManager.getInstance();
            if (!manager.isVotingActive() || !manager.shouldBlockRoomEntry()) {
                return;
            }

            // Check if this node is being clicked
            // We check if the hitbox is hovered and mouse was just clicked
            if (__instance.hb.hovered && InputHelper.justClickedLeft && !handlingClick) {
                // Check if this is a valid node to select (connected to current position)
                if (isValidVotingTarget(__instance)) {
                    handlingClick = true;
                    logger.info("Detected click on node (" + __instance.x + ", " + __instance.y + "), casting vote");
                    manager.castLocalVote(__instance);
                    // Consume the click to prevent vanilla handling
                    InputHelper.justClickedLeft = false;
                    handlingClick = false;
                } else if (__instance.room != null) {
                    // Log invalid click for debugging
                    logger.debug("Rejected click on non-connected node (" + __instance.x + ", " + __instance.y + ")");
                }
            }
        }

        /**
         * Validates if a map node is a valid voting target from the current position.
         * Mirrors vanilla game's room selection logic.
         *
         * @param targetNode The node the player wants to vote for
         * @return true if this is a valid next room to enter
         */
        private static boolean isValidVotingTarget(MapRoomNode targetNode) {
            // Must have a room
            if (targetNode.room == null) {
                return false;
            }

            // At start of act (first room not chosen), any row 0 node is valid
            if (!AbstractDungeon.firstRoomChosen) {
                return targetNode.y == 0;
            }

            // Get current map position
            MapRoomNode currentNode = AbstractDungeon.currMapNode;
            if (currentNode == null) {
                // Shouldn't happen if firstRoomChosen is true, but handle it safely
                return false;
            }

            // Target must be exactly one row above current (forward progression)
            if (targetNode.y != currentNode.y + 1) {
                return false;
            }

            // Check if current node has a connection to target node
            // Use vanilla's isConnectedTo method which checks the edges ArrayList
            boolean normalConnection = currentNode.isConnectedTo(targetNode);
            boolean wingedConnection = currentNode.wingedIsConnectedTo(targetNode);

            return normalConnection || wingedConnection;
        }
    }

    /**
     * Patch to render vote markers on map nodes.
     */
    @SpirePatch2(clz = MapRoomNode.class, method = "render")
    public static class RenderVoteMarkersPatch {

        @SpirePostfixPatch
        public static void Postfix(MapRoomNode __instance, SpriteBatch sb) {
            // Only render in multiplayer board game mode
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            // Only render if voting is active
            RoomVotingManager manager = RoomVotingManager.getInstance();
            if (!manager.isVotingActive()) {
                return;
            }

            // Render vote markers for this node
            MapVoteRenderer.renderVotesForNode(sb, __instance);
        }
    }

    /**
     * Patch to activate voting when the dungeon map screen opens.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "open", paramtypez = {boolean.class})
    public static class ActivateVotingOnMapOpenPatch {

        @SpirePostfixPatch
        public static void Postfix(DungeonMapScreen __instance, boolean doScrollingAnimation) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                logger.info("Map screen opened, activating voting");
                RoomVotingManager.getInstance().activate();
            }
        }
    }

    /**
     * Patch to deactivate voting when the dungeon map screen closes.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "close")
    public static class DeactivateVotingOnMapClosePatch {

        @SpirePrefixPatch
        public static void Prefix(DungeonMapScreen __instance) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                logger.info("Map screen closing, deactivating voting");
                RoomVotingManager.getInstance().deactivate();
            }
        }
    }

    /**
     * Patch to update the voting manager each frame when on the map.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "update")
    public static class UpdateVotingManagerPatch {

        @SpirePostfixPatch
        public static void Postfix(DungeonMapScreen __instance) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                RoomVotingManager.getInstance().update(com.badlogic.gdx.Gdx.graphics.getDeltaTime());
            }
        }
    }

    /**
     * Patch to render the voting status overlay on the map screen.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "render")
    public static class RenderVotingStatusPatch {

        @SpirePostfixPatch
        public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                RoomVotingManager manager = RoomVotingManager.getInstance();
                if (manager.isVotingActive()) {
                    MapVoteRenderer.renderVotingStatus(sb, manager);
                }
            }
        }
    }
}
