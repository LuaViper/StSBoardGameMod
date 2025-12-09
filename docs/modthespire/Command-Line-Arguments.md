# Command Line Arguments

ModTheSpire supports several command-line arguments for launching the application.

## Available Flags

### `--out-jar`
References the [Out Jar](Out-Jar.md) documentation for building output JAR files.

### `--debug`
Activates debug mode, which is also toggleable through the launcher interface.

### `--profile <name>`
Loads a specific mod list by name.

### `--skip-launcher`
Bypasses the launcher UI and starts the game immediately with the previously selected mod configuration. When combined with `--profile`, it launches with that profile's mods instead.

### `--mods <modIDs>`
Starts the game directly with specified mods, accepting comma-separated mod IDs and requiring all dependencies.

**Example:**
```bash
java -jar ModTheSpire.jar --mods basemod,stslib,bard
```

### `--skip-intro`
Removes the game's opening splash screen, proceeding directly to the main menu after loading.

### `--close-when-finished`
Automatically closes ModTheSpire upon completion, useful with `--out-jar` for automated builds.

### `--imgui`
Activates LWJGL3 and Dear ImGui rendering mode.

## Usage

These flags can be passed when launching via command line or configured as program arguments in an IDE.

**Example:**
```bash
java -jar ModTheSpire.jar --out-jar --close-when-finished
```
