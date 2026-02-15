package CoopBoardGame.multiplayer.rows;

import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.characters.BGWatcher;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.List;

/**
 * Manages combat UI for row-based character display in multiplayer mode.
 * Tracks row rendering and handles colored row backgrounds for each player.
 */
public class CombatRowManager {

    // Row background colors for each character class
    public static final Color IRONCLAD_BG = new Color(0.4f, 0.15f, 0.15f, 0.5f);
    public static final Color SILENT_BG = new Color(0.15f, 0.4f, 0.2f, 0.5f);
    public static final Color DEFECT_BG = new Color(0.15f, 0.25f, 0.5f, 0.5f);
    public static final Color WATCHER_BG = new Color(0.35f, 0.15f, 0.4f, 0.5f);
    public static final Color DEFAULT_BG = new Color(0.3f, 0.3f, 0.3f, 0.5f);

    // Active row brightness multiplier
    private static final float ACTIVE_ROW_BRIGHTNESS = 1.3f;
    private static final float INACTIVE_ROW_ALPHA = 0.35f;
    private static final float ACTIVE_ROW_ALPHA = 0.6f;

    // Dynamic row layout constants
    public static final float BOTTOM_MARGIN = 150f;  // Space for UI at bottom (hand, energy, etc.)
    public static final float TOP_MARGIN = 100f;     // Space for UI at top

    // Positioning constants
    public static final float PLAYER_X_FRACTION = 0.20f;  // Players at 20% from left
    public static final float ENEMY_START_X_FRACTION = 0.55f;  // First enemy at 55% from left
    public static final float ENEMY_SPACING_FRACTION = 0.15f;  // Space between enemies

    // Row border constants
    public static final float ROW_BORDER_THICKNESS = 4f;  // Border thickness in unscaled pixels
    public static final float ROW_BORDER_ALPHA = 0.8f;    // Border alpha (more visible than fill)
    public static final float ROW_PADDING = 10f;          // Padding inside each row

    // Maximum number of rows supported
    public static final int MAX_ROWS = 4;

    // Flag to track if we're in combat
    private boolean inCombat = false;

    // Cached player count for multiplayer mode
    private int multiplayerRowCount = 0;

    // Colors for multiplayer rows (when we don't have direct character references)
    private static final Color[] MULTIPLAYER_ROW_COLORS = {
        IRONCLAD_BG, SILENT_BG, DEFECT_BG, WATCHER_BG
    };

    /**
     * Calculates the dynamic row height based on number of active players.
     * @param numRows Number of player rows (1-4)
     * @return Height of each row in unscaled pixels
     */
    public static float getRowHeight(int numRows) {
        if (numRows <= 1) {
            // Single player: use full available height (not really "rows")
            return (Settings.HEIGHT / Settings.scale) - BOTTOM_MARGIN - TOP_MARGIN;
        }
        float availableHeight = (Settings.HEIGHT / Settings.scale) - BOTTOM_MARGIN - TOP_MARGIN;
        return availableHeight / numRows;
    }

    /**
     * Gets the Y position of the bottom of a specific row.
     * @param rowIndex Row index (0 = bottom row)
     * @param numRows Total number of rows
     * @return Y position in unscaled pixels
     */
    public static float getRowBottomY(int rowIndex, int numRows) {
        float rowHeight = getRowHeight(numRows);
        return BOTTOM_MARGIN + (rowIndex * rowHeight);
    }

    /**
     * Gets the Y position of the center of a specific row.
     * @param rowIndex Row index (0 = bottom row)
     * @param numRows Total number of rows
     * @return Y position in unscaled pixels
     */
    public static float getRowCenterY(int rowIndex, int numRows) {
        float rowHeight = getRowHeight(numRows);
        return BOTTOM_MARGIN + (rowIndex * rowHeight) + (rowHeight / 2f);
    }

    /**
     * Gets the scale factor for characters based on number of rows.
     * @param numRows Number of player rows (1-4)
     * @return Scale factor to apply to characters
     */
    public static float getScaleForRows(int numRows) {
        switch (numRows) {
            case 1: return 1.0f;
            case 2: return 0.65f;
            case 3: return 0.50f;
            case 4: return 0.40f;
            default: return 0.40f;
        }
    }

    public CombatRowManager() {
    }

