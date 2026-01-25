package CoopBoardGame.multiplayer.voting;

import CoopBoardGame.util.TogetherInSpireHelper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Singleton manager for the voting-based room selection system.
 * Handles vote collection, counting, tie-breaking, and coordinating room entry.
 */
public class RoomVotingManager {
    private static final Logger logger = LogManager.getLogger(RoomVotingManager.class.getName());

    private static RoomVotingManager instance;

    // Vote storage: playerId -> their current vote
    private final Map<Integer, RoomVote> playerVotes = new HashMap<>();

    // Whether voting is currently active (on map screen in multiplayer)
    private boolean votingActive = false;

    // The winning node after resolution (null until resolved)
    private MapRoomNode winningNode = null;

    // Track if we're in the process of entering a room (to prevent double-entry)
    private boolean enteringRoom = false;

    // Delay after resolution before entering (for visual feedback)
    private float resolutionDelay = 0f;
    private static final float RESOLUTION_DELAY_TIME = 0.5f;

    private RoomVotingManager() {
        // Private constructor for singleton
    }

    public static RoomVotingManager getInstance() {
        if (instance == null) {
            instance = new RoomVotingManager();
        }
        return instance;
    }

    /**
     * Resets the voting manager to initial state.
     * Called when entering a new map screen or after room entry.
     */
    public void reset() {
        playerVotes.clear();
        winningNode = null;
        enteringRoom = false;
        resolutionDelay = 0f;
        logger.info("RoomVotingManager reset");
    }

    /**
     * Activates the voting system. Call when entering the map screen in multiplayer mode.
     */
    public void activate() {
        if (!TogetherInSpireHelper.isMultiplayerActive()) {
            return;
        }
        reset();
        votingActive = true;
        logger.info("Room voting activated for " + TogetherInSpireHelper.getPlayerCount() + " players");
    }

    /**
     * Deactivates the voting system. Call when leaving the map screen.
     */
    public void deactivate() {
        votingActive = false;
        reset();
        logger.info("Room voting deactivated");
    }

    public boolean isVotingActive() {
        return votingActive && TogetherInSpireHelper.isMultiplayerActive();
    }

    /**
     * Casts a vote for the local player.
     * @param node The map node to vote for
     */
    public void castLocalVote(MapRoomNode node) {
        if (!isVotingActive() || enteringRoom) {
            return;
        }

        int localPlayerId = TogetherInSpireHelper.getLocalPlayerId();
        RoomVote vote = new RoomVote(localPlayerId, node.x, node.y);

        // Store locally
        playerVotes.put(localPlayerId, vote);
        logger.info("Local player " + localPlayerId + " voted for node (" + node.x + ", " + node.y + ")");

        // Broadcast to other players
        VotingNetworkHelper.sendVoteCast(vote);

        // Check if all players have voted
        checkAllVoted();
    }

    /**
     * Receives a vote from a remote player.
     * @param vote The vote received from the network
     */
    public void receiveRemoteVote(RoomVote vote) {
        if (!isVotingActive() || enteringRoom) {
            return;
        }

        playerVotes.put(vote.getPlayerId(), vote);
        logger.info("Received vote from player " + vote.getPlayerId() + " for node (" + vote.getNodeX() + ", " + vote.getNodeY() + ")");

        // Check if all players have voted
        checkAllVoted();
    }

    /**
     * Clears a player's vote (if they want to change it before everyone has voted).
     */
    public void clearVote(int playerId) {
        if (!isVotingActive() || enteringRoom) {
            return;
        }

        playerVotes.remove(playerId);
        logger.info("Cleared vote for player " + playerId);
    }

    /**
     * Gets the current vote for a player.
     * @return The vote, or null if the player hasn't voted
     */
    public RoomVote getVote(int playerId) {
        return playerVotes.get(playerId);
    }

    /**
     * Gets all current votes.
     */
    public Collection<RoomVote> getAllVotes() {
        return new ArrayList<>(playerVotes.values());
    }

