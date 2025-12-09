# SpireConfig

**SpireConfig** is a utility that enables mods to persistently store user preferences using Java's `Properties` class. Configuration files are stored outside the game directory in platform-specific user preference locations.

## Storage Locations
- **Windows:** `%LOCALAPPDATA%\ModTheSpire\`
- **Linux:** `~/.config/ModTheSpire/`
- **Mac:** `~/Library/Preferences/ModTheSpire/`

## Constructor Parameters
The `SpireConfig` constructor accepts two required parameters:
1. **Mod Name** - Creates a subdirectory where config files are stored
2. **Config Name** - The configuration file's name (without extension)

## Basic Usage Example
```java
SpireConfig config = new SpireConfig("MyMod", "config");
config.setInt("foo", 42);
config.save();
```

## Using Default Values
Developers can pass a `Properties` object to provide initial defaults:

```java
Properties defaults = new Properties();
defaults.setProperty("bar", "asdf");
SpireConfig config = new SpireConfig("MyMod", "config", defaults);
System.out.println(config.getString("bar")); // Prints "asdf"
```

Default values are retrieved when keys haven't been explicitly set but aren't persisted to disk automatically.

---

*Last edited by Alchyr on December 2, 2020 (3 revisions)*