    /**
     * Resets the manager state at the start of combat.
     */
    public void resetForCombat() {
        inCombat = true;

        // Cache the multiplayer row count at combat start
        if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
            multiplayerRowCount = TogetherInSpireHelper.getBoardGamePlayerCount();
        } else {
            multiplayerRowCount = 0;
        }
    }

    /**
     * Gets the number of active player rows.
     * This supports TogetherInSpire multiplayer mode.
     *
     * @return number of player rows (minimum 1)
     */
    public int getActiveRowCount() {
        // Check for TogetherInSpire multiplayer mode
        if (multiplayerRowCount > 1) {
            return multiplayerRowCount;
        }

        // Fallback: check TogetherInSpire directly
        if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
            return TogetherInSpireHelper.getBoardGamePlayerCount();
        }

        return 1;
    }

    /**
     * Checks if we're in a multi-row combat mode (multiplayer).
     *
     * @return true if multiple rows should be rendered
     */
    public boolean isMultiRowMode() {
        return getActiveRowCount() > 1;
    }

    /**
     * Cleans up state when combat ends.
     */
    public void onCombatEnd() {
        inCombat = false;
    }

    /**
     * Updates the combat row manager each frame.
     */
    public void update() {
        if (!inCombat) return;
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) return;
        if (AbstractDungeon.isScreenUp) return;
        if (AbstractDungeon.getCurrRoom() == null) return;
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) return;

        int numRows = getActiveRowCount();
        if (numRows <= 1) return; // No need for row management with single player
    }

    /**
     * Renders the colored row backgrounds behind characters and enemies.
     */
    public void renderRowBackgrounds(SpriteBatch sb) {
        if (!inCombat) return;

        int numRows = getActiveRowCount();
        if (numRows <= 1) return;

        float rowHeight = getRowHeight(numRows);

        // Get player classes for multiplayer mode
        List<AbstractPlayer.PlayerClass> playerClasses = null;
        if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
            playerClasses = TogetherInSpireHelper.getAllPlayerClasses();
        }

        // Determine the local player's row for highlighting in multiplayer mode
        int localPlayerRow = -1;
        if (AbstractDungeon.player != null) {
            localPlayerRow = MultiCreature.Field.currentRow.get(AbstractDungeon.player);
        }

        for (int i = 0; i < numRows; i++) {
            Color rowColor;

            if (playerClasses != null && i < playerClasses.size()) {
                // Multiplayer mode: get color from player class
                rowColor = getColorForPlayerClass(playerClasses.get(i));
            } else {
                // Fallback: use default color based on row index
                rowColor = MULTIPLAYER_ROW_COLORS[i % MULTIPLAYER_ROW_COLORS.length];
            }

            // Adjust brightness/alpha for active vs inactive rows
            Color renderColor = rowColor.cpy();
            Color borderColor = rowColor.cpy();

            // In multiplayer mode, highlight local player's row
            boolean isActiveRow = (i == localPlayerRow);

            if (isActiveRow) {
                renderColor.r *= ACTIVE_ROW_BRIGHTNESS;
                renderColor.g *= ACTIVE_ROW_BRIGHTNESS;
                renderColor.b *= ACTIVE_ROW_BRIGHTNESS;
                renderColor.a = ACTIVE_ROW_ALPHA;
                borderColor.r = Math.min(1f, borderColor.r * 1.5f);
                borderColor.g = Math.min(1f, borderColor.g * 1.5f);
                borderColor.b = Math.min(1f, borderColor.b * 1.5f);
                borderColor.a = ROW_BORDER_ALPHA;
            } else {
                renderColor.a = INACTIVE_ROW_ALPHA;
                borderColor.a = ROW_BORDER_ALPHA * 0.6f;
            }

            float rowY = getRowBottomY(i, numRows) * Settings.scale;
            float scaledRowHeight = rowHeight * Settings.scale;
            float scaledBorderThickness = ROW_BORDER_THICKNESS * Settings.scale;

            // Draw row background fill
            sb.setColor(renderColor);
            sb.draw(
                ImageMaster.WHITE_SQUARE_IMG,
                0,
                rowY,
                Settings.WIDTH,
                scaledRowHeight
            );

            // Draw row border (top and bottom lines)
            sb.setColor(borderColor);

            // Bottom border
            sb.draw(
                ImageMaster.WHITE_SQUARE_IMG,
                0,
                rowY,
                Settings.WIDTH,
                scaledBorderThickness
            );

            // Top border
            sb.draw(
                ImageMaster.WHITE_SQUARE_IMG,
                0,
                rowY + scaledRowHeight - scaledBorderThickness,
                Settings.WIDTH,
                scaledBorderThickness
            );
        }

        sb.setColor(Color.WHITE);
    }

    /**
     * Gets the appropriate background color for a player class.
     * Used in multiplayer mode where we have player classes but not character instances.
     */
    public static Color getColorForPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        if (playerClass == null) return DEFAULT_BG;

        String className = playerClass.name();

        // Check for BG character classes
        if (className.equals("BG_IRONCLAD") || className.equals("IRONCLAD")) {
            return IRONCLAD_BG;
        } else if (className.equals("BG_SILENT") || className.equals("THE_SILENT")) {
            return SILENT_BG;
        } else if (className.equals("BG_DEFECT") || className.equals("DEFECT")) {
            return DEFECT_BG;
        } else if (className.equals("BG_WATCHER") || className.equals("WATCHER")) {
            return WATCHER_BG;
        }

        return DEFAULT_BG;
    }

    /**
     * Gets the appropriate background color for a character based on their class.
     */
    public static Color getColorForCharacter(AbstractPlayer character) {
        if (character == null) return DEFAULT_BG;

        // Check character class
        if (character.chosenClass == BGIronclad.Enums.BG_IRONCLAD) {
            return IRONCLAD_BG;
        } else if (character.chosenClass == BGSilent.Enums.BG_SILENT) {
            return SILENT_BG;
        } else if (character.chosenClass == BGDefect.Enums.BG_DEFECT) {
            return DEFECT_BG;
        } else if (character.chosenClass == BGWatcher.Enums.BG_WATCHER) {
            return WATCHER_BG;
        }

        return DEFAULT_BG;
    }
}