    /**
     * Gets the number of votes for a specific node.
     */
    public int getVoteCountForNode(int nodeX, int nodeY) {
        int count = 0;
        for (RoomVote vote : playerVotes.values()) {
            if (vote.isForNode(nodeX, nodeY)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the number of votes for a specific node.
     */
    public int getVoteCountForNode(MapRoomNode node) {
        return getVoteCountForNode(node.x, node.y);
    }

    /**
     * Gets all player IDs who voted for a specific node.
     */
    public List<Integer> getPlayersWhoVotedForNode(int nodeX, int nodeY) {
        List<Integer> players = new ArrayList<>();
        for (RoomVote vote : playerVotes.values()) {
            if (vote.isForNode(nodeX, nodeY)) {
                players.add(vote.getPlayerId());
            }
        }
        return players;
    }

    /**
     * Checks if all players have voted and triggers resolution if so.
     */
    private void checkAllVoted() {
        int playerCount = TogetherInSpireHelper.getPlayerCount();
        int voteCount = playerVotes.size();

        logger.info("Vote check: " + voteCount + "/" + playerCount + " players have voted");

        if (voteCount >= playerCount) {
            // All players have voted - host resolves
            if (TogetherInSpireHelper.isHost()) {
                resolveVoting();
            }
        }
    }

    /**
     * Resolves the voting and determines the winning room.
     * Called by the host when all players have voted.
     */
    private void resolveVoting() {
        logger.info("Host resolving votes...");

        // Count votes per node
        Map<String, Integer> voteCounts = new HashMap<>();
        Map<String, int[]> nodeCoords = new HashMap<>();

        for (RoomVote vote : playerVotes.values()) {
            String key = vote.getNodeKey();
            voteCounts.merge(key, 1, Integer::sum);
            nodeCoords.put(key, new int[]{vote.getNodeX(), vote.getNodeY()});
        }

        // Find the maximum vote count
        int maxVotes = Collections.max(voteCounts.values());

        // Find all nodes with the maximum votes (potential tie)
        List<String> topNodes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() == maxVotes) {
                topNodes.add(entry.getKey());
            }
        }

        // Resolve tie with deterministic random
        String winningKey;
        if (topNodes.size() == 1) {
            winningKey = topNodes.get(0);
            logger.info("Winner: " + winningKey + " with " + maxVotes + " votes (no tie)");
        } else {
            // Use seeded random for deterministic tie-breaking
            // Seed based on game seed + floor number so all clients get same result
            long seed = getSeedForTieBreaker();
            Random tieBreaker = new Random(seed);
            int winnerIndex = tieBreaker.nextInt(topNodes.size());
            winningKey = topNodes.get(winnerIndex);
            logger.info("Winner: " + winningKey + " with " + maxVotes + " votes (tie-breaker among " + topNodes.size() + " nodes)");
        }

        int[] coords = nodeCoords.get(winningKey);

        // Broadcast the result to all players
        VotingNetworkHelper.sendVotingResolved(coords[0], coords[1]);

        // Set local state
        setWinningNode(coords[0], coords[1]);
    }

    /**
     * Gets a deterministic seed for tie-breaking that will be the same on all clients.
     */
    private long getSeedForTieBreaker() {
        long seed = 0;

        // Try to get game seed from AbstractDungeon
        if (AbstractDungeon.player != null) {
            // Use floor number as part of seed
            seed = AbstractDungeon.floorNum;
        }

        // Try to get the actual game seed from TogetherInSpire's P2PClientData
        try {
            Long gameSeed = TogetherInSpireHelper.getGameSeed();
            if (gameSeed != null) {
                seed += gameSeed;
            }
        } catch (Exception e) {
            logger.warn("Could not get game seed for tie-breaker: " + e.getMessage());
        }

        // Add some additional entropy based on vote timestamps
        for (RoomVote vote : playerVotes.values()) {
            seed ^= vote.getTimestamp();
        }

        return seed;
    }

    /**
     * Sets the winning node (called after resolution, on all clients).
     */
    public void setWinningNode(int nodeX, int nodeY) {
        // Find the actual MapRoomNode
        if (AbstractDungeon.map != null) {
            for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                for (MapRoomNode node : row) {
                    if (node.x == nodeX && node.y == nodeY) {
                        winningNode = node;
                        resolutionDelay = RESOLUTION_DELAY_TIME;
                        logger.info("Winning node set: (" + nodeX + ", " + nodeY + ")");
                        return;
                    }
                }
            }
        }
        logger.warn("Could not find winning node at (" + nodeX + ", " + nodeY + ")");
    }

