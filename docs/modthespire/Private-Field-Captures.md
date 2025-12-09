# Private Field Captures

This feature allows patches to receive private field values as parameters.

## Compatibility
Private Field Captures works on Prefix, Insert, and Postfix patches.

## How It Works

To capture a private field, add a parameter to your patch method with the field name prepended by three underscores. For example, a field called `fieldName` becomes `___fieldName`.

## Code Example

```java
public static void Prefix(AbstractCard __instance, SpriteBatch sb,
    boolean ___renderTip)
{
    // usage here
}
```

## Insert Patch Parameter Order

For Insert patches specifically, the parameter sequence is:

1. Instance (if applicable)
2. Formal method parameters
3. Private Field Captures
4. Local variables

## Using with @ByRef

The `@ByRef` annotation works with Private Capture Fields, allowing you to modify private field values.

```java
public static void Prefix(AbstractCard __instance,
    @ByRef boolean[] ___renderTip)
{
    ___renderTip[0] = false; // Modifies the private field
}
```
