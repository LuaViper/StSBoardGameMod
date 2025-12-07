# Relic Drag-and-Drop Reordering Implementation Plan

## Overview
Implement drag-and-drop functionality for relics in the top panel relic bar, allowing players to reorder their relics by clicking, holding, and dragging them to new positions.

## Architecture Approach

The base game's `TopPanel` class handles relic rendering and manages the relic bar, including scroll arrows when there are too many relics. We will use SpirePatch to hook into TopPanel's `update()` and `render()` methods to add drag-and-drop functionality, similar to how `EntropicBrewPotionButton` patches into TopPanel.

### Key Technical Decisions

1. **State Management via SpireField**: Add drag state fields to TopPanel using `@SpirePatch(method = SpirePatch.CLASS)` pattern
2. **Intercept Relic Updates**: Patch AbstractRelic's update method to detect drag initiation
3. **Override Rendering**: Patch relic rendering to handle dragged relic following cursor and gap animations
4. **Leverage Existing Hitboxes**: Use AbstractRelic's built-in `hb` (Hitbox) for click detection

---

## Implementation Steps

### Step 1: Create RelicDragManager class

Create `src/main/java/CoopBoardGame/ui/RelicDragManager.java`

This singleton class will manage all drag state and logic:

```java
public class RelicDragManager {
    // Drag state
    public static AbstractRelic draggedRelic = null;
    public static int originalIndex = -1;
    public static int hoverIndex = -1;
    public static float dragOffsetX = 0;
    public static float dragOffsetY = 0;

    // For smooth animations
    public static float[] relicTargetOffsets;  // Target X offset for each relic
    public static float[] relicCurrentOffsets; // Current (animated) X offset

    // Arrow hover state (for scrolling while dragging)
    public static boolean hoveringLeftArrow = false;
    public static boolean hoveringRightArrow = false;
    public static float arrowHoverTime = 0f;
    public static final float ARROW_SCROLL_DELAY = 0.3f; // Seconds before scroll triggers

    public static boolean isDragging() { return draggedRelic != null; }

    public static void startDrag(AbstractRelic relic, int index);
    public static void updateDrag();
    public static void endDrag();
    public static int calculateHoverIndex(float mouseX);
    public static void animateRelicGaps(float deltaTime);
}
```

### Step 2: Patch AbstractRelic update for drag detection

Create `src/main/java/CoopBoardGame/patches/RelicDragPatches.java`

Patch `AbstractRelic.update()` to detect when user starts dragging:

```java
@SpirePatch2(clz = AbstractRelic.class, method = "update")
public static class RelicUpdateDragPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractRelic __instance) {
        // Only handle relics in player's relic list (not shop/reward relics)
        if (AbstractDungeon.player == null ||
            !AbstractDungeon.player.relics.contains(__instance)) {
            return;
        }

        // Check if dragging is allowed in current game state
        if (!RelicDragManager.canDragRelics()) return;

        // Delegate to manager for drag detection
        int index = AbstractDungeon.player.relics.indexOf(__instance);
        RelicDragManager.checkDragStart(__instance, index);
    }
}
```

### Step 3: Patch TopPanel.update() for drag management

Add to `RelicDragPatches.java`:

```java
@SpirePatch2(clz = TopPanel.class, method = "update")
public static class TopPanelDragUpdatePatch {
    @SpirePostfixPatch
    public static void Postfix(TopPanel __instance) {
        if (!RelicDragManager.isDragging()) return;

        // Update drag position and hover index
        RelicDragManager.updateDrag();

        // Check for arrow hover (scrolling while dragging)
        RelicDragManager.checkArrowHover(__instance);

        // Check for drop (mouse released)
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            RelicDragManager.endDrag();
        }
    }
}
```

### Step 4: Patch TopPanel/AbstractRelic render for drag visualization

Add to `RelicDragPatches.java`:

