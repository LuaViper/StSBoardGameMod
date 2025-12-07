# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**The Board Game** is a Slay the Spire mod that adapts the physical board game (which itself was based on the video game) back into the video game. The project implements board game rules for single-player mode and is being extended to support cooperative multiplayer using the TogetherInSpire mod architecture.

**Current Status:** Single-player board game mechanics are implemented. The goal is to add cooperative multiplayer support while maintaining board game rules (dice rolls, row-based combat, token limits, simultaneous turns, potion trading, etc.).

## Build & Development Commands

### Building and Running

**Windows Command Line** (from `D:\STS_BG_Mod\StSCoopBoardGameCoopMod`):

```cmd
# Package the mod (creates JAR in target/ and copies to SteamLibrary/mods/)
mvnw.cmd package

# Run tests (part of development iteration process)
mvnw.cmd test

# Clean build artifacts
mvnw.cmd clean

# Clean and rebuild (recommended after major changes)
mvnw.cmd clean package
```

**Unix/Mac/Linux** (or if you have Maven installed globally):

```bash
# Use ./mvnw (Unix) or mvn (if installed) instead of mvnw.cmd
mvn package
mvn test
mvn clean
mvn clean package
```

**Typical Development Iteration:**

1. Make code changes in IntelliJ
2. Run `mvnw.cmd test` to verify tests pass
3. Run `mvnw.cmd package` to compile and deploy to Steam mods folder
4. Launch Slay the Spire to test changes in-game
5. Repeat as needed

### Development in IntelliJ

- **Steam Installation Path:** Configure in `pom.xml` under `<steam.windows>` property (default: `D:\SteamLibrary\steamapps`)
- **Platform Selection:** In Maven tab → Profiles, enable only the profile for your OS (Windows is default)
- **JAR Auto-Copy:** Maven build automatically copies compiled JAR to `SlayTheSpire/mods/` folder

### Testing Single Changes

After making changes, run `mvn package` which will:

1. Compile Java sources
2. Package resources
3. Create `CoopBoardGame.jar` in `target/`
4. Copy JAR to Steam mods folder
5. Launch Slay the Spire via Steam with ModTheSpire to test

## Architecture Overview

### Core Design Pattern

The mod uses a **framework-based extension architecture** built on ModTheSpire/BaseMod:

- **Entry Point:** `CoopBoardGame.java` - All mod initialization happens here via subscriber interfaces
- **Base Classes:** Every game element (cards, relics, monsters, etc.) extends a `AbstractBG*` base class
- **Actions:** Complex card/relic effects are implemented as custom `AbstractGameAction` subclasses
- **Patches:** Vanilla game behavior modifications use SpirePatch annotations (reflection-based)

### Key Architectural Decisions

1. **No Direct Game File Modification:** All changes via ModTheSpire patching system
2. **Separation of Concerns:** Each package handles one domain (cards/, relics/, monsters/, etc.)
3. **Interface-Based Features:** Board game mechanics use interfaces (`DieControlledRelic`, `MultiCreature`)
4. **ID-Based Registration:** All content uses unique string IDs via `makeID()` helper
5. **Localization-First:** All user-facing text in JSON files, not hardcoded

### Package Structure

```
CoopBoardGame/
├── CoopBoardGame.java           # Main mod initialization (1117 lines)
├── characters/              # 7 character classes (BGIronclad, BGSilent, etc.)
├── cards/                   # 323 cards across 7 colors (Red, Blue, Green, Purple, Colorless, Curse, Status)
├── relics/                  # 96 relics with trading/payment mechanics
├── monsters/                # 65 enemies across 4 acts (bgexordium, bgcity, bgbeyond, bgending)
├── thedie/                  # Core dice roll mechanic for board game
├── multicharacter/          # Cooperative multiplayer grid system (51 files)
│   ├── grid/                # Grid rendering (GridTile, GridSubgrid, GridBackground)
│   └── patches/             # Coop-specific UI/rendering patches
├── actions/                 # 150+ custom action classes for complex effects
├── powers/                  # 101 temporary effects/statuses
├── potions/                 # 24 consumable items
├── events/                  # 44 non-combat encounters
├── ui/                      # Custom UI elements (dice buttons, relic buttons)
├── screen/                  # Custom screens (target selection, orb selection)
├── patches/                 # Vanilla game patches (ascension, card usage, etc.)
└── util/                    # Helpers (TextureLoader, ID validation)
```

