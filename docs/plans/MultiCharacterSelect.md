# Multi-Character Select Screen: Row Selection and Drag-and-Drop

## Summary

Update the multi-character select screen to allow users to:
1. Click row boxes to select which row to assign a character to
2. Assign characters to specific rows (not sequentially)
3. Drag-and-drop character portraits to reorder between rows
4. Remove characters by dragging outside the row boxes
5. Start game with bottom-most character as single-player

## Files to Modify

| File | Changes |
|------|---------|
| [MultiCharacterRowBoxes.java](src/main/java/CoopBoardGame/multicharacter/MultiCharacterRowBoxes.java) | Add row hitboxes, selection state, highlight rendering, assignment logic |
| [MultiCharacterSelectButton.java](src/main/java/CoopBoardGame/multicharacter/MultiCharacterSelectButton.java) | Disable when assigned, assign to selected row on click |
| [MultiCharacterSwapButton.java](src/main/java/CoopBoardGame/multicharacter/MultiCharacterSwapButton.java) | Add row field, drag detection |
| [MultiCharacterSelectScreen.java](src/main/java/CoopBoardGame/multicharacter/MultiCharacterSelectScreen.java) | Update proceed logic (require row 0 filled) |

## New Files to Create

| File | Purpose |
|------|---------|
| [CharacterRowAssignment.java](src/main/java/CoopBoardGame/multicharacter/CharacterRowAssignment.java) | Data structure for row-to-character mapping |
| [CharacterDragManager.java](src/main/java/CoopBoardGame/multicharacter/CharacterDragManager.java) | Drag-and-drop state management (follows RelicDragManager pattern) |

---

## Implementation Steps

### Step 1: Create CharacterRowAssignment class

New file: `src/main/java/CoopBoardGame/multicharacter/CharacterRowAssignment.java`

```java
public class CharacterRowAssignment {
    public static final int MAX_ROWS = 4;
    private AbstractPlayer[] rowAssignments = new AbstractPlayer[MAX_ROWS];
    private int selectedRow = 0;

    // Core methods:
    // - getCharacterAtRow(int row)
    // - assignCharacterToRow(AbstractPlayer character, int row)
    // - removeCharacterFromRow(int row)
    // - getRowForCharacter(AbstractPlayer character) - returns -1 if not found
    // - isRowOccupied(int row)
    // - getSelectedRow() / setSelectedRow(int row)
    // - autoAdvanceSelectedRow() - move to next empty row upward
    // - swapRows(int row1, int row2)
    // - getAssignedCharactersBottomToTop() - returns List<AbstractPlayer>
    // - clear()
}
```

### Step 2: Create CharacterDragManager class

New file: `src/main/java/CoopBoardGame/multicharacter/CharacterDragManager.java`

Follow the pattern from [RelicDragManager.java](src/main/java/CoopBoardGame/ui/RelicDragManager.java):

```java
public class CharacterDragManager {
    // Drag state
    public static MultiCharacterSwapButton draggedButton = null;
    public static int originalRow = -1;
    public static int hoverRow = -1;
    public static float dragOffsetX, dragOffsetY;

    // Click detection (to distinguish click from drag)
    public static boolean clickStarted = false;
    public static float clickStartX, clickStartY;
    public static MultiCharacterSwapButton pendingDragButton = null;
    public static final float DRAG_THRESHOLD = 8f;

    // Post-drag cooldown
    private static boolean justFinishedDrag = false;
    private static float postDragCooldown = 0f;

    // Remove zone threshold (x position beyond which = removal)
    private static final float REMOVE_ZONE_X = 350f * Settings.scale;

    // Methods:
    // - isDragging()
    // - isInRemoveZone()
    // - checkDragStart(button, row)
    // - startDrag(button, row)
    // - updateDrag(rowBoxes)
    // - endDrag(rowBoxes) - handles swap or removal
    // - globalUpdate() - reset click state, countdown cooldown
    // - renderDraggedButton(sb)
}
```

### Step 3: Update MultiCharacterRowBoxes

Add to existing file:

**New fields:**
```java
private CharacterRowAssignment rowAssignment = new CharacterRowAssignment();
private ArrayList<Hitbox> rowHitboxes = new ArrayList<>();
private Color selectedRowGlowColor = new Color(0.2F, 0.8F, 1.0F, 0.0F);
```

**Constructor changes:**
- Initialize 4 hitboxes for row slots (each ~64x80 scaled)

