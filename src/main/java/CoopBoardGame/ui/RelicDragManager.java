package CoopBoardGame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import basemod.ReflectionHacks;

import java.util.ArrayList;

/**
 * Singleton manager for relic drag-and-drop functionality in the top panel.
 * Handles all drag state, animations, and reordering logic.
 */
public class RelicDragManager {

    // ===== Drag State =====
    public static AbstractRelic draggedRelic = null;
    public static int originalIndex = -1;
    public static int hoverIndex = -1;
    public static float dragOffsetX = 0;
    public static float dragOffsetY = 0;

    // ===== Click Detection (to distinguish click from drag) =====
    public static boolean clickStarted = false;
    public static float clickStartX = 0;
    public static float clickStartY = 0;
    public static AbstractRelic pendingDragRelic = null;
    public static int pendingDragIndex = -1;
    public static final float DRAG_THRESHOLD = 8f; // Pixels before drag starts

    // ===== Animation State =====
    public static float[] relicTargetOffsets;  // Target X offset for each relic
    public static float[] relicCurrentOffsets; // Current (animated) X offset

    // ===== Arrow Hover State (for scrolling while dragging) =====
    public static boolean hoveringLeftArrow = false;
    public static boolean hoveringRightArrow = false;
    public static float arrowHoverTime = 0f;
    public static final float ARROW_SCROLL_DELAY = 0.3f; // Seconds before scroll triggers

    // ===== Public API =====

    /**
     * Returns true if currently dragging a relic.
     */
    public static boolean isDragging() {
        return draggedRelic != null;
    }

    /**
     * Checks if dragging relics is allowed in the current game state.
     */
    public static boolean canDragRelics() {
        if (AbstractDungeon.player == null) return false;
        if (AbstractDungeon.isScreenUp) return false;
        if (AbstractDungeon.player.isDraggingCard) return false;
        if (AbstractDungeon.player.inSingleTargetMode) return false;
        if (isDragging()) return true; // Allow continuing existing drag

        return true;
    }