## Board Game Mechanics (vs Vanilla Slay the Spire)

The key differences that define "board game mode":

### 1. Dice Roll System (thedie/TheDie.java)

- **Turn Start:** Roll 1d6, display result to player
- **Monster Moves:** Dice determines which move monsters use (via `DieControlledMoves` interface)
- **Relic Activation:** Some relics trigger on specific dice values (`DieControlledRelic` interface)
- **Modification Phase:** Player can use relics/potions to modify roll before "locking in"
    - `BGGamblingChip` - Reroll the die
    - `BGTheAbacus` - Adjust die value ±1
    - `BGToolbox` - Adjust die value ±1
    - `BGGamblersBrew` potion - Reroll
- **UI:** `LockInRollButton`, `RerollButton`, `TheAbacusButton`, `ToolboxButton` in overlay menu

### 2. Grid-Based Positioning (multicharacter/)

- **MultiCharacter:** Cooperative character (0 energy, 9 HP) containing multiple subcharacters
- **Row System:** Creatures positioned in grid rows (tracked via `MultiCreature.currentRow` field)
- **Combat Order:** Players act bottom-to-top, monsters act top-to-bottom
- **Grid Rendering:** `BGMultiCreatureGrid` → `GridSubgrid` → `GridTile` (216x163px each)

### 3. Token Caps (Not Yet Implemented - Future Work)

- Strength: Max 8 (Ironclad)
- Block: Max 20 (all characters)
- Poison: Max 30 (global, Silent)
- Vulnerable/Weak: Max 3 (per entity)

### 4. Relic Trading System (relics/AbstractBGRelic.java)

- **Payment Mechanic:** Relics can be used as payment (`usableAsPayment()`)
- **Trading Screen:** Custom UI for trading relics between players
- **Restrictions:** BOSS, STARTER, SPECIAL tier relics cannot be traded

### 5. Energy System

- **Single Player:** 3 energy/turn (standard)
- **MultiCharacter:** 0 energy (each subcharacter has independent energy)

## Multiplayer Architecture (Based on TogetherInSpire)

Reference: `ModCopy/TogetherInSpire/stsogether_architecture_analysis.md`

### Network Layer

- **P2P Mode:** Steam Networking API (peer-to-peer mesh)
- **PF Mode:** Socket-based TCP (client-server, port forwarding required)
- **Message Protocol:** Serialized `NetworkMessage` objects with request type + payload
- **State Replication:** Full player state synchronized via `P2PPlayer` objects

### Key Extension Points for Board Game Features

#### Simultaneous Turn System (Needs Implementation)

Current TogetherInSpire is **sequential** (one player acts, others watch). Board game needs:

- **Turn Phase State Machine:**
    - `PLANNING` - Players queue cards without executing
    - `READY_CHECK` - Wait for all players to mark ready
    - `RESOLUTION` - Execute all queued actions simultaneously
    - `ENEMY_TURN` - Monsters act
    - `CLEANUP` - End of turn effects
- **New Message Types:** `QueuedAction`, `PlayerReady`, `DieRolled`, `PhaseTransition`
- **New Callbacks:** `OnActionQueued()`, `OnPlayerReady()`, `OnDieRolled()`, `OnPhaseChanged()`

#### Shared Die Roll (Needs Implementation)

- **Host Authority:** Only host rolls die, broadcasts result to all players
- **Message:** `Send_DieRolled(int result)`
- **Storage:** Add `currentDieResult` to `P2PClientData`
- **Synchronization:** All players must see same die result before resolution phase

#### Row-Based Combat (Needs Implementation)

- **Data Model:** Add `combatRow` field to `P2PPlayer`
- **Enemy Assignment:** Map enemies to rows (`Map<Integer, EnemyRowAssignment>`)
- **Targeting Rules:** Enemies only target player in their row (Boss targets all)
- **UI:** Row selection screen between combats

#### Potion Trading (Partially Implemented)

- TogetherInSpire already supports modifying other players' potions
- **Enhancement:** Add trade UI with `Send_TradePotion(potion, fromPlayerID, toPlayerID)` message

## Common Development Tasks

### Adding a New Card