**update() changes:**
- Update row hitbox positions (accounting for panel animation)
- Handle row click to set selected row
- Call `CharacterDragManager.globalUpdate()` and `updateDrag()`
- Handle mouse release to end drag

**render() changes:**
- Render selected row highlight (pulsing glow)
- Skip rendering dragged button in normal pass
- Render dragged button on top via `CharacterDragManager.renderDraggedButton()`
- Show red tint when dragging in remove zone

**New methods:**
```java
public void assignCharacterToSelectedRow(AbstractPlayer character)
public void removeCharacterFromRow(int row)
public void rebuildSwapButtons()  // replaces remakeSwapButtonsAndPositionCharacters
private void syncSubcharactersFromRowAssignment()
private void renderSelectedRowHighlight(SpriteBatch sb)
public CharacterRowAssignment getRowAssignment()
public ArrayList<Hitbox> getRowHitboxes()
```

### Step 4: Update MultiCharacterSelectButton

**New field:**
```java
public boolean assigned = false;
```

**updateHitbox() changes:**
- Check if character is assigned via `rowAssignment.getRowForCharacter()`
- If assigned, block interaction (no hover sound, no click handling)
- On click: create new character instance, call `rowBoxes.assignCharacterToSelectedRow()`

**renderOptionButton() changes:**
- If `assigned`: render grayed out (Color.DARK_GRAY, alpha 0.5)
- Remove existing `selected` toggle logic (no longer needed)

### Step 5: Update MultiCharacterSwapButton

**New fields:**
```java
public int row = -1;
private Hitbox removeButtonHb;  // Small X button hitbox
private static final float REMOVE_BTN_SIZE = 24f * Settings.scale;
```

**Make buttonImg accessible** (change from private to package-private or add getter)

**Constructor changes:**
- Initialize `removeButtonHb` (positioned at top-right corner of portrait)

**updateHitbox() changes:**
- Update remove button hitbox position relative to portrait
- Check remove button click: if clicked, call `rowBoxes.removeCharacterFromRow(this.row)`
- Call `CharacterDragManager.checkDragStart(this, this.row)` when portrait hovered and mouse pressed

**render() changes:**
- Render small "X" button at top-right corner of portrait
- Highlight X button on hover

### Step 6: Update MultiCharacterSelectScreen

**update() changes:**
```java
// Require at least row 0 (bottom) to be occupied
boolean canProceed = rowBoxes.getRowAssignment().isRowOccupied(0);
if (!canProceed) {
    AbstractDungeon.overlayMenu.proceedButton.hide();
} else {
    AbstractDungeon.overlayMenu.proceedButton.show();
}
```

**render() changes:**
- Update description text: "Select a row, then choose a character."

---

## Visual Behavior

### Row Selection
- Bottom row (0) selected by default on screen open
- Selected row has blue pulsing glow
- Click any row to select it
- After assigning character, auto-advance to next empty row upward

### Character Assignment
- Character buttons gray out when that character is already assigned
- Click available character button to assign to selected row
- Character portrait appears in the row box

### Drag and Drop
- Click and drag portrait to start drag (8px threshold)
- Portrait follows cursor with slight transparency
- Hover over different row = preview swap position
- Drag beyond x=350 (right of panel) = remove zone (portrait turns red)
- Release to complete action (swap, cancel, or remove)

### Removal (Two Options)
1. **X Button**: Click the small X button at top-right of portrait
2. **Drag to Remove**: Drag portrait to remove zone (right side) and release
- Both play burn/remove sound
- Character button becomes available again

---

## Edge Cases

1. **All 4 rows filled**: No auto-advance possible, selected row stays on last assigned
2. **Only row 0 filled**: Can proceed (minimum requirement met)
3. **Non-contiguous rows** (e.g., 0 and 2 filled): Allowed, game starts with row 0 character
4. **Remove last character**: Proceed button hides, must assign at least one to row 0
5. **Drag to same row**: No-op, play cancel sound

---

## Testing Checklist

- [ ] Click row boxes to change selection
- [ ] Selected row shows glow highlight
- [ ] Click character assigns to selected row
- [ ] Assigned characters gray out in button list
- [ ] Auto-advance works after assignment
- [ ] Drag portrait between rows swaps them
- [ ] Click X button on portrait removes character
- [ ] Drag portrait to remove zone removes character
- [ ] Removed character button becomes available
- [ ] Proceed button only shows when row 0 is filled
- [ ] Game starts correctly with row 0 character
