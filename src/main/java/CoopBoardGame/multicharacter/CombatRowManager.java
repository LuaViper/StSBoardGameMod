package CoopBoardGame.multicharacter;

import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.characters.BGWatcher;
import CoopBoardGame.multicharacter.patches.ContextPatches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

/**
 * Manages combat UI for row-based character display.
 * Tracks the active character, renders colored row backgrounds, and handles click-to-switch.
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


    // Active character tracking
    private int activeCharacterIndex = 0;

    // Row hitboxes for click detection
    private ArrayList<Hitbox> rowHitboxes = new ArrayList<>();

    // Flag to track if we're in combat
    private boolean inCombat = false;

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
        // Initialize 4 row hitboxes (max rows)
        // Initial size doesn't matter - they get updated in updateRowHitboxPositions()
        for (int i = 0; i < CharacterRowAssignment.MAX_ROWS; i++) {
            Hitbox hb = new Hitbox(Settings.WIDTH, getRowHeight(4) * Settings.scale);
            rowHitboxes.add(hb);
        }
    }

    /**
     * Resets the manager state at the start of combat.
     */
    public void resetForCombat() {
        activeCharacterIndex = 0;
        inCombat = true;
        updateRowHitboxPositions();
    }

    /**
     * Cleans up state when combat ends.
     */
    public void onCombatEnd() {
        inCombat = false;
    }

    /**
     * Gets the currently active character index (0 = bottom row).
     */
    public int getActiveCharacterIndex() {
        return activeCharacterIndex;
    }

    /**
     * Sets the active character by index.
     * @param index Row index (0 = bottom row)
     */
    public void setActiveCharacter(int index) {
        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (index >= 0 && index < subchars.size()) {
            // Release any held card from current character first
            if (activeCharacterIndex >= 0 && activeCharacterIndex < subchars.size()) {
                subchars.get(activeCharacterIndex).releaseCard();
            }

            activeCharacterIndex = index;

            // Update HandLayoutHelper to sync
            MultiCharacter.handLayoutHelper.changeHand(index);

            // Apply powers for the newly active character's hand
            AbstractPlayer newActive = subchars.get(activeCharacterIndex);
            ContextPatches.pushPlayerContext(newActive);
            for (com.megacrit.cardcrawl.cards.AbstractCard c : newActive.hand.group) {
                c.applyPowers();
            }
            ContextPatches.popPlayerContext();
        }
    }

    /**
     * Gets the currently active character, or null if none.
     */
    public AbstractPlayer getActiveCharacter() {
        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (activeCharacterIndex >= 0 && activeCharacterIndex < subchars.size()) {
            return subchars.get(activeCharacterIndex);
        }
        return null;
    }

    /**
     * Updates row hitbox positions based on current grid settings.
     */
    private void updateRowHitboxPositions() {
        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        int numRows = subchars.size();
        if (numRows <= 1) numRows = 1;

        float rowHeight = getRowHeight(numRows);

        for (int i = 0; i < rowHitboxes.size(); i++) {
            Hitbox hb = rowHitboxes.get(i);
            hb.width = Settings.WIDTH;
            hb.height = rowHeight * Settings.scale;
            hb.x = 0;
            hb.y = getRowBottomY(i, numRows) * Settings.scale;
        }
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

        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (subchars.size() <= 1) return; // No need for row switching with single character

        updateRowHitboxPositions();

        // Check for clicks on player character hitboxes (primary method)
        if (InputHelper.justClickedLeft && !shouldBlockRowSwitch()) {
            for (int i = 0; i < subchars.size(); i++) {
                AbstractPlayer player = subchars.get(i);
                // Check if the player's hitbox was clicked
                if (player.hb != null && player.hb.hovered) {
                    if (i != activeCharacterIndex) {
                        setActiveCharacter(i);
                        CardCrawlGame.sound.playA("UI_CLICK_1", -0.4f);
                        InputHelper.justClickedLeft = false; // Consume the click
                    }
                    break;
                }
            }
        }

        // Update player hitboxes
        for (AbstractPlayer player : subchars) {
            if (player.hb != null) {
                player.hb.update();
            }
        }

        // Also allow number keys 1-4 to switch characters
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1) && subchars.size() > 0) {
            setActiveCharacter(0);
        } else if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2) && subchars.size() > 1) {
            setActiveCharacter(1);
        } else if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3) && subchars.size() > 2) {
            setActiveCharacter(2);
        } else if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_4) && subchars.size() > 3) {
            setActiveCharacter(3);
        }
    }

    /**
     * Renders the colored row backgrounds behind characters and enemies.
     */
    public void renderRowBackgrounds(SpriteBatch sb) {
        if (!inCombat) return;

        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (subchars.size() <= 1) return;

        int numRows = subchars.size();
        float rowHeight = getRowHeight(numRows);

        for (int i = 0; i < subchars.size(); i++) {
            AbstractPlayer character = subchars.get(i);
            Color rowColor = getColorForCharacter(character);

            // Adjust brightness/alpha for active vs inactive rows
            Color renderColor = rowColor.cpy();
            Color borderColor = rowColor.cpy();
            if (i == activeCharacterIndex) {
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

    /**
     * Checks if row switching should be blocked (e.g., during card dragging).
     */
    public boolean shouldBlockRowSwitch() {
        if (AbstractDungeon.player == null) return true;
        if (AbstractDungeon.player.isDraggingCard) return true;
        if (AbstractDungeon.actionManager.turnHasEnded) return true;
        return false;
    }

    /**
     * Called when a character dies during combat.
     * Switches to another character if the active one died.
     */
    public void onCharacterDeath(AbstractPlayer deadCharacter) {
        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (subchars.isEmpty()) return;

        // Find the dead character's index
        int deadIndex = -1;
        for (int i = 0; i < subchars.size(); i++) {
            if (subchars.get(i) == deadCharacter) {
                deadIndex = i;
                break;
            }
        }

        // If active character died, switch to another
        if (deadIndex == activeCharacterIndex) {
            // Try to find an alive character
            for (int i = 0; i < subchars.size(); i++) {
                if (i != deadIndex && !subchars.get(i).isDead) {
                    setActiveCharacter(i);
                    return;
                }
            }
        }
    }
}
