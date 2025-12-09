# Code Review: TODO/FIXME Analysis

This document tracks known issues found via TODO/FIXME comments and provides patterns for finding similar problems.

---

## Summary

| Priority  | Count | Category                                 |
| --------- | ----- | ---------------------------------------- |
| 🔴 High   | 3     | Softlocks, Missing Features              |
| 🟡 Medium | 8     | Code Quality, Incomplete Implementations |
| 🟢 Low    | 5     | Polish, Optimization                     |

---

## 🔴 High Priority Issues

### 1. FIXME: Hardcoded Character Title

**File:** `src/main/java/CoopBoardGame/characters/BGIronclad.java:39`

```java
//FIXME: Shouldn't be hardcoded
this.title = "Ironclad";
```

**Problem:** Breaks localization/i18n support.

**Fix:**

```java
this.title = CardCrawlGame.languagePack.getCharacterString(ID).NAMES[0];
```

**Find Similar Issues:**

```bash
# Search for hardcoded character names
grep -rn "this.title = \"" src/main/java/CoopBoardGame/characters/
grep -rn "\.title = \"" src/main/java/CoopBoardGame/
```

---

### 2. Potential Softlock in Neow Event Menu

**File:** `src/main/java/CoopBoardGame/neow/BGNeowQuickStart.java:233`

```java
//TODO: This might softlock? Haven't been able to confirm
```

**Status:** Needs testing/investigation

**Find Similar Issues:**

```bash
grep -rn "softlock" src/main/java/CoopBoardGame/
grep -rn "might.*lock\|may.*lock\|could.*lock" src/main/java/CoopBoardGame/
```

---

### 3. Multi-room Navigation Softlock

**File:** `src/main/java/CoopBoardGame/multicharacter/patches/MultiDungeonTransitionPatches.java:45`

```java
//TODO if multiple rooms are navigated it softlocks, need to wait for first anim to finish
```

**Problem:** Game-breaking in multiplayer room transitions.

**Suggested Fix:** Add animation completion check before allowing next navigation.

---

## 🟡 Medium Priority Issues

### 4. Missing Energy-Based Card Generation

**File:** `src/main/java/CoopBoardGame/cards/BGColorless/BGMetamorphosis.java:44`

```java
//TODO: generate cards based on current energy
```

**Problem:** Card effect incomplete.

---

### 5. Incomplete Monster Behavior (Snake Plant)

**File:** `src/main/java/CoopBoardGame/monsters/bgcity/BGSnakePlant.java:99`

```java
//TODO: splits based on shield/weapons
```

---

### 6. Awakened One Phase 2 Effect Missing

**File:** `src/main/java/CoopBoardGame/monsters/bgbeyond/BGAwakenedOne.java:206`

```java
//TODO: should probably do something too
```

---

### 7. Fairy Potion Implementation Incomplete

**File:** `src/main/java/CoopBoardGame/potions/BGFairyPotion.java:58`

```java
//TODO!!!!
```

**Priority:** HIGH - Multiple exclamation marks suggest urgency.

---

### 8. Duplicate Dice Ability Code

**File:** `src/main/java/CoopBoardGame/thedie/TheDie.java:304,351`

```java
//TODO: this is duplicate code of getDieAbilitiesFromInteger
```

**Problem:** Maintenance burden, potential desync bugs.

**Find Similar Issues:**

```bash
grep -rn "duplicate\|duplicated\|copy of\|same as" src/main/java/CoopBoardGame/
```

---

### 9. Variable Naming Inconsistency

**File:** `src/main/java/CoopBoardGame/powers/BGDoubleTapPower.java:30`

```java
//TODO: probably should use the "amount" variable instead of "doubletapsRemaining"
```

**Find Similar Issues:**

```bash
# Look for custom tracking variables that shadow base class fields
grep -rn "Remaining\|Counter\|Count" src/main/java/CoopBoardGame/powers/ | grep -v "amount"
```

---

### 10. Greedy Gremlins Power Condition Unknown

**File:** `src/main/java/CoopBoardGame/powers/BGGreedyGremlinsPower.java:37`

```java
//TODO is this even the right condition???
```

**Status:** Needs review against board game rules.

---

### 11. Snecko Power Cost Tracking

**File:** `src/main/java/CoopBoardGame/powers/BGSneckoPower.java:55`

```java
//TODO: might not be consistent in tracking costs properly? Seems to work right now
```

---

## 🟢 Low Priority Issues

### 12. Hardcoded "Board Game" Label