    /**
     * Gets the winning node after resolution.
     */
    public MapRoomNode getWinningNode() {
        return winningNode;
    }

    /**
     * Checks if voting has been resolved and a winner determined.
     */
    public boolean isResolved() {
        return winningNode != null;
    }

    /**
     * Update method called each frame. Handles the delay after resolution.
     */
    public void update(float deltaTime) {
        if (!isVotingActive()) {
            return;
        }

        if (winningNode != null && resolutionDelay > 0) {
            resolutionDelay -= deltaTime;

            if (resolutionDelay <= 0 && !enteringRoom) {
                // Delay complete - host triggers room entry
                if (TogetherInSpireHelper.isHost()) {
                    triggerRoomEntry();
                }
            }
        }
    }

    /**
     * Triggers all players to enter the winning room.
     * Called by the host after the resolution delay.
     */
    private void triggerRoomEntry() {
        if (winningNode == null || enteringRoom) {
            return;
        }

        logger.info("Host triggering room entry for node (" + winningNode.x + ", " + winningNode.y + ")");
        enteringRoom = true;

        // Broadcast room entry command to all players
        VotingNetworkHelper.sendEnterRoom(winningNode.x, winningNode.y);

        // Enter room locally
        enterRoom(winningNode);
    }

    /**
     * Called when receiving the room entry command (on all clients including host).
     */
    public void receiveEnterRoom(int nodeX, int nodeY) {
        if (enteringRoom) {
            return; // Already entering
        }

        enteringRoom = true;

        // Find the node and enter
        if (AbstractDungeon.map != null) {
            for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                for (MapRoomNode node : row) {
                    if (node.x == nodeX && node.y == nodeY) {
                        logger.info("Entering room at (" + nodeX + ", " + nodeY + ")");
                        enterRoom(node);
                        return;
                    }
                }
            }
        }

        logger.warn("Could not find room to enter at (" + nodeX + ", " + nodeY + ")");
    }

    /**
     * Actually enters the room. This calls the vanilla room entry logic.
     */
    private void enterRoom(MapRoomNode node) {
        // Deactivate voting
        votingActive = false;

        // Use vanilla room entry - this will be handled by letting the normal
        // MapRoomNode.update() proceed after we've set up the state
        if (node != null && node.room != null) {
            // Set the next room
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.nextRoomTransitionStart();
        }
    }

    /**
     * Checks if we should block normal room entry (because voting is in progress).
     */
    public boolean shouldBlockRoomEntry() {
        return isVotingActive() && !enteringRoom;
    }

    /**
     * Checks if a specific node is the winning node.
     */
    public boolean isWinningNode(MapRoomNode node) {
        return winningNode != null && winningNode.x == node.x && winningNode.y == node.y;
    }

    /**
     * Gets the total number of players expected to vote.
     */
    public int getExpectedVoteCount() {
        return TogetherInSpireHelper.getPlayerCount();
    }

    /**
     * Gets the current number of votes cast.
     */
    public int getCurrentVoteCount() {
        return playerVotes.size();
    }

    /**
     * Handles a player disconnecting - removes their vote and rechecks.
     */
    public void onPlayerDisconnect(int playerId) {
        if (playerVotes.remove(playerId) != null) {
            logger.info("Removed vote from disconnected player " + playerId);
            checkAllVoted();
        }
    }
}
