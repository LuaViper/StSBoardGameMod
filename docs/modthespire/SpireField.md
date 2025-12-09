# SpireField

SpireField is a mechanism that allows mod developers to dynamically add new fields to existing game classes.

## Core Functionality

The implementation involves creating a SpirePatch annotation targeting `SpirePatch.CLASS`:

```java
@SpirePatch(
    clz=AbstractCard.class,
    method=SpirePatch.CLASS
)
public class ExampleField {
    public static SpireField<String> example =
        new SpireField<>(() -> "default value");
}
```

## Usage Pattern

Field values are accessed through getter and setter operations:

```java
AbstractCard card = ...;
String str = ExampleField.example.get(card);
ExampleField.example.set(card, "foo");
```

## Key Constraint

Primitive types (int, float, boolean, etc.) are not supported in Java generics. You must use wrapper types like Integer, Float, and Boolean when creating SpireField instances for primitive values.

### Example with Primitives

```java
@SpirePatch(
    clz=AbstractCard.class,
    method=SpirePatch.CLASS
)
public class ExampleIntField {
    // Use Integer, not int
    public static SpireField<Integer> count =
        new SpireField<>(() -> 0);
}
```

## Navigation Context

This page is part of the ModTheSpire wiki documentation, positioned as a subpage under the SpirePatch section alongside related patching utilities.