1. Create class extending `AbstractBGCard` in appropriate package (`cards/BGRed/`, etc.)
2. Implement constructor with card properties (cost, damage, block, description)
3. Override `use(AbstractPlayer p, AbstractMonster m)` to define card effect
4. Use existing actions from `actions/` package or create new `AbstractGameAction` subclass
5. Register card in `CoopBoardGame.receiveEditCards()`
6. Add localization strings to `resources/CoopBoardGameModResources/localization/eng/[Card/Power/Relic]-Strings.json`

**Example Pattern:**

```java
public class MyNewCard extends AbstractBGCard {

    public static final String ID = makeID("MyNewCard");

    public MyNewCard() {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        baseDamage = 6;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn)));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }
}
```

### Adding a Dice-Controlled Relic

1. Create class extending `AbstractBGRelic` implementing `DieControlledRelic`
2. Implement `getQuickSummary()` to explain what relic does on current die roll
3. Set `available = true/false` based on die value in `atStartOfTurn()`
4. Implement relic effect logic
5. Register in `CoopBoardGame.receiveEditRelics()`

### Adding a Monster with Dice Moves

1. Create class extending `AbstractBGMonster` implementing `DieControlledMoves`
2. Define move sets for each die result (1-6)
3. Implement `takeTurn()` to execute selected move
4. Use `TheDie.monsterRoll` to determine which move set to use
5. Register in appropriate dungeon file (`dungeons/BGExordium.java`, etc.)

### Adding a Cooperative Grid Character

1. Extend `AbstractBGPlayer`
2. Attach `MultiCreature` field via `SpireField`
3. Set `currentRow` for positioning
4. Use `BGMultiCreatureGrid` for rendering
5. Handle energy management (0 for parent, individual for subcharacters)

### Patching Vanilla Game Behavior

1. Create patch class in `patches/` directory
2. Use `@SpirePatch` annotation to target vanilla class/method
3. Use `@SpireInsertPatch`, `@SpirePrefixPatch`, or `@SpirePostfixPatch`
4. Use `SpireField` to add fields to vanilla classes without modifying source
5. See `patches/Ascension.java` or `patches/TransformPatch.java` for examples

## Critical Files to Understand

### Core Initialization

- **CoopBoardGame.java** - All mod registration and initialization (characters, cards, relics, events)

### Base Classes (Extend These)

- **characters/AbstractBGPlayer.java** - Base for all playable characters
- **cards/AbstractBGCard.java** - Base for all cards (adds `defaultSecondMagicNumber` support)
- **relics/AbstractBGRelic.java** - Base for all relics (adds payment/trading mechanics)
- **monsters/AbstractBGMonster.java** - Base for all enemies (adds row positioning)
- **powers/AbstractBGPower.java** - Base for all temporary effects

### Board Game Mechanics

- **thedie/TheDie.java** - Dice rolling, modification, and monster move selection
- **multicharacter/MultiCharacter.java** - Cooperative grid character
- **multicharacter/grid/BGMultiCreatureGrid.java** - Grid rendering system
- **ui/LockInRollButton.java** - Dice confirmation UI

### Multiplayer Integration Points

