package CoopBoardGame.multicharacter;

import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.characters.BGWatcher;
import CoopBoardGame.multicharacter.grid.GridBackground;
import CoopBoardGame.multicharacter.grid.GridTile;
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

    // Row dimensions (based on GridTile)
    private static final float ROW_HEIGHT_BASE = GridTile.TILE_HEIGHT;
    private static final float ROW_Y_OFFSET = 350f; // Matches GridBackground.playerGrid.offsetY

    // Active character tracking
    private int activeCharacterIndex = 0;

    // Row hitboxes for click detection
    private ArrayList<Hitbox> rowHitboxes = new ArrayList<>();

    // Flag to track if we're in combat
    private boolean inCombat = false;

    public CombatRowManager() {
        // Initialize 4 row hitboxes (max rows)
        for (int i = 0; i < CharacterRowAssignment.MAX_ROWS; i++) {
            // Full screen width, GridTile height
            Hitbox hb = new Hitbox(Settings.WIDTH, ROW_HEIGHT_BASE * Settings.scale);
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
        for (int i = 0; i < rowHitboxes.size(); i++) {
            Hitbox hb = rowHitboxes.get(i);
            hb.width = Settings.WIDTH;
            hb.height = ROW_HEIGHT_BASE * Settings.scale;
            hb.x = 0;
            hb.y = (ROW_Y_OFFSET + (i * ROW_HEIGHT_BASE)) * Settings.scale;
        }
    }

    /**
     * Updates the combat row manager each frame.
     */
    public void update() {
        if (!inCombat) return;
        if (!GridBackground.isGridViewActive()) return;
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) return;
        if (AbstractDungeon.isScreenUp) return;
        if (AbstractDungeon.getCurrRoom() == null) return;
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) return;

        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (subchars.size() <= 1) return; // No need for row switching with single character

        updateRowHitboxPositions();

        // Update hitboxes and check for clicks
        for (int i = 0; i < subchars.size(); i++) {
            Hitbox hb = rowHitboxes.get(i);
            hb.update();

            if (hb.hovered && InputHelper.justClickedLeft) {
                if (i != activeCharacterIndex) {
                    setActiveCharacter(i);
                    CardCrawlGame.sound.playA("UI_CLICK_1", -0.4f);
                }
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
        if (!GridBackground.isGridViewActive()) return;

        ArrayList<AbstractPlayer> subchars = MultiCharacter.getSubcharacters();
        if (subchars.size() <= 1) return;

        for (int i = 0; i < subchars.size(); i++) {
            AbstractPlayer character = subchars.get(i);
            Color rowColor = getColorForCharacter(character);

            // Adjust brightness/alpha for active vs inactive rows
            Color renderColor = rowColor.cpy();
            if (i == activeCharacterIndex) {
                renderColor.r *= ACTIVE_ROW_BRIGHTNESS;
                renderColor.g *= ACTIVE_ROW_BRIGHTNESS;
                renderColor.b *= ACTIVE_ROW_BRIGHTNESS;
                renderColor.a = ACTIVE_ROW_ALPHA;
            } else {
                renderColor.a = INACTIVE_ROW_ALPHA;
            }

            sb.setColor(renderColor);

            float rowY = (ROW_Y_OFFSET + (i * ROW_HEIGHT_BASE)) * Settings.scale;
            float rowHeight = ROW_HEIGHT_BASE * Settings.scale;

            // Draw full-width row background
            sb.draw(
                ImageMaster.WHITE_SQUARE_IMG,
                0,
                rowY,
                Settings.WIDTH,
                rowHeight
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