```java
@SpirePatch2(clz = AbstractRelic.class, method = "renderInTopPanel",
             paramtypez = {SpriteBatch.class})
public static class RelicRenderDragPatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractRelic __instance, SpriteBatch sb) {
        if (!RelicDragManager.isDragging()) return SpireReturn.Continue();

        // Skip rendering the dragged relic in its normal position
        if (__instance == RelicDragManager.draggedRelic) {
            return SpireReturn.Return(); // Will render separately at cursor
        }

        return SpireReturn.Continue();
    }
}

@SpirePatch2(clz = TopPanel.class, method = "renderRelics",
             paramtypez = {SpriteBatch.class})
public static class TopPanelRenderRelicsPatch {
    @SpirePostfixPatch
    public static void Postfix(TopPanel __instance, SpriteBatch sb) {
        // Render dragged relic at cursor position (on top of everything)
        if (RelicDragManager.isDragging()) {
            RelicDragManager.renderDraggedRelic(sb);
        }
    }
}
```

### Step 5: Implement RelicDragManager core logic

Complete the `RelicDragManager.java` implementation:

```java
// Drag threshold to distinguish click from drag
public static boolean clickStarted = false;
public static float clickStartX = 0;
public static float clickStartY = 0;
public static AbstractRelic pendingDragRelic = null;
public static int pendingDragIndex = -1;
public static final float DRAG_THRESHOLD = 8f; // Pixels

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

public static void globalUpdate() {
    // Reset click state when mouse released
    if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
        clickStarted = false;
        pendingDragRelic = null;
    }
}

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

public static int calculateHoverIndex(float mouseX) {
    ArrayList<AbstractRelic> relics = AbstractDungeon.player.relics;
    float relicSpacing = 72.0f * Settings.scale;

    // Find first relic to determine base X position
    if (relics.isEmpty()) return 0;

    AbstractRelic firstRelic = relics.get(0);
    float baseX = firstRelic.currentX;

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

public static void animateRelicGaps(float deltaTime) {
    float lerpSpeed = 12.0f; // Adjust for animation speed

    for (int i = 0; i < relicCurrentOffsets.length; i++) {
        float target = relicTargetOffsets[i];
        float current = relicCurrentOffsets[i];
        relicCurrentOffsets[i] = MathHelper.lerp(current, target, lerpSpeed * deltaTime);
    }
}

public static float getRelicOffset(int index) {
    if (relicCurrentOffsets == null || index < 0 || index >= relicCurrentOffsets.length) {
        return 0;
    }
    return relicCurrentOffsets[index];
}

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

public static boolean canDragRelics() {
    if (AbstractDungeon.player == null) return false;
    if (AbstractDungeon.isScreenUp) return false;
    if (AbstractDungeon.player.isDraggingCard) return false;
    if (AbstractDungeon.player.inSingleTargetMode) return false;
    if (isDragging()) return true; // Allow continuing existing drag

    return true;
}
```

### Step 6: Handle relic arrow scrolling while dragging

Add arrow hover detection and scrolling to `RelicDragManager.java`:

```java
public static void checkArrowHover(TopPanel topPanel) {
    // Access arrow hitboxes via reflection
    Hitbox leftArrowHb = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicArrowHbL");
    Hitbox rightArrowHb = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicArrowHbR");

    if (leftArrowHb == null || rightArrowHb == null) return;

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

private static void scrollRelicsLeft(TopPanel topPanel) {
    Integer currentPage = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicPage");
    if (currentPage != null && currentPage > 0) {
        ReflectionHacks.setPrivate(topPanel, TopPanel.class, "relicPage", currentPage - 1);
        CardCrawlGame.sound.play("UI_CLICK_1");
    }
}

private static void scrollRelicsRight(TopPanel topPanel) {
    Integer currentPage = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "relicPage");
    Integer maxPage = ReflectionHacks.getPrivate(topPanel, TopPanel.class, "maxRelicPage");
    if (currentPage != null && maxPage != null && currentPage < maxPage) {
        ReflectionHacks.setPrivate(topPanel, TopPanel.class, "relicPage", currentPage + 1);
        CardCrawlGame.sound.play("UI_CLICK_1");
    }
}
```

