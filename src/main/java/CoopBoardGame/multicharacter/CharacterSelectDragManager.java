package CoopBoardGame.multicharacter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import java.util.ArrayList;

/**
 * Singleton manager for character portrait drag-and-drop functionality.
 * Handles drag state, animations, reordering, and removal logic.
 * Follows the pattern established by RelicDragManager.
 */
public class CharacterSelectDragManager {

    // ===== Drag State =====
    public static MultiCharacterSwapButton draggedButton = null;
    public static int originalRow = -1;
    public static int hoverRow = -1;
    public static float dragOffsetX = 0;
    public static float dragOffsetY = 0;

    // ===== Click Detection (to distinguish click from drag) =====
    public static boolean clickStarted = false;
    public static float clickStartX = 0;
    public static float clickStartY = 0;
    public static MultiCharacterSwapButton pendingDragButton = null;
    public static final float DRAG_THRESHOLD = 8f;

    // ===== Post-Drag State (to prevent accidental clicks after drag) =====
    private static boolean justFinishedDrag = false;
    private static float postDragCooldown = 0f;
    private static final float POST_DRAG_COOLDOWN_TIME = 0.2f;

    // ===== Remove Zone (x position beyond which = removal) =====
    private static final float REMOVE_ZONE_X = 350f * Settings.scale;

    // ===== Public API =====

    /**
     * Returns true if currently dragging a character.
     */
    public static boolean isDragging() {
        return draggedButton != null;
    }

    /**
     * Returns true if the dragged character is in the remove zone.
     */
    public static boolean isInRemoveZone() {
        return isDragging() && InputHelper.mX > REMOVE_ZONE_X;
    }

    /**
     * Returns true if we should block clicks (during or just after dragging).
     */
    public static boolean shouldBlockClicks() {
        return isDragging() || justFinishedDrag;
    }

    /**
     * Called from MultiCharacterSwapButton.update() to check if a drag should start.
     */
    public static void checkDragStart(MultiCharacterSwapButton button, int row) {
        if (button.hb.hovered && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!clickStarted && !isDragging()) {
                // Record click start position
                clickStarted = true;
                clickStartX = InputHelper.mX;
                clickStartY = InputHelper.mY;
                pendingDragButton = button;
            } else if (pendingDragButton == button && !isDragging()) {
                // Check if mouse has moved enough to start drag
                float dx = InputHelper.mX - clickStartX;
                float dy = InputHelper.mY - clickStartY;
                if (Math.sqrt(dx * dx + dy * dy) > DRAG_THRESHOLD) {
                    startDrag(button, row);
                }
            }
        }
    }

    /**
     * Begins dragging a character portrait.
     */
    public static void startDrag(MultiCharacterSwapButton button, int row) {
        draggedButton = button;
        originalRow = row;
        hoverRow = row;

        // Calculate offset from button center to mouse position
        dragOffsetX = button.hb.cX - InputHelper.mX;
        dragOffsetY = button.hb.cY - InputHelper.mY;

        clickStarted = false;
        pendingDragButton = null;

        CardCrawlGame.sound.play("UI_CLICK_1");
    }

    /**
     * Global update to reset click state when mouse released.
     * Should be called once per frame from MultiCharacterRowBoxes.update().
     */
    public static void globalUpdate() {
        // Reset click state when mouse released
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            clickStarted = false;
            pendingDragButton = null;
        }

        // Countdown post-drag cooldown
        if (justFinishedDrag) {
            postDragCooldown -= Gdx.graphics.getDeltaTime();
            if (postDragCooldown <= 0) {
                justFinishedDrag = false;
            }
        }
    }

    /**
     * Updates drag state during drag operation.
     * Calculates which row the mouse is hovering over.
     */
    public static void updateDrag(ArrayList<Hitbox> rowHitboxes) {
        if (!isDragging()) return;

        // Find which row hitbox the mouse is over
        hoverRow = -1;
        for (int i = 0; i < rowHitboxes.size(); i++) {
            Hitbox hb = rowHitboxes.get(i);
            if (
                InputHelper.mX >= hb.x &&
                InputHelper.mX <= hb.x + hb.width &&
                InputHelper.mY >= hb.y &&
                InputHelper.mY <= hb.y + hb.height
            ) {
                hoverRow = i;
                break;
            }
        }

        // If not over any row hitbox, check if in remove zone
        if (hoverRow == -1 && !isInRemoveZone()) {
            hoverRow = originalRow; // Default back to original row
        }
    }

    /**
     * Ends the drag operation and commits the action (swap, cancel, or remove).
     * @param rowBoxes The MultiCharacterRowBoxes to perform actions on
     */
    public static void endDrag(MultiCharacterRowBoxes rowBoxes) {
        if (!isDragging()) return;

        if (isInRemoveZone()) {
            // Remove the character (deferred to avoid ConcurrentModificationException)
            rowBoxes.removeCharacterFromRow(originalRow);
            // Sound is played in performRemoval
        } else if (hoverRow != -1 && hoverRow != originalRow) {
            // Swap rows
            rowBoxes.getRowAssignment().swapRows(originalRow, hoverRow);
            rowBoxes.rebuildSwapButtons();
            CardCrawlGame.sound.play("RELIC_DROP_CLINK");
        } else {
            // Cancel - back to original position
            CardCrawlGame.sound.play("UI_CLICK_2");
        }

        // Set cooldown to prevent accidental clicks
        justFinishedDrag = true;
        postDragCooldown = POST_DRAG_COOLDOWN_TIME;

        // Reset all state
        draggedButton = null;
        originalRow = -1;
        hoverRow = -1;
    }

    /**
     * Renders the dragged button at the cursor position.
     */
    public static void renderDraggedButton(SpriteBatch sb) {
        if (draggedButton == null) return;

        float x = InputHelper.mX + dragOffsetX;
        float y = InputHelper.mY + dragOffsetY;

        // Red tint if in remove zone, otherwise slight transparency
        if (isInRemoveZone()) {
            sb.setColor(1f, 0.3f, 0.3f, 0.85f);
        } else {
            sb.setColor(1f, 1f, 1f, 0.85f);
        }

        float size = MultiCharacterSwapButton.HB_W;
        sb.draw(
            draggedButton.getButtonImg(),
            x - size / 2f,
            y - size / 2f,
            size / 2f,
            size / 2f,
            size,
            size,
            1.0f,
            1.0f,
            0.0f,
            0,
            0,
            64,
            64,
            false,
            false
        );

        sb.setColor(Color.WHITE);
    }

    /**
     * Renders a highlight on the row being hovered over during drag.
     */
    public static void renderHoverHighlight(SpriteBatch sb, ArrayList<Hitbox> rowHitboxes) {
        if (!isDragging() || hoverRow < 0 || hoverRow >= rowHitboxes.size()) return;
        if (hoverRow == originalRow) return; // Don't highlight original position

        Hitbox hb = rowHitboxes.get(hoverRow);

        // Draw a subtle highlight
        sb.setColor(0.2f, 0.8f, 1.0f, 0.3f);
        sb.draw(
            com.megacrit.cardcrawl.helpers.ImageMaster.WHITE_SQUARE_IMG,
            hb.x,
            hb.y,
            hb.width,
            hb.height
        );
        sb.setColor(Color.WHITE);
    }
}