- **ModCopy/TogetherInSpire/** - Reference implementation for networking
- See `stsogether_architecture_analysis.md` for detailed network architecture

## Known Issues & Limitations

### Technical Faults (from README.md)

- Quick Start interface can softlock (use map screen to escape)
- Some events can softlock without enough cards
- Cards use wrong UI graphics in some situations
- Score bonuses calculated incorrectly

### Board Game Inaccuracies

- Physical token limits not yet implemented (Strength/Block/Poison/Vulnerable/Weak caps)
- Non-card-reward decks can produce duplicates of unique cards
- Ironclad + Gremlin Nob + Feel No Pain interaction ordering issue

### Multiplayer Not Yet Implemented

- Simultaneous turn system (currently sequential in TogetherInSpire)
- Shared die roll synchronization
- Row-based combat with multiple players
- Potion trading UI (partial support exists)
- Boss relic multi-choice system (reveal N+1, each player picks one)

## Dependencies

### Required Mods

- **ModTheSpire** 3.30.0 - Mod loader framework (loads JAR files, enables patching)
- **BaseMod** 5.44.0 - Modding API (CustomCard, CustomPlayer, event registration, etc.)
- **StSLib** 2.4.0 - Utility library (custom keywords, icons, targeting)

### Optional Mods

- **Bestiary** 0.1.1 - Enemy glossary support (patches in `patches/bestiary/`)

### Future Multiplayer Dependency

- **TogetherInSpire** - Cooperative multiplayer networking (reference implementation in `ModCopy/`)

## Resource Files

### Localization

- `src/main/resources/CoopBoardGameModResources/localization/eng/`
    - `Card-Strings.json` - Card names, descriptions, upgrades
    - `Relic-Strings.json` - Relic names, descriptions, flavor text
    - `Power-Strings.json` - Power names, descriptions
    - `Event-Strings.json` - Event dialog text, options
    - `Monster-Strings.json` - Monster names, move descriptions
    - `UI-Strings.json` - UI element text

### Assets

- `src/main/resources/CoopBoardGameModResources/images/`
    - `characters/` - Character sprites and portraits
    - `cards/` - Card art (512x512 for power/skill/attack)
    - `relics/` - Relic icons
    - `monsters/` - Enemy sprites
    - `ui/` - UI element graphics

## Board Game Rules Reference

See `.llm/CoopBoardGameRules.txt` for complete physical board game rulebook. Key mechanics:

- **Cooperative:** All players win or lose together
- **Dice Roll:** 1d6 at start of each combat round determines monster moves
- **Row-Based Combat:** Each player occupies a row, enemies in that row target that player
- **Token Limits:** Strength (8), Block (20), Poison (30), Vulnerable/Weak (3)
- **Energy:** Always 3 per turn (no energy relic scaling)
- **Simultaneous Turns:** All players plan actions, then resolve together
- **Potion Trading:** Only potions can be traded between players (max 3 per player)
- **Boss Relics:** Reveal (# players + 1) relics, each player picks one

## Future Development Roadmap

### Phase 1: Core Multiplayer Mechanics

- [ ] Implement turn phase state machine (PLANNING → READY_CHECK → RESOLUTION)
- [ ] Add action queueing system (cards selected but not executed)
- [ ] Implement "Ready" button and all-players-ready check
- [ ] Add shared die roll broadcast system
- [ ] Display die result to all players

### Phase 2: Combat Rules

- [ ] Implement row assignment system for players and enemies
- [ ] Add row-based enemy targeting (enemies only target player in their row)
- [ ] Implement token caps (Strength 8, Block 20, Poison 30, Vulnerable/Weak 3)
- [ ] Fix energy reset to always be 3 (ignore energy relics in board game mode)
- [ ] Modify block to reset at start of turn (not end)

### Phase 3: Progression & Items

- [ ] Implement shared map progression (party moves together)
- [ ] Add boss relic multi-choice system (reveal N+1, each picks one)
- [ ] Complete potion trading UI (already partially supported)
- [ ] Implement gold pooling at merchants

### Phase 4: Polish

- [ ] Add row switching UI between combats
- [ ] Implement enemy action preview based on die result
- [ ] Add visual indicators for simultaneous action resolution
- [ ] Create board game mode tutorial/onboarding

## Debugging Tips

### Common Issues

- **JAR Not Loading:** Check `ModTheSpire.json` is valid JSON, verify Steam path in `pom.xml`
- **Cards Not Appearing:** Verify registration in `CoopBoardGame.receiveEditCards()`, check ID uniqueness
- **Relics Not Working:** Check `onEquip()`, `atTurnStart()`, other hooks are implemented
- **Patches Not Applying:** Verify target class/method names match vanilla game, check ModTheSpire logs
- **Multiplayer Desyncs:** Ensure deterministic RNG, use host-authoritative die rolls

### Logging

- Use `logger.info()` from `CoopBoardGame` class for debugging
- ModTheSpire logs appear in Steam console and `ModTheSpire-*/run.log`
- Enable verbose logging via ModTheSpire launcher settings

## Contributing

When modifying code:

1. Follow existing naming conventions (`BG` prefix for all mod classes)
2. Use `makeID()` helper for all IDs
3. Add localization strings, don't hardcode text
4. Test with all 4 characters (Ironclad, Silent, Defect, Watcher)
5. Verify compatibility with existing relics/powers
6. Document complex interactions in code comments
7. Update this CLAUDE.md if architecture changes significantly