### Step 7: Apply position offsets during render

Modify the relic render patch to apply animated offsets:

```java
@SpirePatch2(clz = AbstractRelic.class, method = "renderInTopPanel",
             paramtypez = {SpriteBatch.class})
public static class RelicRenderWithOffsetPatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractRelic __instance, SpriteBatch sb) {
        if (!RelicDragManager.isDragging()) return SpireReturn.Continue();

        // Skip the dragged relic entirely (rendered separately)
        if (__instance == RelicDragManager.draggedRelic) {
            return SpireReturn.Return();
        }

        // Apply offset to other relics
        int index = AbstractDungeon.player.relics.indexOf(__instance);
        if (index >= 0) {
            float offset = RelicDragManager.getRelicOffset(index);
            __instance.currentX += offset;
        }

        return SpireReturn.Continue();
    }

    @SpirePostfixPatch
    public static void Postfix(AbstractRelic __instance, SpriteBatch sb) {
        if (!RelicDragManager.isDragging()) return;
        if (__instance == RelicDragManager.draggedRelic) return;

        // Restore original position
        int index = AbstractDungeon.player.relics.indexOf(__instance);
        if (index >= 0) {
            float offset = RelicDragManager.getRelicOffset(index);
            __instance.currentX -= offset;
        }
    }
}
```

---

## File Summary

### New Files to Create

1. **`src/main/java/CoopBoardGame/ui/RelicDragManager.java`**
   - Singleton managing all drag-and-drop state
   - Core logic for drag detection, animation, and reordering
   - Arrow hover detection for scrolling

2. **`src/main/java/CoopBoardGame/patches/RelicDragPatches.java`**
   - `@SpirePatch` for AbstractRelic.update() - drag detection
   - `@SpirePatch` for AbstractRelic.renderInTopPanel() - offset rendering
   - `@SpirePatch` for TopPanel.update() - drag state management
   - `@SpirePatch` for TopPanel.renderRelics() - render dragged relic on top

### Existing Files Modified

None - all functionality added via patches.

---

## Testing Checklist

1. **Basic drag**: Click and hold on a relic, move mouse, verify relic follows cursor
2. **Reorder left**: Drag relic left past other relics, verify gap opens and relics slide
3. **Reorder right**: Drag relic right past other relics, verify gap opens and relics slide
4. **Cancel drag**: Release without moving to new position, verify returns to original
5. **Arrow scroll**: With many relics, drag toward arrow, verify page scrolls after delay
6. **Click still works**: Quick click on relic still shows tooltip (no accidental drag)
7. **State checks**: Verify drag disabled during card play, targeting, screen overlays
8. **Edge cases**:
   - Drag first relic to last position
   - Drag last relic to first position
   - Single relic (no reordering possible)
   - Exactly full row of relics
   - Multiple pages of relics

---

## Notes

- Relics stored in `AbstractDungeon.player.relics` (ArrayList)
- `AbstractPlayer.reorganizeRelics()` updates relic positions after list changes
- TopPanel manages scroll via `relicPage` and `maxRelicPage` fields
- Standard relic spacing approximately 72 pixels (scaled)
- Relic images are 128x128, rendered at 64x64 effective size

## Dependencies

- `Gdx.input.isButtonPressed(Input.Buttons.LEFT)` - mouse button state
- `InputHelper.mX` / `InputHelper.mY` - current mouse position
- `ReflectionHacks` (from ModTheSpire) - accessing private TopPanel fields
- `Gdx.graphics.getDeltaTime()` - for smooth animations
- `MathHelper.lerp()` - smooth animation interpolation
