# Multi-Character Row-Based UI System Plan

## Problem Summary

Currently, in multi-character mode:
1. **All player hands/energy are visible at once** - hands are stacked vertically and users scroll between them, but all are rendered simultaneously (just grayed out when not active)
2. **All UI elements show all characters** - energy orbs stack, relics/potions show for all characters
3. **No visual distinction between rows** - characters are in "rows" but there's no colored background to identify which row is which
4. **Clicking doesn't switch the active character** - only scroll wheel changes which hand is "active"

## Goals

1. **Single-character UI view** - Only show one character's hand, energy, relics, and potions at a time
2. **Row-based colored backgrounds** - Each row has a distinct color (red=Ironclad, green=Silent, blue=Defect, purple=Watcher)
3. **Click to switch** - Clicking on a character in their row switches the UI to that character
4. **Visual clarity** - Clear separation between rows showing which enemies face which player

---

## Architecture Overview

### Current System
```
MultiCharacter (container)
├── subcharacters: ArrayList<AbstractPlayer>
├── HandLayoutHelper (manages hand offset/scrolling)
└── ContextPatches (pushes/pops player context for operations)

During combat:
- All hands rendered with Y offsets
- Energy orbs rendered stacked
- Cards grayed out when not in "currentHand"
- Scroll wheel changes currentHand
```

### Proposed System
```
MultiCharacter (container)
├── subcharacters: ArrayList<AbstractPlayer>
├── activeCharacterIndex: int (replaces HandLayoutHelper.currentHand)
├── CombatRowManager (NEW - manages row display and switching)
│   ├── RowPanel[] rows (4 max)
│   ├── activeRow: int
│   └── handleRowClick()
└── ContextPatches (unchanged)

During combat:
- Only active character's hand rendered
- Only active character's energy/relics/potions in panels
- Row backgrounds with character colors
- Click on row = switch active character
```

---

## Implementation Steps

### Step 1: Create CombatRowManager class

**New file:** `src/main/java/CoopBoardGame/multicharacter/CombatRowManager.java`

Manages combat UI for row-based character display:
- Tracks `activeCharacterIndex` (0 = bottom row)
- Renders colored row backgrounds behind each character/enemy row
- Handles click detection to switch active character
- Provides methods: `getActiveCharacter()`, `setActiveCharacter(int)`, `renderRowBackgrounds(SpriteBatch)`

**Row colors:**
```java
private static final Color IRONCLAD_BG = new Color(0.4f, 0.15f, 0.15f, 0.6f);
private static final Color SILENT_BG = new Color(0.15f, 0.4f, 0.2f, 0.6f);
private static final Color DEFECT_BG = new Color(0.15f, 0.25f, 0.5f, 0.6f);
private static final Color WATCHER_BG = new Color(0.35f, 0.15f, 0.4f, 0.6f);
```

### Step 2: Create RowBackground rendering

**New file:** `src/main/java/CoopBoardGame/multicharacter/grid/RowBackgroundRenderer.java`

Renders horizontal colored bands across the screen for each row:
- Each band spans full screen width
- Height based on GridTile.TILE_HEIGHT (~163px scaled)
- Color determined by character class in that row
- Active row has brighter/more opaque color
- Inactive rows are more translucent

### Step 3: Update HandLayoutHelper

**File:** `src/main/java/CoopBoardGame/multicharacter/patches/HandLayoutHelper.java`

Changes:
- Remove scroll wheel hand switching (lines 48-58)
- Simplify to only render active character's hand (no Y offsets)
- `currentHand` now set exclusively by CombatRowManager
- `shouldCardBeGrayedOut()` returns true for ALL cards not belonging to active character (no longer renders grayed cards at all)

### Step 4: Update MultiCharacter.renderHand()

**File:** `src/main/java/CoopBoardGame/multicharacter/MultiCharacter.java`

Change `renderHand()` (lines 435-448) to only render the active character's hand:
```java
public void renderHand(SpriteBatch sb) {
    int activeRow = combatRowManager.getActiveCharacterIndex();
    if (activeRow >= 0 && activeRow < subcharacters.size()) {
        AbstractPlayer activeChar = subcharacters.get(activeRow);
        ContextPatches.pushPlayerContext(activeChar);
        activeChar.renderHand(sb);
        ContextPatches.popPlayerContext();
    }
}
```

### Step 5: Update OverlayMenuPatches for single-character UI

**File:** `src/main/java/CoopBoardGame/multicharacter/patches/OverlayMenuPatches.java`

Change `renderSubcharacterEnergyInstead()` to only render active character's energy:
```java
public static void renderSubcharacterEnergyInstead(OverlayMenu __instance, SpriteBatch sb) {
    if (AbstractDungeon.player instanceof MultiCharacter) {
        int activeRow = MultiCharacter.combatRowManager.getActiveCharacterIndex();
        if (activeRow >= 0) {
            AbstractPlayer activeChar = MultiCharacter.getSubcharacters().get(activeRow);
            ContextPatches.pushPlayerContext(activeChar);
            __instance.energyPanel.render(sb);
            ContextPatches.popPlayerContext();
        }
    } else {
        __instance.energyPanel.render(sb);
    }
}
```

