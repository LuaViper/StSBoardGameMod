package CoopBoardGame.ui.map;

import CoopBoardGame.multiplayer.voting.RoomVote;
import CoopBoardGame.multiplayer.voting.RoomVotingManager;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.List;

/**
 * Renders vote markers on the map for the voting-based room selection system.
 * Shows which players have voted for each node, vote counts, and voting status.
 */
public class MapVoteRenderer {

    // Colors for each player (up to 4 players)
    private static final Color[] PLAYER_COLORS = {
        new Color(0.8f, 0.2f, 0.2f, 1f),  // Player 0: Red
        new Color(0.2f, 0.6f, 0.2f, 1f),  // Player 1: Green
        new Color(0.2f, 0.4f, 0.8f, 1f),  // Player 2: Blue
        new Color(0.7f, 0.5f, 0.1f, 1f),  // Player 3: Orange/Gold
    };

    // Highlight color for winning node
    private static final Color WINNING_NODE_COLOR = new Color(1f, 0.84f, 0f, 0.7f); // Gold

    // Size of vote marker dots
    private static final float MARKER_SIZE = 24f;
    private static final float MARKER_SPACING = 20f;

    // Status panel dimensions
    private static final float STATUS_PANEL_WIDTH = 300f;
    private static final float STATUS_PANEL_HEIGHT = 100f;
    private static final float STATUS_PANEL_X = 20f;
    private static final float STATUS_PANEL_Y_OFFSET = 200f;

    /**
     * Renders vote markers for a specific map node.
     * Called from MapVotingPatch after each node renders.
     */
    public static void renderVotesForNode(SpriteBatch sb, MapRoomNode node) {
        RoomVotingManager manager = RoomVotingManager.getInstance();

        // Get players who voted for this node
        List<Integer> voters = manager.getPlayersWhoVotedForNode(node.x, node.y);
        if (voters.isEmpty()) {
            return;
        }

        // Calculate position relative to node
        float nodeX = node.hb.cX;
        float nodeY = node.hb.cY;

        // Highlight the node if it's the winning node
        if (manager.isWinningNode(node)) {
            renderWinningHighlight(sb, nodeX, nodeY, node.hb.width, node.hb.height);
        }

        // Render vote markers for each voter
        float startX = nodeX - ((voters.size() - 1) * MARKER_SPACING * Settings.scale) / 2f;
        float markerY = nodeY + node.hb.height / 2f + 15f * Settings.scale;

        for (int i = 0; i < voters.size(); i++) {
            int playerId = voters.get(i);
            Color markerColor = getPlayerColor(playerId);
            float markerX = startX + (i * MARKER_SPACING * Settings.scale);

            renderVoteMarker(sb, markerX, markerY, markerColor, playerId == TogetherInSpireHelper.getLocalPlayerId());
        }

        // Render vote count badge
        int voteCount = voters.size();
        if (voteCount > 0) {
            renderVoteCount(sb, nodeX + node.hb.width / 2f, nodeY + node.hb.height / 2f, voteCount);
        }
    }

    /**
     * Renders a single vote marker (colored dot).
     */
    private static void renderVoteMarker(SpriteBatch sb, float x, float y, Color color, boolean isLocalPlayer) {
        float size = MARKER_SIZE * Settings.scale;

        // Draw outer ring (slightly larger for local player)
        if (isLocalPlayer) {
            sb.setColor(Color.WHITE);
            sb.draw(
                ImageMaster.WHITE_RING,
                x - size / 2f - 2f * Settings.scale,
                y - size / 2f - 2f * Settings.scale,
                size + 4f * Settings.scale,
                size + 4f * Settings.scale
            );
        }

        // Draw the colored circle
        sb.setColor(color);
        sb.draw(
            ImageMaster.TP_HP,  // Use a circular texture
            x - size / 2f,
            y - size / 2f,
            size,
            size
        );
    }

    /**
     * Renders a vote count badge next to a node.
     */
    private static void renderVoteCount(SpriteBatch sb, float x, float y, int count) {
        String text = String.valueOf(count);
        float textX = x + 5f * Settings.scale;
        float textY = y + 5f * Settings.scale;

        // Draw background
        sb.setColor(new Color(0, 0, 0, 0.7f));
        float bgSize = 20f * Settings.scale;
        sb.draw(
            ImageMaster.WHITE_RING,
            textX - bgSize / 2f,
            textY - bgSize / 2f,
            bgSize,
            bgSize
        );

        // Draw count text
        FontHelper.renderFontCentered(
            sb,
            FontHelper.topPanelAmountFont,
            text,
            textX,
            textY,
            Color.WHITE
        );
    }

    /**
     * Renders a highlight around the winning node.
     */
    private static void renderWinningHighlight(SpriteBatch sb, float x, float y, float width, float height) {
        sb.setColor(WINNING_NODE_COLOR);

        float padding = 10f * Settings.scale;
        float highlightWidth = width + padding * 2f;
        float highlightHeight = height + padding * 2f;

        // Draw glowing border
        sb.draw(
            ImageMaster.MAP_CIRCLE_3,
            x - highlightWidth / 2f,
            y - highlightHeight / 2f,
            highlightWidth,
            highlightHeight
        );
    }

