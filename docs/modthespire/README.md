# ModTheSpire Wiki

> This documentation is sourced from the [ModTheSpire Wiki](https://github.com/kiooeht/ModTheSpire/wiki) on GitHub.

## Overview

ModTheSpire is a tool to load external mods for Slay the Spire without modifying the base game files, along with allowing mods the ability to patch their own code into the game's code.

## Playing Mods

The installation process involves:

1. Downloading the latest ModTheSpire release
2. Placing `ModTheSpire.jar` in the Slay the Spire installation directory alongside `desktop-1.0.jar`
3. Adding the appropriate helper script (MTS.cmd for Windows, MTS.sh for Linux)
4. Creating a `mods` directory for mod files
5. Running ModTheSpire through the helper script or directly with Java 8

### Platform-Specific Notes

On Mac systems, the installation path varies depending on the game's location—either through Applications or Steam's common directory. Users should locate `desktop-1.0.jar` first and install ModTheSpire in that same location.

Alternatively, users can rename files to run ModTheSpire through Steam or the SlayTheSpire.exe launcher.

## Writing Mods

Two approaches exist:

**Replacing Game Files**: Simpler but incompatible with other mods. ModTheSpire prioritizes mod files over base game files when names match.

**Patching**: The preferred method allowing code injection directly into game code. This enables multiple mods to work simultaneously.

## Documentation Pages

### Core Annotations
- [SpirePatch](SpirePatch.md) - Main patching system
- [SpirePatch2](SpirePatch2.md) - Improved annotation-based patching
- [SpireInitializer](SpireInitializer.md) - Mod initialization
- [SpireEnum](SpireEnum.md) - Adding enum values
- [SpireField](SpireField.md) - Adding fields to classes
- [SpireOverride](SpireOverride.md) - Overriding private methods
- [SpireSideload](SpireSideload.md) - Automatic mod loading
- [SpireReturn](SpireReturn.md) - Early method returns

### Utilities & Features
- [@ByRef](@ByRef.md) - Pass-by-reference parameters
- [SpireConfig](SpireConfig.md) - Configuration storage
- [Private Field Captures](Private-Field-Captures.md) - Accessing private fields
- [Matcher](Matcher.md) - Locator patch positioning

### Configuration & Tools
- [ModInfo](ModInfo.md) - ModTheSpire.json format
- [Command Line Arguments](Command-Line-Arguments.md) - CLI flags
- [Out Jar](Out-Jar.md) - Debugging tool

### Reference
- [PatchingException](PatchingException.md) - Common errors
- [Troubleshooting](Troubleshooting.md) - Common issues
- [List of Known Mods](List-of-Known-Mods.md) - Mod catalog (deprecated)
