# SpireReturn

SpireReturn is a mechanism that allows patches to terminate method execution prematurely by returning a value.

## Key Features
- **Compatibility**: Works with Prefix and Insert patch types
- **Return Types**: Supports any return type matching the patched method

## Basic Usage Examples

### Returning an Object
```java
public static SpireReturn<AbstractRelic> Prefix(String key) {
    return SpireReturn.Return(new BlueCandle());
}
```

### Void Method Return
```java
public static SpireReturn<Void> Insert() {
    return SpireReturn.Return();
}
```

### Continuing Normally
```java
public static SpireReturn<AbstractRelic> Prefix(String key) {
    if (key.equals(BurningBlood.ID)) {
        return SpireReturn.Continue();
    } else {
        return SpireReturn.Return(new BlueCandle());
    }
}
```

## Primitive Type Handling

When patching methods returning primitives, use wrapper classes:

### Boolean Return
```java
public static SpireReturn<Boolean> Insert() {
    return SpireReturn.Return(false);
}
```

### Integer Return
```java
public static SpireReturn<Integer> Insert() {
    return SpireReturn.Return(9);
}
```

### Float Return
```java
public static SpireReturn<Float> Prefix(float value) {
    if (value > 100.0f) {
        return SpireReturn.Return(100.0f);  // Cap at 100
    }
    return SpireReturn.Continue();
}
```

## Key Takeaway
SpireReturn provides "early exit" functionality through `Return()` for immediate termination or `Continue()` to proceed normally.

## When to Use

Use SpireReturn when you need to:
- Override method behavior entirely under certain conditions
- Prevent certain code paths from executing
- Replace return values conditionally
- Implement feature toggles or compatibility patches

## Example: Preventing Method Execution

```java
@SpirePatch(
    clz=AbstractPlayer.class,
    method="damage"
)
public static class DamageImmunityPatch {
    public static SpireReturn<Void> Prefix(AbstractPlayer __instance, DamageInfo info) {
        if (__instance.hasPower("ImmunityPower")) {
            // Don't take damage if immune
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }
}
```