**File:** `src/main/java/CoopBoardGame/patches/OtherModsPatches.java:45`

```java
label.text = "Board Game"; // TODO: This should be translated eventually...
```

**Find Similar Issues:**

```bash
grep -rn "\"Board Game\"\|= \".*\".*//.*translat" src/main/java/CoopBoardGame/
```

---

### 13. Font Size for Long Abilities

**File:** `src/main/java/CoopBoardGame/thedie/TheDie.java:209`

```java
//TODO: see if we can shrink the font here for longer abilities
```

---

### 14. Animation Timing Adjustment

**File:** `src/main/java/CoopBoardGame/cards/AbstractBGCard.java:606`

```java
//todo: adjust the timing here so that single card plays don't skip directly to block break?
```

---

### 15. Color Calculation Optimization

**File:** `src/main/java/CoopBoardGame/cards/AbstractBGCard.java:361,377`

```java
//todo: maybe calculate these colors once during init instead of every call?
```

---

### 16. Singleplayer Options Check

**File:** `src/main/java/CoopBoardGame/savables/CurrentRoom.java:35`

```java
//todo check singleplayer options
```

---

## Main CoopBoardGame.java TODOs

**File:** `src/main/java/CoopBoardGame/CoopBoardGame.java`

| Line | Issue                                                            |
| ---- | ---------------------------------------------------------------- |
| 66   | Game softlocks if CoopBoardGame character started on new profile |
| 67   | PostCreditsNeow easter egg not implemented                       |
| 98   | RGB values for BG_DEFECT_BLUE are guessed                        |
| 178  | Still using placeholder TheDefaultMod badge                      |
| 465  | Non-repeating monster deck not fully implemented                 |

---

## Search Patterns for Finding Issues

### Find All TODOs and FIXMEs

```bash
grep -rn "TODO\|FIXME\|XXX\|HACK\|BUG" src/main/java/CoopBoardGame/
```

### Find Incomplete Implementations

```bash
grep -rn "not implemented\|incomplete\|stub\|placeholder" src/main/java/CoopBoardGame/
```

### Find Potential Null Issues

```bash
grep -rn "// null\|//null\|might be null\|could be null" src/main/java/CoopBoardGame/
```

### Find Hardcoded Values

```bash
# Hardcoded strings that might need localization
grep -rn "\.text = \"" src/main/java/CoopBoardGame/
grep -rn "= \"[A-Z].*\";" src/main/java/CoopBoardGame/

# Hardcoded numbers that might be magic numbers
grep -rn "= [0-9][0-9][0-9]" src/main/java/CoopBoardGame/
```

### Find Commented-Out Code

```bash
grep -rn "^[[:space:]]*//[[:space:]]*[a-zA-Z].*(" src/main/java/CoopBoardGame/ | head -50
```

### Find Empty/Stub Methods

```bash
grep -rA2 "public void\|public boolean\|public int" src/main/java/CoopBoardGame/ | grep -B1 "^.*{$" | grep -A1 "^.*}$"
```

---

## Action Plan

### Quick Wins (< 30 min each)

- [ ] Fix `BGIronclad.java` hardcoded title
- [ ] Fix `OtherModsPatches.java` hardcoded "Board Game" string
- [ ] Rename variable in `BGDoubleTapPower.java`
- [ ] Check other character files for same hardcoded title issue

### Investigation Required

- [ ] Test softlock scenario in `BGNeowQuickStart.java`
- [ ] Review `BGGreedyGremlinsPower` condition against rules
- [ ] Verify `BGSneckoPower` cost tracking in edge cases

### Refactoring Tasks

- [ ] Deduplicate dice ability code in `TheDie.java`
- [ ] Extract monster registration from `CoopBoardGame.java` to registry class
- [ ] Extract event registration from `CoopBoardGame.java` to registry class

### Feature Completion

- [ ] Implement energy-based card generation in `BGMetamorphosis.java`
- [ ] Complete `BGFairyPotion` implementation
- [ ] Add shield/weapon split for `BGSnakePlant.java`
- [ ] Add phase 2 effect for `BGAwakenedOne.java`

---

## How to Use This Document

1. **Before starting work:** Check if the area you're modifying has known issues listed here
2. **When fixing an issue:** Mark it as complete with `[x]` and add the commit/PR reference
3. **When finding new issues:** Add them to the appropriate section with file path and line number
4. **Periodically:** Run the search patterns above to find new issues

---

_Last updated: [Date]_
_Generated from codebase scan_
