# TogetherInSpire Multiplayer Integration Plan

## Executive Summary

This document outlines the comprehensive plan to integrate the Board Game mod with TogetherInSpire for cooperative multiplayer support. The integration will implement row-based combat, synchronized die rolls, simultaneous turns, and full network support for all custom game elements.

**Estimated Scope:**

- ~323 cards requiring network sync
- ~105 custom actions requiring network awareness
- ~101 powers requiring state synchronization
- ~65 monsters requiring row assignment and targeting
- ~96 relics requiring network awareness
- Core systems: Die rolling, row management, turn phases

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Phase 1: Core Infrastructure](#2-phase-1-core-infrastructure)
3. [Phase 2: Row System Implementation](#3-phase-2-row-system-implementation)
4. [Phase 3: Die System Integration](#4-phase-3-die-system-integration)
5. [Phase 4: Simultaneous Turn System](#5-phase-4-simultaneous-turn-system)
6. [Phase 5: Card & Action Networking](#6-phase-5-card--action-networking)
7. [Phase 6: Monster & Enemy Networking](#7-phase-6-monster--enemy-networking)
8. [Phase 7: Relic & Potion Networking](#8-phase-7-relic--potion-networking)
9. [Phase 8: Power & Effect Networking](#9-phase-8-power--effect-networking)
10. [Phase 9: UI & Polish](#10-phase-9-ui--polish)
11. [Phase 10: Testing & Validation](#11-phase-10-testing--validation)
12. [Technical Specifications](#12-technical-specifications)
13. [Risk Assessment](#13-risk-assessment)

---

## 1. Architecture Overview

### Current State

**Board Game Mod (Single Player):**

- Implements board game rules (dice, token caps, energy reset)
- Has TheDie system with modification phase
- Has AbstractBGCard/Monster/Relic/Power base classes
- Has non-functional multicharacter/ code (to be replaced)

**TogetherInSpire (Reference Implementation):**

- Full state replication networking (P2P via Steam, TCP fallback)
- 35+ message types for state synchronization
- 45+ callbacks for handling remote events
- Player state tracked via P2PPlayer objects
- Simultaneous turn mode available

### Target Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Board Game Coop Mod                             │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                    New Network Layer                         │   │
│  │  ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐    │   │
│  │  │  BGNetwork  │  │ BGMessages   │  │  BGCallbacks      │    │   │
│  │  │  Manager    │  │ (40+ types)  │  │  (50+ handlers)   │    │   │
│  │  └──────┬──────┘  └──────────────┘  └───────────────────┘    │   │
│  │         │                                                    │   │
│  │  ┌──────▼────────────────────────────────────────────────┐   │   │
│  │  │              TogetherInSpire API Bridge               │   │   │
│  │  │  (Extends P2PMessageSender, P2PCallbacks, P2PPlayer)  │   │   │
│  │  └──────┬────────────────────────────────────────────────┘   │   │
│  └─────────┼────────────────────────────────────────────────────┘   │
│            │                                                        │
│  ┌─────────▼────────────────────────────────────────────────────┐   │
│  │                  Board Game Systems                          │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────────────────┐  │   │
│  │  │  TheDie    │  │  RowSystem │  │  TurnPhaseManager      │  │   │
│  │  │  (Sync'd)  │  │  (New)     │  │  (Simultaneous)        │  │   │
│  │  └────────────┘  └────────────┘  └────────────────────────┘  │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────────────────┐  │   │
│  │  │  Cards     │  │  Monsters  │  │  Relics/Powers/Potions │  │   │
│  │  │  (323)     │  │  (65)      │  │  (96/101/24)           │  │   │
│  │  └────────────┘  └────────────┘  └────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                   TogetherInSpire (Dependency)               │   │
│  │  ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐    │   │
│  │  │ P2PManager  │  │ P2PNetwork   │  │ Existing Messages │    │   │
│  │  └─────────────┘  └──────────────┘  └───────────────────┘    │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### Key Design Principles

1. **Build ON TOP of TogetherInSpire** - Extend, don't fork
2. **Maintain Single-Player Compatibility** - All features work without TogetherInSpire loaded
3. **Trust-Based Synchronization** - Match TogetherInSpire's cooperative model
4. **Host Authority for Critical Systems** - Die rolls, phase transitions
5. **Minimal Latency Design** - Batch updates, predictive display

---

## 2. Phase 1: Core Infrastructure

### 2.1 Create Network Abstraction Layer

**Goal:** Create a clean API that works whether TogetherInSpire is loaded or not.

**New Package:** `CoopBoardGame/network/`

**Files to Create:**

```
network/
├── BGNetworkManager.java          # Central network coordination
├── BGNetworkHelper.java           # Static utilities for network checks
├── BGP2PPlayerExtension.java      # Extension fields for P2PPlayer
├── messages/
│   ├── BGMessageType.java         # Enum of all BG-specific message types
│   ├── BGNetworkMessage.java      # Wrapper for BG messages
│   └── payloads/
│       ├── DieRollPayload.java
│       ├── RowAssignmentPayload.java
│       ├── TurnPhasePayload.java
│       ├── QueuedActionPayload.java
│       └── ... (more payloads)
├── callbacks/
│   ├── BGNetworkCallbacks.java    # Central callback handler
│   └── IBGNetworkListener.java    # Interface for components to implement
└── sync/
    ├── BGStateSynchronizer.java   # Handles full state sync on join
    └── BGDeltaSync.java           # Handles incremental updates
```

**BGNetworkHelper.java:**

```java
public class BGNetworkHelper {

    public static boolean isMultiplayerActive() {
        // Check if TogetherInSpire is loaded and we're in a multiplayer session
        try {
            Class<?> p2pManager = Class.forName("spireTogether.network.P2P.P2PManager");
            // Check if players list has more than 1 player
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isHost() {
        if (!isMultiplayerActive()) return true; // Single player = always host
        // Check TogetherInSpire host status
    }

    public static int getLocalPlayerId() { ... }
    public static int getPlayerCount() { ... }
    public static List<Integer> getAllPlayerIds() { ... }
}
```

### 2.2 TogetherInSpire API Bridge

**Goal:** Create clean interfaces to TogetherInSpire without hard dependencies.

**Files to Create:**

```
network/bridge/
├── TogetherInSpireBridge.java     # Main bridge class with reflection
├── P2PManagerBridge.java          # Wraps P2PManager access
├── P2PMessageSenderBridge.java    # Wraps message sending
└── P2PCallbacksBridge.java        # Wraps callback registration
```

**Pattern:** Use `requiredModId` in `@SpirePatch` annotations to conditionally apply patches only when TogetherInSpire is loaded. This is cleaner than runtime reflection checks for patch classes.

```java
// Patches that require TogetherInSpire use requiredModId
@SpirePatch(
    clz = SomeTogetherInSpireClass.class,
    method = "someMethod",
    requiredModId = "spireTogether" // Only applies if TogetherInSpire is loaded
)
public class ConditionalNetworkPatch {

    @SpirePostfixPatch
    public static void Postfix() {
        // This code only runs when TogetherInSpire is present
    }
}

// For runtime checks (non-patch code), use a bridge class
public class TogetherInSpireBridge {

    private static boolean initialized = false;
    private static boolean available = false;

    public static void initialize() {
        try {
            Class.forName("spireTogether.network.P2P.P2PManager");
            available = true;
            // Register our custom message types
            // Register our callback handlers
        } catch (ClassNotFoundException e) {
            available = false;
        }
        initialized = true;
    }

    public static boolean isAvailable() {
        if (!initialized) initialize();
        return available;
    }
}
```

**Key Insight:** The `requiredModId` parameter in `@SpirePatch` allows patches to be conditionally applied based on whether a mod is loaded. This means:

- Patches targeting TogetherInSpire classes won't cause errors when TogetherInSpire isn't installed
- No need for try-catch blocks or reflection in patch code
- Cleaner separation between single-player and multiplayer code paths

### 2.3 Configuration System

**Goal:** Allow players to configure multiplayer-specific settings.

**New Config Options:**

- `boardGameMultiplayerEnabled` - Master toggle for MP features
- `simultaneousTurnsEnabled` - Use simultaneous vs sequential turns
- `showOtherPlayersHands` - Visibility setting
- `dieRollConfirmationRequired` - Require all players to confirm die

### 2.4 Delete/Ignore Old Multicharacter Code

**Action:** Mark entire `multicharacter/` directory as deprecated. Do not modify, do not reference. New code should not import from this package.

**Files to Ignore (51 files):**

- `multicharacter/*.java`
- `multicharacter/grid/*.java`
- `multicharacter/patches/*.java`

---

## 3. Phase 2: Row System Implementation

### 3.1 Row Data Model

**New Package:** `CoopBoardGame/rows/`

```
rows/
├── RowManager.java                # Central row management
├── RowAssignment.java             # Data class for row assignments
├── PlayerRow.java                 # Represents a single row
├── RowTargeting.java              # Targeting logic utilities
└── patches/
    ├── MonsterTargetingPatch.java # Patch monster targeting to respect rows
    ├── PlayerPositionPatch.java   # Patch player rendering positions
    └── CombatInitPatch.java       # Patch combat initialization
```

**RowManager.java:**

```java
public class RowManager {
    public static final int MAX_ROWS = 4;

    // Maps player ID -> row number (0-3, bottom to top)
    private static Map<Integer, Integer> playerRowAssignments = new HashMap<>();

    // Maps row number -> list of monsters in that row
    private static Map<Integer, List<AbstractMonster>> rowMonsters = new HashMap<>();

    // Boss is special - considered to be in ALL rows
    private static AbstractMonster currentBoss = null;

    public static void assignPlayerToRow(int playerId, int row) { ... }
    public static int getPlayerRow(int playerId) { ... }
    public static void assignMonsterToRow(AbstractMonster m, int row) { ... }
    public static List<AbstractMonster> getMonstersInRow(int row) { ... }
    public static boolean isBoss(AbstractMonster m) { ... }

    // Targeting helpers
    public static List<AbstractMonster> getTargetableMonsters(int playerId) {
        int row = getPlayerRow(playerId);
        List<AbstractMonster> targets = new ArrayList<>(getMonstersInRow(row));
        if (currentBoss != null && !currentBoss.isDead) {
            targets.add(currentBoss);
        }
        return targets;
    }

    public static AbstractPlayer getTargetedPlayer(AbstractMonster m) {
        if (isBoss(m)) {
            // Boss targets based on its action's target type
            return null; // Handled specially
        }
        int row = getMonsterRow(m);
        int playerId = getPlayerInRow(row);
        return getPlayerById(playerId);
    }
}
```

### 3.2 Row Assignment at Combat Start

**Logic:**

1. When combat starts in multiplayer, check player count
2. Assign each player to a row (persistent between combats unless switched)
3. Distribute encounter enemies: one per player row
4. Summons go in the row of the enemy that summoned them
5. Elites/Bosses go in special position

**Encounter Distribution Algorithm:**

```java
public static void distributeEncounterEnemies(List<AbstractMonster> enemies) {
    int playerCount = BGNetworkHelper.getPlayerCount();

    for (int i = 0; i < enemies.size() && i < playerCount; i++) {
        AbstractMonster m = enemies.get(i);
        int row = i; // Assign to row matching player order
        assignMonsterToRow(m, row);
    }

    // Extra enemies go to lowest row
    for (int i = playerCount; i < enemies.size(); i++) {
        assignMonsterToRow(enemies.get(i), 0);
    }
}
```

### 3.3 Row Rendering System

**Goal:** Visually display rows with distinct backgrounds and positions.

**New Files:**

```
rows/ui/
├── RowRenderer.java               # Renders row backgrounds
├── RowBackground.java             # Background graphics per character class
├── BossBackground.java            # Special boss area rendering
└── RowIndicator.java              # Shows which row is active/selected
```

**Visual Layout (1920x1080 reference):**

**Normal Encounters (enemies spawn to the RIGHT of spawner):**

```
┌─────────────────────────────────────────────────────────────────────────┐
│ Row 3 │ Player 4 │ Enemy │ Enemy │ Summon │                             │
│ (Top) │ Watcher  │       │       │ →      │                             │
├───────┼──────────┼───────┼───────┼────────┼─────────────────────────────┤
│ Row 2 │ Player 3 │ Enemy │ Enemy │        │                             │
│       │ Defect   │       │       │        │                             │
├───────┼──────────┼───────┼───────┼────────┼─────────────────────────────┤
│ Row 1 │ Player 2 │ Enemy │ Enemy │ Summon │                             │
│       │ Silent   │       │       │ →      │                             │
├───────┼──────────┼───────┼───────┼────────┼─────────────────────────────┤
│ Row 0 │ Player 1 │ Enemy │ Enemy │        │                             │
│ (Bot) │ Ironclad │       │       │        │                             │
└─────────────────────────────────────────────────────────────────────────┘
```

**Boss Encounters (boss on far RIGHT, summons spawn to LEFT of boss):**

```
┌─────────────────────────────────────────────────────────────────────────┐
│ Row 3 │ Player 4 │              │ Summon │ ┌─────────────────────────┐ │
│ (Top) │ Watcher  │              │ ←      │ │                         │ │
├───────┼──────────┼──────────────┼────────┤ │                         │ │
│ Row 2 │ Player 3 │              │        │ │        BOSS AREA        │ │
│       │ Defect   │              │        │ │   (Right-aligned,       │ │
├───────┼──────────┼──────────────┼────────┤ │    spans all rows       │ │
│ Row 1 │ Player 2 │              │ Summon │ │    vertically)          │ │
│       │ Silent   │              │ ←      │ │                         │ │
├───────┼──────────┼──────────────┼────────┤ │                         │ │
│ Row 0 │ Player 1 │              │        │ │                         │ │
│ (Bot) │ Ironclad │              │        │ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

**Key Layout Rules:**

- **Normal encounters:** Enemies in each row, summons appear to the RIGHT of spawner
- **Boss encounters:** Boss occupies right side spanning all rows, summons appear to the LEFT of boss
- **Boss area:** Right-aligned, center-right of screen, with special background
- **Boss targeting:** Boss is considered "in all rows" - any player can target it, it can target any player

**Row Colors (matching existing CombatRowManager logic):**

- Ironclad: Red (0.4, 0.15, 0.15, 0.3)
- Silent: Green (0.15, 0.4, 0.2, 0.3)
- Defect: Blue (0.15, 0.25, 0.5, 0.3)
- Watcher: Purple (0.35, 0.15, 0.4, 0.3)

### 3.4 Row Switching Mechanics

**Rules (from board game):**

- Players can switch rows between combats freely
- During combat, only specific abilities allow row switching
- **No automatic prompts** - players access row switching voluntarily via map screen

**Between Combats - Map Screen Access:**

- Players can access a "Row Assignment" screen from the map view
- Accessible via a button on the map UI (not a forced prompt after each combat)
- Shows all players and their current row assignments
- Players can drag/click to reassign themselves to different rows
- Changes sync to all players via network

**During Combat - Ability-Triggered Switching:**

- No manual row switch button in combat
- When a player uses an ability that allows row switching:
    1. The ability triggers the row switch prompt automatically
    2. Player selects their new row
    3. Switch executes as part of the ability resolution
- Examples: Cards/relics that say "switch rows" or "move to another row"

**New UI:**

```
rows/ui/
├── RowAssignmentScreen.java       # Screen accessible from map view
├── RowAssignmentButton.java       # Button on map screen to open assignment
└── AbilityRowSwitchPrompt.java    # Prompt shown when ability allows row switch
```

**Implementation for Ability-Triggered Switching:**

```java
// Called by abilities that allow row switching
public class RowSwitchAction extends AbstractGameAction {

    public void update() {
        // Show row selection UI
        RowManager.showRowSwitchPrompt(playerId, (selectedRow) -> {
            // Player selected a row
            RowManager.assignPlayerToRow(playerId, selectedRow);

            if (BGNetworkHelper.isMultiplayerActive()) {
                BGNetworkManager.sendRowSwitch(playerId, selectedRow);
            }

            this.isDone = true;
        });
    }
}
```

**Network Messages:**

```java
// Player switches row (from map screen or ability)
Send_RowSwitch(int playerId, int newRow)

// Full row state sync (on join or resync)
Send_RowStateSync(Map<Integer, Integer> playerRows, Map<Integer, List<MonsterRef>> monsterRows)
```

### 3.5 Monster Targeting Patches

**Goal:** Override default targeting to respect row boundaries.

**Patch 1: Monster Action Targeting**

```java
@SpirePatch(clz = AbstractMonster.class, method = "takeTurn")
public static class MonsterTargetingPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractMonster __instance) {
        if (BGNetworkHelper.isMultiplayerActive()) {
            // Store original target
            // Override target to be player in monster's row
            AbstractPlayer target = RowManager.getTargetedPlayer(__instance);
            // Set target for this turn
        }
    }
}
```

**Patch 2: AoE Handling**

- AoE attacks (marked with row-wide symbol) hit all targets in row + boss
- Full AoE attacks (marked with ALL symbol) hit all targets regardless of row

### 3.6 Card Targeting Updates

**Goal:** Allow cards to target enemies in any row (per board game rules).

**No patch needed for enemy targeting** - Players can target any enemy.

**Patch needed for player-targeting cards:**

```java
@SpirePatch(clz = AbstractCard.class, method = "???") // Find correct method
public static class PlayerTargetingPatch {
    // Cards that say "target any player" should show player selection UI
    // Instead of defaulting to self
}
```

**New Field on AbstractBGCard:**

```java
public boolean canTargetAnyPlayer = false; // Set true for cards like "give ally block"
```

---

## 4. Phase 3: Die System Integration

### 4.1 Refactor TheDie for Network Support

**Current State:** TheDie uses static fields, single-player focused

**Changes Needed:**

**TheDie.java Modifications:**

```java
public class TheDie {

    public static int initialRoll = 0;
    public static int finalRelicRoll = 0;
    public static int monsterRoll = 0;
    public static boolean forceLockInRoll = false;

    // NEW: Network state
    public static boolean awaitingConfirmation = false;
    public static Set<Integer> playersConfirmed = new HashSet<>();
    public static Map<Integer, DieModification> pendingModifications = new HashMap<>();

    public static void roll() {
        if (BGNetworkHelper.isMultiplayerActive()) {
            if (BGNetworkHelper.isHost()) {
                // Host generates roll
                int r = monsterRng.random(1, 6);
                BGNetworkManager.sendDieRoll(r);
                applyRoll(r);
            }
            // Non-hosts wait for network message
        } else {
            // Single player - existing logic
            int r = monsterRng.random(1, 6);
            applyRoll(r);
        }
    }

    public static void receiveNetworkRoll(int r) {
        applyRoll(r);
    }

    private static void applyRoll(int r) {
        initialRoll = r;
        finalRelicRoll = -1;
        monsterRoll = r;
        setMonsterMoves(monsterRoll);
        awaitingConfirmation = true;
        playersConfirmed.clear();
        // Show UI for die modification
        showDieModificationUI();
    }

    public static void requestModification(int playerId, DieModification mod) {
        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendDieModificationRequest(playerId, mod);
        } else {
            applyModification(mod);
        }
    }

    public static void confirmRoll(int playerId) {
        playersConfirmed.add(playerId);
        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendDieConfirmation(playerId);
        }
        checkAllConfirmed();
    }

    private static void checkAllConfirmed() {
        if (playersConfirmed.size() >= BGNetworkHelper.getPlayerCount()) {
            lockInRoll();
        }
    }
}
```

### 4.2 Die Modification Synchronization

**Order of Operations:**

1. Host rolls die, broadcasts result
2. All players see die result and available modifications
3. Any player can request a modification (uses their relic/potion)
4. Modification requests sent to host, host applies in order received
5. Modified result broadcast to all players
6. Each player must confirm the roll (click "Confirm" or "Lock In")
7. Once all players confirm, turn proceeds

**Modification Request Message:**

```java
public class DieModification {

    public enum Type {
        REROLL,
        ADJUST_UP,
        ADJUST_DOWN,
    }

    public Type type;
    public int playerId;
    public String sourceId; // Relic or potion ID that's being used
}
```

**Network Flow:**

```
Host                           Player 2                      Player 3
  |                               |                              |
  |-- Send_DieRoll(4) ----------->|----------------------------->|
  |                               |                              |
  |<- Send_DieModRequest(REROLL)--|                              |
  |                               |                              |
  |-- Apply reroll (new: 2)       |                              |
  |-- Send_DieModified(2) ------->|----------------------------->|
  |                               |                              |
  |                               |<-- Send_DieModRequest(+1) ---|
  |<--------------------------------------------------(+1)-------|
  |                               |                              |
  |-- Apply +1 (new: 3)           |                              |
  |-- Send_DieModified(3) ------->|----------------------------->|
  |                               |                              |
  |<- Send_DieConfirm(P2) --------|                              |
  |                               |<-- Send_DieConfirm(P3) ------|
  |<-------------------------------------------------(P3)--------|
  |-- Send_DieConfirm(Host)       |                              |
  |                               |                              |
  |-- All confirmed, lock in      |                              |
  |-- Send_DieLockedIn(3) ------->|----------------------------->|
```

### 4.3 Die UI Updates

**Current UI Elements (to be updated):**

- `LockInRollButton.java` - Change to "Confirm Roll" for MP
- `RerollButton.java` - Works the same, sends network request
- `TheAbacusButton.java` - Works the same, sends network request
- `ToolboxButton.java` - Works the same, sends network request

**New UI Elements:**

```
ui/die/
├── DieConfirmationPanel.java     # Shows who has/hasn't confirmed
├── DieModificationQueue.java     # Shows pending modifications
└── NetworkDieDisplay.java        # Shows die prominently for all players
```

**DieConfirmationPanel Layout:**

```
┌────────────────────────────────┐
│        Die Roll: [4]           │
│   ┌─────────────────────────┐  │
│   │ ✓ Ironclad (Host)       │  │
│   │ ✓ Silent                │  │
│   │ ○ Defect (modifying...) │  │
│   │ ○ Watcher               │  │
│   └─────────────────────────┘  │
│                                │
│  Your relics:                  │
│  • Gambling Chip: Reroll       │
│  • The Abacus: +1/-1           │
│                                │
│     [Confirm Roll]             │
└────────────────────────────────┘
```

### 4.4 Remove TheDieRelic Dependency

**Current:** Die is tied to `BGTheDieRelic` relic

**Change:** Die should be a core game mechanic, not a relic

**Actions:**

1. Move die rolling from relic to `TurnManager` or `CombatManager`
2. Die-modification relics (Gambling Chip, etc.) work on new TheDieManager, not TheDieRelic

---

## 5. Phase 4: Simultaneous Turn System

### 5.1 Turn Phase State Machine

**New Package:** `CoopBoardGame/turns/`

```
turns/
├── TurnPhaseManager.java          # Central turn phase controller
├── TurnPhase.java                 # Enum of phases
├── QueuedAction.java              # Data class for queued actions
├── ActionResolver.java            # Resolves queued actions
└── patches/
    ├── EndTurnButtonPatch.java    # Change to "Ready" button
    ├── CardPlayPatch.java         # Queue cards instead of playing
    └── TurnStartPatch.java        # Intercept turn start
```

**TurnPhase.java:**

```java
public enum TurnPhase {
    DIE_ROLL, // Host rolls, players can modify, all must confirm
    PLANNING, // Players select cards, queue actions (don't execute)
    READY_CHECK, // Waiting for all players to mark ready
    RESOLUTION, // Execute all queued actions in order
    ENEMY_TURN, // All enemies act (top row to bottom, left to right)
    CLEANUP, // End of turn effects, reset for next round
}
```

**TurnPhaseManager.java:**

```java
public class TurnPhaseManager {

    private static TurnPhase currentPhase = TurnPhase.DIE_ROLL;
    private static Map<Integer, List<QueuedAction>> playerQueues = new HashMap<>();
    private static Set<Integer> readyPlayers = new HashSet<>();

    public static void startCombatRound() {
        currentPhase = TurnPhase.DIE_ROLL;
        playerQueues.clear();
        readyPlayers.clear();

        if (BGNetworkHelper.isHost()) {
            TheDie.roll(); // Host rolls
        }
        // Non-hosts wait for die roll message
    }

    public static void onDieConfirmed() {
        transitionTo(TurnPhase.PLANNING);
    }

    public static void queueAction(int playerId, QueuedAction action) {
        playerQueues.computeIfAbsent(playerId, (k) -> new ArrayList<>()).add(action);

        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendQueuedAction(playerId, action);
        }
    }

    public static void markReady(int playerId) {
        readyPlayers.add(playerId);

        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendPlayerReady(playerId);
        }

        checkAllReady();
    }

    private static void checkAllReady() {
        if (readyPlayers.size() >= BGNetworkHelper.getPlayerCount()) {
            transitionTo(TurnPhase.RESOLUTION);
        }
    }

    public static void transitionTo(TurnPhase newPhase) {
        TurnPhase oldPhase = currentPhase;
        currentPhase = newPhase;

        if (BGNetworkHelper.isMultiplayerActive() && BGNetworkHelper.isHost()) {
            BGNetworkManager.sendPhaseTransition(newPhase);
        }

        switch (newPhase) {
            case PLANNING:
                enableCardPlaying();
                break;
            case RESOLUTION:
                resolveAllActions();
                break;
            case ENEMY_TURN:
                executeEnemyTurns();
                break;
            case CLEANUP:
                performCleanup();
                startCombatRound(); // Loop back
                break;
        }
    }

    private static void resolveAllActions() {
        // Resolve by row, bottom to top
        for (int row = 0; row < RowManager.MAX_ROWS; row++) {
            int playerId = RowManager.getPlayerInRow(row);
            if (playerId != -1) {
                List<QueuedAction> actions = playerQueues.get(playerId);
                if (actions != null) {
                    for (QueuedAction action : actions) {
                        ActionResolver.resolve(action);
                    }
                }
            }
        }

        transitionTo(TurnPhase.ENEMY_TURN);
    }
}
```

### 5.2 Action Queueing System

**QueuedAction.java:**

```java
public class QueuedAction {
    public enum ActionType { PLAY_CARD, USE_POTION, ACTIVATE_RELIC, USE_SHIV, USE_MIRACLE }

    public ActionType type;
    public int playerId;
    public String cardId;           // For PLAY_CARD
    public String targetMonsterId;  // UUID of target monster
    public int targetRow;           // Row of target
    public int energyCost;          // Energy spent
    public int xCostValue;          // For X-cost cards

    // Serialization for network
    public byte[] serialize() { ... }
    public static QueuedAction deserialize(byte[] data) { ... }
}
```

**Card Play Interception:**

```java
@SpirePatch(clz = AbstractPlayer.class, method = "useCard")
public static class CardPlayInterceptPatch {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(
        AbstractPlayer __instance,
        AbstractCard c,
        AbstractMonster m,
        int energyOnUse
    ) {
        if (
            BGNetworkHelper.isMultiplayerActive() &&
            TurnPhaseManager.getCurrentPhase() == TurnPhase.PLANNING
        ) {
            // Don't actually play the card - queue it instead
            QueuedAction action = new QueuedAction();
            action.type = QueuedAction.ActionType.PLAY_CARD;
            action.playerId = BGNetworkHelper.getLocalPlayerId();
            action.cardId = c.uuid.toString();
            action.targetMonsterId = m != null ? m.id : null;
            action.energyCost = energyOnUse;

            TurnPhaseManager.queueAction(action.playerId, action);

            // Move card to "queued" visual position
            // Deduct energy visually
            // Update UI to show queued card

            return SpireReturn.Return(null); // Prevent actual card play
        }

        return SpireReturn.Continue();
    }
}
```

### 5.3 "Ready" Button Replacement

**Current:** End Turn button ends your turn

**Multiplayer Change:** End Turn button becomes "Ready" button during PLANNING phase

```java
@SpirePatch(clz = EndTurnButton.class, method = "render")
public static class EndTurnButtonTextPatch {

    @SpirePostfixPatch
    public static void Postfix(EndTurnButton __instance, SpriteBatch sb) {
        if (
            BGNetworkHelper.isMultiplayerActive() &&
            TurnPhaseManager.getCurrentPhase() == TurnPhase.PLANNING
        ) {
            // Change button text from "End Turn" to "Ready"
            // Change button color when player has queued actions
        }
    }
}

@SpirePatch(clz = EndTurnButton.class, method = "disable", paramtypez = { boolean.class })
public static class EndTurnButtonClickPatch {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(EndTurnButton __instance, boolean isEndingTurn) {
        if (
            BGNetworkHelper.isMultiplayerActive() &&
            TurnPhaseManager.getCurrentPhase() == TurnPhase.PLANNING
        ) {
            TurnPhaseManager.markReady(BGNetworkHelper.getLocalPlayerId());
            return SpireReturn.Return(null);
        }

        return SpireReturn.Continue();
    }
}
```

### 5.4 Action Resolution Order

**Board Game Rules:**

1. Players act from bottom row to top row
2. Within each row, cards resolve in the order they were queued
3. After all players, enemy turn begins
4. Enemies act from top row to bottom row, left to right within row
5. Bosses always act last

**ActionResolver.java:**

```java
public class ActionResolver {

    public static void resolve(QueuedAction action) {
        switch (action.type) {
            case PLAY_CARD:
                resolveCardPlay(action);
                break;
            case USE_POTION:
                resolvePotionUse(action);
                break;
            case USE_SHIV:
                resolveShivUse(action);
                break;
            case USE_MIRACLE:
                resolveMiracleUse(action);
                break;
        }
    }

    private static void resolveCardPlay(QueuedAction action) {
        AbstractPlayer player = getPlayerById(action.playerId);
        AbstractCard card = findCardByUuid(action.cardId);
        AbstractMonster target = findMonsterById(action.targetMonsterId);

        // Actually play the card now
        // Use existing card play logic
        player.useCard(card, target, action.energyCost);
    }
}
```

### 5.5 Queued Action Visualization

**New UI:**

```
ui/queue/
├── QueuedCardDisplay.java        # Shows cards in queue order
├── QueuedActionIndicator.java    # Indicator on card when queued
└── OtherPlayerQueuePanel.java    # Shows what other players queued
```

**Visual Layout:**

```
┌─────────────────────────────────────────────────────────────┐
│                     YOUR QUEUE                               │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐                       │
│  │ Strike  │ │ Defend  │ │ Bash    │  [Unqueue] [Ready]    │
│  │ → Slime │ │ (self)  │ │ → Slime │                       │
│  └─────────┘ └─────────┘ └─────────┘                       │
├─────────────────────────────────────────────────────────────┤
│  Other Players:                                              │
│  • Silent (Row 1): 2 cards queued ✓ Ready                   │
│  • Defect (Row 2): 3 cards queued ○ Not Ready               │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Phase 5: Card & Action Networking

### 6.1 AbstractBGCard Network Extensions

**Add to AbstractBGCard.java:**

```java
public abstract class AbstractBGCard extends CustomCard {

    // Existing fields...

    // NEW: Network fields
    public boolean requiresNetworkSync = true; // Most cards do
    public boolean canTargetOtherPlayers = false; // For buff/heal cards

    // Called when card is queued (multiplayer) or played (single player)
    public void onQueued(AbstractPlayer p, AbstractMonster m) {
        // Override in cards that need special queue behavior
    }

    // Called during resolution phase
    public void resolveQueued(AbstractPlayer p, AbstractMonster m) {
        // Default: call use()
        use(p, m);
    }

    // Network serialization
    public CardNetworkData getNetworkData() {
        CardNetworkData data = new CardNetworkData();
        data.cardId = this.uuid.toString();
        data.cardClass = this.getClass().getName();
        data.damage = this.damage;
        data.block = this.block;
        data.magicNumber = this.magicNumber;
        // Add any card-specific state
        return data;
    }
}
```

### 6.2 Card Categorization for Network Sync

**Categories:**

1. **Standard Attack/Block Cards (~200 cards)**
    - Need: Target sync, damage value sync
    - Pattern: Existing `use()` works, just need to sync target selection

2. **Draw/Discard Cards (~40 cards)**
    - Need: Deck state sync after resolution
    - Pattern: Sync deck state at end of resolution phase

3. **Random Effect Cards (~30 cards)**
    - Need: Host-authoritative RNG, sync result
    - Pattern: If random, host resolves, broadcasts result

4. **Multi-Target Cards (~20 cards)**
    - Need: All target selections synced
    - Pattern: Collect all targets, sync before resolution

5. **X-Cost Cards (~15 cards)**
    - Need: X value sync
    - Pattern: Include energyCost in QueuedAction

6. **Choice Cards (~20 cards)**
    - Need: Choice sync before resolution
    - Pattern: Include choice in QueuedAction

### 6.3 Action Network Wrappers

**Pattern:** Wrap actions that have random elements or need sync.

**Example - BGBouncingFlaskAction (random bounce):**

```java
public class BGBouncingFlaskAction extends AbstractGameAction {

    // Existing code...

    @Override
    public void update() {
        if (BGNetworkHelper.isMultiplayerActive() && !BGNetworkHelper.isHost()) {
            // Wait for host to tell us where flask bounced
            return;
        }

        // Host or single player - do normal random logic
        AbstractMonster target = getRandomTarget();

        if (BGNetworkHelper.isMultiplayerActive()) {
            // Broadcast the result
            BGNetworkManager.sendActionResult("BouncingFlask", target.id);
        }

        applyPoison(target);
        this.isDone = true;
    }

    public void receiveNetworkResult(String targetId) {
        AbstractMonster target = findMonsterById(targetId);
        applyPoison(target);
        this.isDone = true;
    }
}
```

### 6.4 Cards Requiring Special Handling

**High-Priority Cards (complex interactions):**

| Card             | Issue                  | Solution                      |
| ---------------- | ---------------------- | ----------------------------- |
| Havoc            | Plays random card      | Host decides, syncs choice    |
| Mayhem           | Plays random card      | Host decides, syncs choice    |
| Distilled Chaos  | Three random cards     | Host decides all three        |
| Seek             | Choose from draw pile  | Player choice, sync selection |
| Hologram         | Choose from discard    | Player choice, sync selection |
| Nightmare        | Creates copies         | Sync card copies              |
| Echo Form        | Plays cards twice      | Sync double play              |
| Burst/Double Tap | Next skill/attack x2   | Track state per player        |
| Corruption       | Skills cost 0, exhaust | Track state per player        |

**Cards That Target Other Players:**

These need new targeting UI:

- Apparatus (give orb slot - Defect only)
- Prayer Wheel (give card reward - doesn't exist in BG?)
- Custom cards that buff allies

---

## 7. Phase 6: Monster & Enemy Networking

### 7.1 Monster State Synchronization

**Key Monster State to Sync:**

- HP, MaxHP
- Block
- Powers (Strength, Vulnerable, Weak, etc.)
- Current Move (nextMove byte)
- Intent
- Position (row assignment)
- Is Dead

**Monster Sync Message:**

```java
public class MonsterStatePayload {

    public String monsterId;
    public int hp;
    public int maxHp;
    public int block;
    public byte nextMove;
    public int row;
    public boolean isDead;
    public List<PowerState> powers;
}
```

### 7.2 Monster Targeting Changes

**Current:** Monster targets `AbstractDungeon.player`

**Multiplayer:** Monster targets player in its row

**Patch:**

```java
@SpirePatch(clz = AbstractMonster.class, method = SpirePatch.CLASS)
public static class MonsterTargetField {

    public static SpireField<AbstractPlayer> rowTarget = new SpireField<>(() -> null);
}

@SpirePatch(clz = AbstractMonster.class, method = "takeTurn")
public static class MonsterTakeTurnPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractMonster __instance) {
        if (BGNetworkHelper.isMultiplayerActive()) {
            AbstractPlayer target = RowManager.getTargetedPlayer(__instance);
            MonsterTargetField.rowTarget.set(__instance, target);
        }
    }
}
```

### 7.3 Boss Special Handling

**Bosses:**

- Are considered "in all rows"
- Can be targeted by any player
- May target all players or specific player based on action type
- Use HP board scaling based on player count

**HP Scaling:**

```java
public static int getBossHp(int baseHp, int playerCount) {
    // From board game rules - HP board shows per-player-count values
    // Example: Slime Boss base 75, scales to 75/100/125/150 for 1/2/3/4 players
    return baseHp + (baseHp / 3) * (playerCount - 1);
}
```

### 7.4 Monster Turn Execution Order

**Enemy Turn Order (from board game rules):**

1. Remove all enemy block
2. Starting with highest row (Row 3), enemies act left to right
3. Then Row 2 enemies left to right
4. Then Row 1, Row 0
5. Bosses always act last

```java
public static void executeEnemyTurns() {
    // 1. Remove all enemy block
    for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
        if (!m.isDead && m.currentBlock > 0) {
            m.loseBlock(m.currentBlock);
        }
    }

    // 2. Execute by row, top to bottom
    for (int row = RowManager.MAX_ROWS - 1; row >= 0; row--) {
        List<AbstractMonster> rowMonsters = RowManager.getMonstersInRow(row);
        for (AbstractMonster m : rowMonsters) {
            if (!m.isDead && !RowManager.isBoss(m)) {
                // Queue monster turn action
                AbstractDungeon.actionManager.addToBottom(new MonsterTurnAction(m));
            }
        }
    }

    // 3. Boss acts last
    if (RowManager.currentBoss != null && !RowManager.currentBoss.isDead) {
        AbstractDungeon.actionManager.addToBottom(new MonsterTurnAction(RowManager.currentBoss));
    }

    // 4. After all monsters acted, transition to cleanup
    AbstractDungeon.actionManager.addToBottom(
        new AbstractGameAction() {
            public void update() {
                TurnPhaseManager.transitionTo(TurnPhase.CLEANUP);
                isDone = true;
            }
        }
    );
}
```

### 7.5 Summon Handling

**Summons:**

- Go in the same row as the enemy that summoned them
- If summoner is boss, go in the row of the targeted player

**SummonAction Wrapper:**

```java
public class BGSummonAction extends AbstractGameAction {

    private AbstractMonster toSummon;
    private AbstractMonster summoner;

    @Override
    public void update() {
        int row;
        if (RowManager.isBoss(summoner)) {
            // Boss summons go to a specific player's row (based on context)
            row = determineBossSummonRow();
        } else {
            row = RowManager.getMonsterRow(summoner);
        }

        RowManager.assignMonsterToRow(toSummon, row);

        // Existing summon logic...

        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendMonsterSummoned(toSummon, row);
        }

        isDone = true;
    }
}
```

---

## 8. Phase 7: Relic & Potion Networking

### 8.1 Relic State Synchronization

**Relic State to Sync:**

- Counter values
- Availability flags (for once-per-combat relics)
- Activation state (for triggered relics)

**AbstractBGRelic Extensions:**

```java
public abstract class AbstractBGRelic extends AbstractRelic {

    // Existing fields...

    // NEW: Network sync
    public RelicNetworkData getNetworkData() {
        RelicNetworkData data = new RelicNetworkData();
        data.relicId = this.relicId;
        data.counter = this.counter;
        // Add relic-specific state
        return data;
    }

    public void applyNetworkData(RelicNetworkData data) {
        this.counter = data.counter;
    }

    // Called when relic triggers on any player
    public void onNetworkTrigger(int playerId) {
        // Override if relic cares about other player triggers
    }
}
```

### 8.2 Die-Controlled Relic Sync

**DieControlledRelic modifications:**

```java
public interface DieControlledRelic {
    // Existing methods...

    // NEW: Network methods
    default void onNetworkDieModification(int playerId, int oldRoll, int newRoll) {
        // Called when any player modifies the die
    }

    default boolean canActivateOnCurrentRoll() {
        // Check if this relic can do something on current die result
        return !getQuickSummary().isEmpty();
    }
}
```

### 8.3 Potion Trading System

**Board Game Rules:**

- Potions can be traded between players outside of combat
- Trading is free, no gold cost
- Max 3 potions per player

**New Package:**

```
potions/trading/
├── PotionTradeManager.java        # Manages trade offers
├── PotionTradeOffer.java          # Data class for offer
└── PotionTradeUI.java             # UI for trading
```

**PotionTradeManager.java:**

```java
public class PotionTradeManager {

    private static Map<Integer, PotionTradeOffer> pendingOffers = new HashMap<>();

    public static void offerPotion(int fromPlayerId, int toPlayerId, AbstractPotion potion) {
        PotionTradeOffer offer = new PotionTradeOffer(fromPlayerId, toPlayerId, potion);
        pendingOffers.put(offer.getId(), offer);

        BGNetworkManager.sendPotionOffer(offer);
    }

    public static void acceptOffer(int offerId) {
        PotionTradeOffer offer = pendingOffers.get(offerId);
        if (offer != null) {
            executeTradeLocally(offer);
            BGNetworkManager.sendPotionOfferAccepted(offerId);
            pendingOffers.remove(offerId);
        }
    }

    public static void declineOffer(int offerId) {
        BGNetworkManager.sendPotionOfferDeclined(offerId);
        pendingOffers.remove(offerId);
    }

    private static void executeTradeLocally(PotionTradeOffer offer) {
        // Remove from giver, add to receiver
        // Check receiver has space (max 3)
    }
}
```

**Trade UI:**

```
┌─────────────────────────────────────────┐
│           POTION TRADING                │
│                                         │
│  Your Potions:                          │
│  [Fire Potion] [Block Potion] [empty]   │
│                                         │
│  Offer to: [Dropdown: Select Player]    │
│                                         │
│  ─────────────────────────────          │
│  Pending Offers:                        │
│  • Defect offers you: Fairy in Bottle   │
│    [Accept] [Decline]                   │
│                                         │
└─────────────────────────────────────────┘
```

### 8.4 Gold Pooling at Merchant

**Board Game Rule:** "You can use your gold to pay for any amount of another player's purchase at a Merchant or Event."

**Implementation:**

```java
@SpirePatch(clz = ShopScreen.class, method = "purchaseCard")
public static class ShopPurchasePatch {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(ShopScreen __instance, AbstractCard card) {
        if (BGNetworkHelper.isMultiplayerActive()) {
            int cost = card.price;
            int playerGold = AbstractDungeon.player.gold;

            if (playerGold < cost) {
                // Show gold pooling UI
                GoldPoolingUI.show(cost - playerGold, card, () -> {
                    // Callback when pooling complete
                    completePurchase(card);
                });
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
}
```

### 8.5 Boss Relic Selection

**Board Game Rule:** "Reveal 1 Boss relic per player + 1. Each player may gain a relic or skip."

**Implementation:**

```java
public class BossRelicSelection {

    private static List<AbstractRelic> revealedRelics = new ArrayList<>();
    private static Map<Integer, AbstractRelic> playerChoices = new HashMap<>();

    public static void startSelection(int playerCount) {
        int relicsToReveal = playerCount + 1;
        revealedRelics.clear();

        for (int i = 0; i < relicsToReveal; i++) {
            AbstractRelic relic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS);
            revealedRelics.add(relic);
        }

        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendBossRelicOptions(revealedRelics);
        }

        showSelectionUI();
    }

    public static void selectRelic(int playerId, AbstractRelic relic) {
        if (playerChoices.containsValue(relic)) {
            // Already claimed!
            return;
        }

        playerChoices.put(playerId, relic);

        if (BGNetworkHelper.isMultiplayerActive()) {
            BGNetworkManager.sendBossRelicSelection(playerId, relic);
        }

        checkAllSelected();
    }

    private static void checkAllSelected() {
        int playerCount = BGNetworkHelper.getPlayerCount();
        if (playerChoices.size() >= playerCount) {
            distributeRelics();
        }
    }
}
```

---

## 9. Phase 8: Power & Effect Networking

### 9.1 Power State Synchronization

**Power State:**

- Amount (stacks)
- Owner (player or monster)
- Special flags (just applied, etc.)

**AbstractBGPower Extensions:**

```java
public abstract class AbstractBGPower extends AbstractPower {

    // Existing...

    public PowerNetworkData getNetworkData() {
        PowerNetworkData data = new PowerNetworkData();
        data.powerId = this.ID;
        data.ownerId = getOwnerId();
        data.amount = this.amount;
        return data;
    }

    // Called when this power triggers on any player
    public void onNetworkTrigger(int playerId, String triggerType) {
        // Override if power needs to react to other players
    }
}
```

### 9.2 Token Cap Implementation

**Board Game Token Caps:**

- Strength: Max 8 (players only)
- Block: Max 20 (players only)
- Poison: Max 30 (global across all enemies)
- Vulnerable: Max 3 (per entity)
- Weak: Max 3 (per entity)

**StrengthCap.java (exists, enhance):**

```java
public class StrengthCap {

    public static final int MAX_STRENGTH = 8;
    public static final int MAX_BLOCK = 20;
    public static final int MAX_POISON_GLOBAL = 30;
    public static final int MAX_VULNERABLE = 3;
    public static final int MAX_WEAK = 3;

    public static int getCurrentGlobalPoison() {
        int total = 0;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            AbstractPower poison = m.getPower(PoisonPower.POWER_ID);
            if (poison != null) {
                total += poison.amount;
            }
        }
        return total;
    }

    public static int getPoisonRoomRemaining() {
        return MAX_POISON_GLOBAL - getCurrentGlobalPoison();
    }
}
```

**Patches for caps:**

```java
@SpirePatch(clz = ApplyPowerAction.class, method = "update")
public static class TokenCapPatch {

    @SpirePrefixPatch
    public static void Prefix(ApplyPowerAction __instance) {
        // Check if applying would exceed cap
        // Reduce amount if needed
        // Show warning if cap reached
    }
}
```

### 9.3 Per-Player Power Tracking

Some powers need to be tracked per player:

- Corruption (skills cost 0)
- Burst (next skill played twice)
- Double Tap (next attack played twice)

**Solution:** These powers already only exist on the player who has them. Network sync handles correctly by syncing per-player state.

### 9.4 Triggered Power Sync

Powers that trigger on events need sync:

- Noxious Fumes (start of turn)
- Combust (end of turn)
- A Thousand Cuts (on card play)

**Pattern:** Trigger locally, sync result (damage dealt, etc.)

---

## 10. Phase 9: UI & Polish

### 10.1 New UI Components

**Multiplayer HUD:**

```
ui/multiplayer/
├── PlayerStatusPanel.java         # Shows all players' status
├── RowIndicator.java              # Shows current row assignments
├── TurnPhaseIndicator.java        # Shows current phase
├── ReadyStatusPanel.java          # Shows who's ready
├── QueuedActionsPanel.java        # Shows queued actions
└── DieConfirmationPanel.java      # Shows die confirmation status
```

**PlayerStatusPanel Layout:**

```
┌────────────────────────────────────────────────────────────────┐
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│ │ Ironclad │ │ Silent   │ │ Defect   │ │ Watcher  │          │
│ │ HP: 45/75│ │ HP: 32/70│ │ HP: 56/75│ │ HP: 41/72│          │
│ │ E: 2/3   │ │ E: 1/3   │ │ E: 3/3   │ │ E: 0/3   │          │
│ │ Row: 0   │ │ Row: 1   │ │ Row: 2   │ │ Row: 3   │          │
│ │ ✓ Ready  │ │ ○ Plan.. │ │ ✓ Ready  │ │ ○ Plan.. │          │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
└────────────────────────────────────────────────────────────────┘
```

### 10.2 Combat Screen Layout Changes

**Changes needed for row display:**

1. Move player characters to left side, arranged vertically by row
2. Move enemies to right side, arranged in rows
3. Add row background colors
4. Add boss area at top

### 10.3 Hand Display for Non-Active Rows

**Option 1:** Only show active player's hand

- Other players see their own screen
- Simplest implementation

**Option 2:** Show all hands in mini-view

- Complex, potential information overload
- Better for spectators

**Recommendation:** Option 1 for initial implementation

### 10.4 Turn Phase Announcements

**Visual feedback for phase transitions:**

- "DIE ROLL" - Show die rolling animation
- "PLANNING PHASE" - Enable card play, show queue UI
- "WAITING FOR PLAYERS" - Show ready status
- "RESOLVING ACTIONS" - Show actions playing out
- "ENEMY TURN" - Show enemy intents executing

### 10.5 Network Status Indicator

**Show connection status:**

- Host vs Client indicator
- Ping/latency
- Connected players
- Sync status

---

## 11. Phase 10: Testing & Validation

### 11.1 Unit Tests

**Test Categories:**

1. Row assignment logic
2. Die roll synchronization
3. Action queueing and resolution
4. Token cap enforcement
5. Network message serialization

### 11.2 Integration Tests

**Test Scenarios:**

1. 2-player game, full Act 1 run
2. 4-player game, boss fight
3. Player disconnect/reconnect
4. Network latency simulation
5. Die modification sequence

### 11.3 Compatibility Tests

**Test With:**

- TogetherInSpire base mod
- Board game single-player mode
- Various ascension levels
- All 4 characters
- All bosses

### 11.4 Stress Tests

**Load Testing:**

- Maximum message throughput
- State sync with many cards/relics
- Combat with many monsters

---

## 12. Technical Specifications

### 12.1 New Message Types

| Message Type         | Direction  | Payload                           | Description             |
| -------------------- | ---------- | --------------------------------- | ----------------------- |
| `BG_DieRoll`         | Host→All   | `{roll: int}`                     | Initial die roll        |
| `BG_DieModRequest`   | Any→Host   | `{type, playerId, sourceId}`      | Request to modify die   |
| `BG_DieModified`     | Host→All   | `{newRoll: int, modBy: playerId}` | Die was modified        |
| `BG_DieConfirm`      | Any→All    | `{playerId}`                      | Player confirms die     |
| `BG_DieLocked`       | Host→All   | `{finalRoll: int}`                | Die is locked in        |
| `BG_QueueAction`     | Any→All    | `QueuedAction`                    | Player queued an action |
| `BG_UnqueueAction`   | Any→All    | `{playerId, actionId}`            | Player unqueued action  |
| `BG_PlayerReady`     | Any→All    | `{playerId}`                      | Player is ready         |
| `BG_PhaseChange`     | Host→All   | `{phase: TurnPhase}`              | Turn phase changed      |
| `BG_RowAssign`       | Host→All   | `{playerId, row}`                 | Player row assignment   |
| `BG_MonsterRow`      | Host→All   | `{monsterId, row}`                | Monster row assignment  |
| `BG_RowSwitch`       | Any→Host   | `{playerId, newRow}`              | Request row switch      |
| `BG_PotionOffer`     | Any→Target | `PotionTradeOffer`                | Offer potion trade      |
| `BG_PotionAccept`    | Any→All    | `{offerId}`                       | Accept trade            |
| `BG_BossRelicReveal` | Host→All   | `{relics[]}`                      | Boss relics revealed    |
| `BG_BossRelicSelect` | Any→All    | `{playerId, relicId}`             | Player selected relic   |

### 12.2 State Sync Frequency

| State            | Sync Frequency | Method              |
| ---------------- | -------------- | ------------------- |
| Die roll         | On change      | Immediate broadcast |
| Turn phase       | On transition  | Immediate broadcast |
| Player HP/Block  | On change      | Delta sync          |
| Player energy    | On change      | Delta sync          |
| Player powers    | On change      | Delta sync          |
| Monster HP/Block | On change      | Delta sync          |
| Monster powers   | On change      | Delta sync          |
| Card queue       | On add/remove  | Delta sync          |
| Row assignments  | On change      | Immediate broadcast |

### 12.3 Performance Targets

- Message latency: <100ms
- Full state sync: <500ms
- Actions per second: 60+
- Max concurrent players: 4

---

## 13. Risk Assessment

### High Risk Items

| Risk                        | Impact                  | Mitigation                          |
| --------------------------- | ----------------------- | ----------------------------------- |
| TogetherInSpire API changes | Could break integration | Use reflection, version checking    |
| Desync during combat        | Game-breaking           | Periodic full state sync, checksums |
| Network latency spikes      | Poor user experience    | Predictive display, buffer actions  |
| Complex card interactions   | Unexpected behavior     | Extensive testing, fallback logic   |

### Medium Risk Items

| Risk                       | Impact        | Mitigation                         |
| -------------------------- | ------------- | ---------------------------------- |
| Performance with 4 players | Slowdown      | Optimize message batching          |
| UI clutter with all info   | Hard to read  | Collapsible panels, focus modes    |
| Save/load complexity       | Lost progress | Frequent auto-saves, recovery mode |

### Low Risk Items

| Risk                     | Impact       | Mitigation                         |
| ------------------------ | ------------ | ---------------------------------- |
| Single-player regression | Feature loss | Maintain test suite                |
| Localization issues      | Minor UX     | Use existing localization patterns |

---

## Implementation Priority

### Must Have (MVP)

1. Network infrastructure (Phase 1)
2. Row system (Phase 2)
3. Die synchronization (Phase 3)
4. Basic turn phase management (Phase 4)
5. Card play sync (Phase 5)
6. Monster state sync (Phase 6)

### Should Have

1. Potion trading (Phase 7)
2. Boss relic selection (Phase 7)
3. Token caps (Phase 8)
4. Full UI polish (Phase 9)

### Nice to Have

1. Spectator mode
2. Replay system
3. Advanced statistics
4. Custom game modes

---

## Appendix A: File Change Summary

### New Files (~50 files)

- `network/` package (~15 files)
- `rows/` package (~10 files)
- `turns/` package (~8 files)
- `ui/multiplayer/` (~10 files)
- `potions/trading/` (~3 files)

### Modified Files (~20 files)

- `thedie/TheDie.java` - Major refactor
- `cards/AbstractBGCard.java` - Add network methods
- `relics/AbstractBGRelic.java` - Add network methods
- `monsters/AbstractBGMonster.java` - Add row tracking
- `powers/AbstractBGPower.java` - Add network methods
- `CoopBoardGame.java` - Add network initialization
- `ui/LockInRollButton.java` - Update for multiplayer

### Deprecated Files (51 files)

- Entire `multicharacter/` directory

---

## Appendix B: Dependencies

### Required

- TogetherInSpire (latest version)
- ModTheSpire 3.30.0+
- BaseMod 5.44.0+
- StSLib 2.4.0+

### Optional

- Bestiary (for enemy info display)

---

## Appendix C: Testing Checklist

### Pre-Release Testing

- [ ] 2-player full run (Act 1-3)
- [ ] 3-player full run
- [ ] 4-player full run
- [ ] All characters tested
- [ ] All bosses tested
- [ ] Disconnect/reconnect tested
- [ ] Die modification with all relics tested
- [ ] Potion trading tested
- [ ] Boss relic selection tested
- [ ] Token caps verified
- [ ] Single-player mode still works
- [ ] Performance acceptable

---

_Document Version: 1.0_
_Last Updated: 2024_
_Author: Claude (AI Assistant)_
