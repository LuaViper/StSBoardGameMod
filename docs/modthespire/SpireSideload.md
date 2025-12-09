# SpireSideload

The `@SpireSideload` annotation enables a mod to automatically load another mod without explicit user selection.

## Sideloading Another Mod

### Constraints
- Must be placed on the same class as `@SpireInitializer`
- Only functions if the target mod has been downloaded

### Functions for Checking Mod Status
```java
Loader.isModLoaded(modID)
Loader.isModSideloaded(modID)
Loader.isModLoadedOrSideloaded(modID)
```

### Code Example

```java
@SpireInitializer
@SpireSideload(
    modIDs = {
        "otherModID"
    }
)
public class ExampleMod
{
    public static void initialize()
    {
        // Main initialization code
    }
}
```

## Being Sideloaded

When a mod defines a `sideload` method in its `@SpireInitializer` class, that method executes when another mod sideloads it instead of the standard `initialize` method.

This allows selective functionality loading—for instance, the Bard mod loads only its "notes and melodies systems" when sideloaded rather than its character and cards.

### Code Example

```java
@SpireInitializer
public class SideloadedMod
{
    public static void initialize()
    {
        // Called if explicitly loaded by the player
        System.out.println("Full mod initialization");
        loadCharacter();
        loadCards();
        loadRelics();
    }

    public static void sideload()
    {
        // Called if another mod sideloads this mod
        System.out.println("Sideload initialization - limited features");
        loadSharedSystems();  // Only load what other mods need
    }
}
```

## Use Cases

### API Mods
Load library functionality without full mod features:
```java
public static void sideload() {
    // Only load utility classes, not content
    registerUtilityAPIs();
}
```

### Compatibility Layers
Enable cross-mod features selectively:
```java
public static void sideload() {
    // Load only compatibility hooks
    registerCompatibilityCallbacks();
}
```

### Optional Dependencies
Access another mod's features without requiring user interaction:
```java
@SpireSideload(modIDs = {"stslibrary"})
public class MyMod {
    public static void initialize() {
        if (Loader.isModLoadedOrSideloaded("stslibrary")) {
            // Use StSLibrary features
        }
    }
}
```