    /**
     * Called from AbstractRelic.update() to check if a drag should start.
     */
    public static void checkDragStart(AbstractRelic relic, int index) {
        if (relic.hb.hovered && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!clickStarted && !isDragging()) {
                // Record click start position
                clickStarted = true;
                clickStartX = InputHelper.mX;
                clickStartY = InputHelper.mY;
                pendingDragRelic = relic;
                pendingDragIndex = index;
            } else if (pendingDragRelic == relic && !isDragging()) {
                // Check if mouse has moved enough to start drag
                float dx = InputHelper.mX - clickStartX;
                float dy = InputHelper.mY - clickStartY;
                if (Math.sqrt(dx*dx + dy*dy) > DRAG_THRESHOLD) {
                    startDrag(relic, index);
                }
            }
        }
    }

    /**
     * Global update to reset click state when mouse released.
     */
    public static void globalUpdate() {
        // Reset click state when mouse released
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            clickStarted = false;
            pendingDragRelic = null;
        }
    }

    /**
     * Begins dragging a relic.
     */
    public static void startDrag(AbstractRelic relic, int index) {
        draggedRelic = relic;
        originalIndex = index;
        hoverIndex = index;

        // Calculate offset from relic center to mouse position
        dragOffsetX = relic.currentX - InputHelper.mX;
        dragOffsetY = relic.currentY - InputHelper.mY;

        // Initialize animation offsets arrays
        int size = AbstractDungeon.player.relics.size();
        relicTargetOffsets = new float[size];
        relicCurrentOffsets = new float[size];

        clickStarted = false;
        pendingDragRelic = null;

        CardCrawlGame.sound.play("UI_CLICK_1");
    }

    /**
     * Updates drag state during drag operation.
     */
    public static void updateDrag() {
        // Calculate which slot the mouse is hovering over
        int newHoverIndex = calculateHoverIndex(InputHelper.mX);

        if (newHoverIndex != hoverIndex) {
            hoverIndex = newHoverIndex;
            updateTargetOffsets();
        }

        // Animate offsets smoothly
        animateRelicGaps(Gdx.graphics.getDeltaTime());
    }

    /**
     * Calculates which index position the mouse is hovering over.
     */
    public static int calculateHoverIndex(float mouseX) {
        ArrayList<AbstractRelic> relics = AbstractDungeon.player.relics;

        // Find first relic to determine base X position
        if (relics.isEmpty()) return 0;

        // Calculate index based on mouse position relative to relic positions
        for (int i = 0; i < relics.size(); i++) {
            if (i == originalIndex) continue;

            AbstractRelic r = relics.get(i);
            // If mouse is to the left of this relic's center
            if (mouseX < r.currentX) {
                return i;
            }
        }

        return relics.size(); // After all relics
    }

    /**
     * Updates target offsets for relic gap animation.
     */
    private static void updateTargetOffsets() {
        float relicSpacing = 72.0f * Settings.scale;

        for (int i = 0; i < relicTargetOffsets.length; i++) {
            if (i == originalIndex) {
                relicTargetOffsets[i] = 0; // Dragged relic slot collapses
            } else if (originalIndex < hoverIndex) {
                // Dragging right: relics between original and hover shift left
                if (i > originalIndex && i < hoverIndex) {
                    relicTargetOffsets[i] = -relicSpacing;
                } else if (i >= hoverIndex) {
                    relicTargetOffsets[i] = 0; // Make room at hover position
                } else {
                    relicTargetOffsets[i] = 0;
                }
            } else if (originalIndex > hoverIndex) {
                // Dragging left: relics between hover and original shift right
                if (i >= hoverIndex && i < originalIndex) {
                    relicTargetOffsets[i] = relicSpacing;
                } else {
                    relicTargetOffsets[i] = 0;
                }
            } else {
                relicTargetOffsets[i] = 0;
            }
        }
    }

    /**
     * Smoothly animates relic gap offsets.
     */
    public static void animateRelicGaps(float deltaTime) {
        for (int i = 0; i < relicCurrentOffsets.length; i++) {
            float target = relicTargetOffsets[i];
            float current = relicCurrentOffsets[i];
            // Manual lerp: current + (target - current) * speed * deltaTime
            float lerpSpeed = 12.0f;
            relicCurrentOffsets[i] = current + (target - current) * lerpSpeed * deltaTime;
        }
    }

    /**
     * Gets the current animated offset for a relic at the given index.
     */
    public static float getRelicOffset(int index) {
        if (relicCurrentOffsets == null || index < 0 || index >= relicCurrentOffsets.length) {
            return 0;
        }
        return relicCurrentOffsets[index];
    }

    /**
     * Ends the drag operation and commits the reorder if applicable.
     */
    public static void endDrag() {
        if (hoverIndex != originalIndex && hoverIndex >= 0 &&
            hoverIndex <= AbstractDungeon.player.relics.size()) {

            // Reorder the relic list
            AbstractRelic relic = AbstractDungeon.player.relics.remove(originalIndex);

            // Calculate insertion index
            int insertIndex = hoverIndex;
            if (hoverIndex > originalIndex) {
                insertIndex--; // Adjust because we removed an element
            }

            // Clamp to valid range
            insertIndex = Math.max(0, Math.min(insertIndex, AbstractDungeon.player.relics.size()));

            AbstractDungeon.player.relics.add(insertIndex, relic);

            // Reorganize relic positions
            AbstractDungeon.player.reorganizeRelics();

            CardCrawlGame.sound.play("RELIC_DROP_CLINK");
        } else {
            CardCrawlGame.sound.play("UI_CLICK_2");
        }

        // Reset all state
        draggedRelic = null;
        originalIndex = -1;
        hoverIndex = -1;
        relicTargetOffsets = null;
        relicCurrentOffsets = null;
        hoveringLeftArrow = false;
        hoveringRightArrow = false;
        arrowHoverTime = 0f;
    }

    /**
     * Renders the dragged relic at the cursor position.
     */
    public static void renderDraggedRelic(SpriteBatch sb) {
        if (draggedRelic == null) return;

        float x = InputHelper.mX + dragOffsetX;
        float y = InputHelper.mY + dragOffsetY;

        // Slight transparency to indicate dragging
        sb.setColor(1f, 1f, 1f, 0.85f);
        sb.draw(draggedRelic.img,
                x - 64f, y - 64f,
                64f, 64f,
                128f, 128f,
                Settings.scale, Settings.scale,
                0f, 0, 0, 128, 128, false, false);
        sb.setColor(Color.WHITE);
    }

    /**
     * Checks if the dragged relic is hovering over scroll arrows.
     */
    public static void checkArrowHover(TopPanel topPanel) {
        // Access arrow hitboxes via reflection
        Hitbox leftArrowHb = null;
        Hitbox rightArrowHb = null;

        try {
            leftArrowHb = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicArrowHbL");
            rightArrowHb = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicArrowHbR");
        } catch (Exception e) {
            // Arrows don't exist (not enough relics for pagination)
            hoveringLeftArrow = false;
            hoveringRightArrow = false;
            arrowHoverTime = 0f;
            return;
        }

        if (leftArrowHb == null || rightArrowHb == null) {
            hoveringLeftArrow = false;
            hoveringRightArrow = false;
            arrowHoverTime = 0f;
            return;
        }

        boolean wasHoveringLeft = hoveringLeftArrow;
        boolean wasHoveringRight = hoveringRightArrow;

        // Check if dragged relic is near arrow
        float dragX = InputHelper.mX + dragOffsetX;
        float dragY = InputHelper.mY + dragOffsetY;

        hoveringLeftArrow = leftArrowHb.hovered ||
            (dragX < leftArrowHb.x + leftArrowHb.width + 50 && dragY > Settings.HEIGHT - 100);
        hoveringRightArrow = rightArrowHb.hovered ||
            (dragX > rightArrowHb.x - 50 && dragY > Settings.HEIGHT - 100);

        // Reset timer if hover state changed
        if (hoveringLeftArrow != wasHoveringLeft || hoveringRightArrow != wasHoveringRight) {
            arrowHoverTime = 0f;
        }

        // Accumulate hover time
        if (hoveringLeftArrow || hoveringRightArrow) {
            arrowHoverTime += Gdx.graphics.getDeltaTime();

            // Trigger scroll after delay
            if (arrowHoverTime >= ARROW_SCROLL_DELAY) {
                if (hoveringLeftArrow) {
                    scrollRelicsLeft(topPanel);
                } else {
                    scrollRelicsRight(topPanel);
                }
                arrowHoverTime = ARROW_SCROLL_DELAY * 0.5f; // Faster repeat scrolling
            }
        }
    }

    /**
     * Scrolls the relic bar left one page.
     */
    private static void scrollRelicsLeft(TopPanel topPanel) {
        Integer currentPage = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicPage");
        if (currentPage != null && currentPage > 0) {
            ReflectionHacks.setPrivate(topPanel, TopPanel.class, "relicPage", currentPage - 1);
            CardCrawlGame.sound.play("UI_CLICK_1");
        }
    }

    /**
     * Scrolls the relic bar right one page.
     */
    private static void scrollRelicsRight(TopPanel topPanel) {
        Integer currentPage = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicPage");
        Integer maxPage = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "maxRelicPage");
        if (currentPage != null && maxPage != null && currentPage < maxPage) {
            ReflectionHacks.setPrivate(topPanel, TopPanel.class, "relicPage", currentPage + 1);
            CardCrawlGame.sound.play("UI_CLICK_1");
        }
    }
}
