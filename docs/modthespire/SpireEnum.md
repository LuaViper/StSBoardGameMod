# SpireEnum

The `@SpireEnum` annotation enables mods to add new values to existing enums in Slay The Spire by marking static fields with this annotation.

## Key Requirements
- The annotated field's type must match the enum being extended
- The field name becomes the new enum value name
- **A new enum value cannot be defined inside a class deriving from the class that defines the enum**

## Usage Example

Adding a custom player class:

```java
public class Foo {
    @SpireEnum
    public static AbstractPlayer.PlayerClass EXAMPLE_CHARACTER;
}
```

This creates a new enum value accessible via `Foo.EXAMPLE_CHARACTER` and appears in the enum's values array alongside built-in options like `IRONCLAD`, `THE_SILENT`, and `CROWBOT`.

## Practical Application

The example shows implementing the new enum value in a switch statement within an extending class:

```java
switch (playerClass) {
    case IRONCLAD:
        // Handle Ironclad
        break;
    case THE_SILENT:
        // Handle Silent
        break;
    case EXAMPLE_CHARACTER:
        // Handle custom character
        break;
}
```

Custom enum values integrate seamlessly with existing game logic while maintaining type safety.
