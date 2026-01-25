package CoopBoardGame.multiplayer.voting;

import java.io.Serializable;

/**
 * Represents a player's vote for a map room node.
 * Used in the voting-based room selection system for multiplayer board game mode.
 */
public class RoomVote implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int playerId;
    private final int nodeX;
    private final int nodeY;
    private final long timestamp;

    public RoomVote(int playerId, int nodeX, int nodeY) {
        this.playerId = playerId;
        this.nodeX = nodeX;
        this.nodeY = nodeY;
        this.timestamp = System.currentTimeMillis();
    }

    public RoomVote(int playerId, int nodeX, int nodeY, long timestamp) {
        this.playerId = playerId;
        this.nodeX = nodeX;
        this.nodeY = nodeY;
        this.timestamp = timestamp;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getNodeX() {
        return nodeX;
    }

    public int getNodeY() {
        return nodeY;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if this vote is for the same node as another vote.
     */
    public boolean isSameNode(RoomVote other) {
        return this.nodeX == other.nodeX && this.nodeY == other.nodeY;
    }

    /**
     * Checks if this vote is for the specified node coordinates.
     */
    public boolean isForNode(int x, int y) {
        return this.nodeX == x && this.nodeY == y;
    }

    /**
     * Creates a string key for the node location, useful for map lookups.
     */
    public String getNodeKey() {
        return nodeX + "," + nodeY;
    }

    /**
     * Static helper to create a node key from coordinates.
     */
    public static String makeNodeKey(int x, int y) {
        return x + "," + y;
    }

    @Override
    public String toString() {
        return "RoomVote{playerId=" + playerId + ", node=(" + nodeX + "," + nodeY + "), time=" + timestamp + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoomVote other = (RoomVote) obj;
        return playerId == other.playerId && nodeX == other.nodeX && nodeY == other.nodeY;
    }

    @Override
    public int hashCode() {
        int result = playerId;
        result = 31 * result + nodeX;
        result = 31 * result + nodeY;
        return result;
    }
}