### Step 6: Create patches for TopPanel (relics/potions)

**New file:** `src/main/java/CoopBoardGame/multicharacter/patches/TopPanelPatches.java`

Patches to make TopPanel show only active character's relics and potions:

1. **Relic rendering patch:** Intercept `TopPanel.renderRelics()` to push active character context
2. **Potion rendering patch:** Intercept `TopPanel.renderPotions()` to push active character context
3. **Update patches:** Similar patches for `updateRelics()` and `updatePotions()`

### Step 7: Add click-to-switch in combat

**File:** `src/main/java/CoopBoardGame/multicharacter/CombatRowManager.java`

In `update()`:
- Create hitboxes for each row (spanning character + enemy area)
- On click within a row hitbox, call `setActiveCharacter(rowIndex)`
- Play UI sound on switch
- Trigger hand re-layout via `HandLayoutHelper.changeHand()`

### Step 8: Update MultiCharacter.update() and render()

**File:** `src/main/java/CoopBoardGame/multicharacter/MultiCharacter.java`

Add calls to CombatRowManager:
```java
public static CombatRowManager combatRowManager = new CombatRowManager();

public void update() {
    combatRowManager.update();
    // ... rest of update
}

public void render(SpriteBatch sb) {
    combatRowManager.renderRowBackgrounds(sb);
    // ... rest of render (character sprites)
}
```

### Step 9: Update GridBackground integration

**File:** `src/main/java/CoopBoardGame/multicharacter/grid/GridBackground.java`

Coordinate with RowBackgroundRenderer:
- If grid view is enabled, use GridTile-based positioning
- If grid view is disabled, use simpler row bands
- Both should respect the same row colors

### Step 10: Add visual feedback for active row

Add visual indicators:
- Active row background slightly brighter
- Small arrow or highlight on the character in the active row
- Energy orb shows which character is active (already handled by context)

---

## Files to Create

| File | Purpose |
|------|---------|
| `CombatRowManager.java` | Central manager for combat row UI and character switching |
| `RowBackgroundRenderer.java` | Renders colored row backgrounds |
| `TopPanelPatches.java` | Patches to show only active character's relics/potions |

## Files to Modify

| File | Changes |
|------|---------|
| `MultiCharacter.java` | Add CombatRowManager instance, update render/update calls |
| `HandLayoutHelper.java` | Remove scroll wheel switching, simplify to single-hand rendering |
| `OverlayMenuPatches.java` | Render only active character's energy |
| `GridBackground.java` | Coordinate with row colors |
| `GridTile.java` | Optional: add row color tinting |

---

## Testing Checklist

- [ ] Only one character's hand visible at a time
- [ ] Only one character's energy orb visible
- [ ] Only active character's relics shown in top panel
- [ ] Only active character's potions shown in top panel
- [ ] Clicking on a row switches to that character
- [ ] Row backgrounds render with correct colors per character class
- [ ] Active row is visually distinct (brighter)
- [ ] Card targeting still works correctly
- [ ] Actions still resolve with correct player context
- [ ] Turn order (bottom-to-top) still works
- [ ] Enemy targeting (enemies target player in their row) still works

---

## Edge Cases to Handle

1. **Single character mode** - Skip row UI entirely, behave as vanilla
2. **Character death** - Remove from available rows, switch if active character dies
3. **Mid-combat character changes** - Refresh row manager state
4. **Screen overlays** - Don't process row clicks when screens are up
5. **Card selection targeting** - Ensure card effects target correct row's enemies

---

## Alternative Approaches Considered

### A. Keep all hands visible but clearer separation
- Pros: Less code change, familiar to existing users
- Cons: Screen clutter, doesn't address the core issue of "only one hand is usable"

### B. Tab-based character switching (top of screen)
- Pros: Clear UI pattern, accessible
- Cons: Disconnects from spatial row representation, extra UI element

### C. Keyboard shortcuts only (1-4 keys)
- Pros: Fast switching for power users
- Cons: Not discoverable, doesn't solve visual clutter

**Chosen approach:** Click-on-row switching with single-character UI view provides the best balance of visual clarity and intuitive interaction.

---

## Implementation Order (Recommended)

1. Create `CombatRowManager` with basic active character tracking
2. Update `HandLayoutHelper` to only render active hand
3. Update `OverlayMenuPatches` for single energy display
4. Create `TopPanelPatches` for relics/potions
5. Add `RowBackgroundRenderer` for colored backgrounds
6. Add click-to-switch functionality
7. Polish visual feedback and edge cases
8. Testing and bug fixes
