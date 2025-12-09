# @ByRef

**@ByRef** enables parameters in patch methods to be passed by-reference, wrapping them in single-element arrays.

## Compatibility
Works with Prefix, Insert, and Postfix patches.

## Basic Example
```java
// Parameter i is of type int, when using @ByRef, it is passed as int[]
public static void Prefix(@ByRef int[] i, float f)
{
	i[0] = 14; // i will be changed outside this method
	f = 3.14;  // f will NOT be changed outside this method
}
```

## Advanced Usage with Objects
When using ByRef on a parameter of type Object that was not originally of type Object, you must use the type parameter to specify the original type.

Syntax allows two approaches:

```java
@SpireInsertPatch(loc=14, localvars={"p"})
// With fully qualified name:
public static void Insert(@ByRef(type="com.megacrit.cardcrawl.characters.AbstractPlayer") Object[] p) { ... }

// Shorthand (omitting com.megacrit.cardcrawl prefix):
public static void Insert(@ByRef(type="characters.AbstractPlayer") Object[] p) { ... }
```

---

*Last edited: February 6, 2021 | Revisions: 4*
