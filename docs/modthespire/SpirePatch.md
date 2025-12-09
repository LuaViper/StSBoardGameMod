# SpirePatch

SpirePatch is a system that enables mods to inject code into Slay The Spire. ModTheSpire identifies patch classes via the `@SpirePatch` annotation and supports six distinct patching methodologies.

## Core Patch Types

**Prefix** - Executes code at method start

**Postfix** - Executes code at method end, optionally modifying return values

**Insert** - Injects code at specific line numbers within methods

**Instrument** - Leverages Javassist API for advanced bytecode manipulation

**Replace** - Completely substitutes method implementation

**Raw** - Provides direct Javassist CtBehavior access

## Fundamental Requirements

- Nested patch classes must be static
- Patch methods must be static
- Methods receive original method parameters plus instance reference (for non-static methods)

## @SpirePatch Annotation Parameters

| Parameter | Purpose |
|-----------|---------|
| `clz` | Target class containing method |
| `method` | Target method name |
| `paramtypez` | Parameter type array for disambiguation |
| `requiredModId` | Cross-mod dependency specification |
| `optional` | Suppresses errors if target unavailable |

## Patching Sequence

Patches apply in this order: **Insert → Instrument → Replace → Prefix → Postfix → Raw**

Within each category, patches load according to mod ordering.

## Insert Patch Locators

Insert patches support three positioning mechanisms:

### 1. Absolute Line Number
```java
@SpireInsertPatch(loc=42)
```

### 2. Relative to Method Start
```java
@SpireInsertPatch(rloc=5)  // 5 lines from method start
```

### 3. Custom Locator Class
```java
@SpireInsertPatch(locator=MyLocator.class)

public static class MyLocator extends SpireInsertLocator {
    public int[] Locate(CtBehavior ctBehavior) throws Exception {
        Matcher finalMatcher = new Matcher.MethodCallMatcher(
            AbstractDungeon.class, "getCurrRoom"
        );
        return LineFinder.findInOrder(ctBehavior, finalMatcher);
    }
}
```

The `LineFinder` API provides `findInOrder()` and `findAllInOrder()` methods for resilient pattern-matching approaches.

## Key Features

Prefix/Postfix/Insert patches support:
- [`@ByRef`](@ByRef.md) parameter binding
- [Private field capture](Private-Field-Captures.md)
- [`SpireReturn`](SpireReturn.md) early exit mechanism (Prefix/Insert only)

## Important Warnings

**Replace patches override any other patches applied to the same method** - use cautiously as this destructive approach prevents compatibility with other mods.

## Example: Simple Prefix Patch

```java
@SpirePatch(
    clz=AbstractPlayer.class,
    method="damage"
)
public class DamagePatch {
    public static void Prefix(AbstractPlayer __instance, DamageInfo info) {
        System.out.println("Player is about to take damage: " + info.output);
    }
}
```

## Example: Postfix with Return Modification

```java
@SpirePatch(
    clz=AbstractCard.class,
    method="calculateCardDamage"
)
public class CardDamagePatch {
    public static void Postfix(AbstractCard __instance, AbstractMonster mo) {
        __instance.damage *= 2;  // Double all card damage
    }
}
```

## See Also

- [SpirePatch2](SpirePatch2.md) - Improved annotation-based patching
- [Matcher](Matcher.md) - Locator positioning tools
- [PatchingException](PatchingException.md) - Common errors
