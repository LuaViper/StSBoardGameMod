# SpireInitializer

The `@SpireInitializer` annotation enables mods to designate classes for execution after patching completes but before the game launches. ModTheSpire identifies methods named `initialize` within annotated classes and invokes them prior to game initialization.

## Requirements

The `initialize` method must satisfy these criteria:
- **Access modifier:** public static
- **Parameters:** none (takes no arguments)

## Code Example

```java
@SpireInitializer
public class ExampleMod
{
    public static void initialize()
    {
        // Mod initialization code here
        // Subscribe to BaseMod events, register content, etc.
    }
}
```

## Common Use Cases

The initialize method is typically used for:
- Subscribing to BaseMod events
- Registering custom cards, relics, characters
- Setting up configuration files
- Loading assets
- Initializing patches that require runtime setup

---

*Last edited on August 11, 2018, with 2 revisions*