    /**
     * Renders the voting status panel showing overall voting progress.
     */
    public static void renderVotingStatus(SpriteBatch sb, RoomVotingManager manager) {
        float panelX = STATUS_PANEL_X * Settings.scale;
        float panelY = Settings.HEIGHT - STATUS_PANEL_Y_OFFSET * Settings.scale;
        float panelWidth = STATUS_PANEL_WIDTH * Settings.scale;
        float panelHeight = STATUS_PANEL_HEIGHT * Settings.scale;

        // Draw panel background
        sb.setColor(new Color(0, 0, 0, 0.75f));
        sb.draw(
            ImageMaster.WHITE_SQUARE_IMG,
            panelX,
            panelY,
            panelWidth,
            panelHeight
        );

        // Draw border
        sb.setColor(Color.GRAY);
        float borderWidth = 2f * Settings.scale;
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, panelX, panelY, panelWidth, borderWidth);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, panelX, panelY + panelHeight - borderWidth, panelWidth, borderWidth);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, panelX, panelY, borderWidth, panelHeight);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, panelX + panelWidth - borderWidth, panelY, borderWidth, panelHeight);

        // Draw title
        float textY = panelY + panelHeight - 25f * Settings.scale;
        FontHelper.renderFontLeftTopAligned(
            sb,
            FontHelper.tipBodyFont,
            "Room Selection",
            panelX + 15f * Settings.scale,
            textY,
            Color.WHITE
        );

        // Draw vote count
        int currentVotes = manager.getCurrentVoteCount();
        int totalPlayers = manager.getExpectedVoteCount();
        String voteText = "Votes: " + currentVotes + "/" + totalPlayers;
        textY -= 25f * Settings.scale;

        Color voteColor = (currentVotes >= totalPlayers) ? Color.GREEN : Color.YELLOW;
        FontHelper.renderFontLeftTopAligned(
            sb,
            FontHelper.tipBodyFont,
            voteText,
            panelX + 15f * Settings.scale,
            textY,
            voteColor
        );

        // Draw player vote indicators
        textY -= 30f * Settings.scale;
        float indicatorX = panelX + 15f * Settings.scale;
        float indicatorSpacing = 35f * Settings.scale;

        for (int i = 0; i < totalPlayers; i++) {
            RoomVote vote = manager.getVote(i);
            Color indicatorColor = getPlayerColor(i);

            // Draw player indicator
            sb.setColor(indicatorColor);
            float indicatorSize = 20f * Settings.scale;
            sb.draw(
                ImageMaster.TP_HP,
                indicatorX + (i * indicatorSpacing) - indicatorSize / 2f,
                textY - indicatorSize / 2f,
                indicatorSize,
                indicatorSize
            );

            // Draw checkmark if player has voted
            if (vote != null) {
                sb.setColor(Color.WHITE);
                FontHelper.renderFontCentered(
                    sb,
                    FontHelper.tipBodyFont,
                    "✓",
                    indicatorX + (i * indicatorSpacing),
                    textY,
                    Color.GREEN
                );
            }
        }

        // Draw status message
        textY -= 25f * Settings.scale;
        String statusMessage;
        Color statusColor;

        if (manager.isResolved()) {
            statusMessage = "Entering room...";
            statusColor = Color.GREEN;
        } else if (currentVotes >= totalPlayers) {
            statusMessage = "Resolving votes...";
            statusColor = Color.CYAN;
        } else {
            statusMessage = "Click a room to vote";
            statusColor = Color.LIGHT_GRAY;
        }

        FontHelper.renderFontLeftTopAligned(
            sb,
            FontHelper.tipBodyFont,
            statusMessage,
            panelX + 15f * Settings.scale,
            textY,
            statusColor
        );
    }

    /**
     * Gets the color for a specific player ID.
     */
    public static Color getPlayerColor(int playerId) {
        if (playerId >= 0 && playerId < PLAYER_COLORS.length) {
            return PLAYER_COLORS[playerId];
        }
        // Default color for unexpected player IDs
        return Color.GRAY;
    }

    /**
     * Gets a color based on the player's character class (if available).
     */
    public static Color getPlayerColorByClass(AbstractPlayer.PlayerClass playerClass) {
        if (playerClass == null) {
            return Color.GRAY;
        }

        switch (playerClass.name()) {
            case "BG_IRONCLAD":
            case "IRONCLAD":
                return new Color(0.8f, 0.2f, 0.2f, 1f);  // Red
            case "BG_SILENT":
            case "THE_SILENT":
                return new Color(0.2f, 0.6f, 0.2f, 1f);  // Green
            case "BG_DEFECT":
            case "DEFECT":
                return new Color(0.2f, 0.4f, 0.8f, 1f);  // Blue
            case "BG_WATCHER":
            case "WATCHER":
                return new Color(0.7f, 0.3f, 0.7f, 1f);  // Purple
            default:
                return Color.GRAY;
        }
    }
}
