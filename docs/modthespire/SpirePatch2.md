# SpirePatch2

SpirePatch2 is an improved annotation-based patching system for the ModTheSpire framework. It builds upon the SpirePatch system with enhanced parameter handling.

## Key Differences from SpirePatch

### 1. Annotation Change
Uses `@SpirePatch2` instead of `@SpirePatch`

### 2. Named Parameters
Patch methods are passed arguments of the original method **by name** rather than receiving all arguments sequentially.

### 3. Flexible Parameter Handling
Developers can exclude unused parameters and arrange included parameters in any order.

### 4. Special Parameters

- `__instance`: Receives the object instance (for non-static methods)
- `__result`: Receives the original return value (Postfix only)
- `__args`: Receives all original parameters as an `Object[]` array

## Main Use Cases

### Parameter Exclusion
Include only the parameters you need from the original method.

```java
// Original method: void foo(int a, String b, float c)

@SpirePatch2(clz=MyClass.class, method="foo")
public static class FooPatch {
    public static void Prefix(String b) {
        // Only include parameter 'b', ignore 'a' and 'c'
    }
}
```

### Parameter Ordering
Parameters can appear in any sequence within your patch method.

```java
@SpirePatch2(clz=MyClass.class, method="foo")
public static class FooPatch {
    public static void Prefix(float c, int a) {
        // Parameters in different order than original
    }
}
```

### Private Field Access
Integrates with [Private Field Captures](Private-Field-Captures.md) for accessing private variables.

```java
@SpirePatch2(clz=MyClass.class, method="foo")
public static class FooPatch {
    public static void Prefix(String b, boolean ___privateField) {
        // Access parameter 'b' and private field 'privateField'
    }
}
```

### Local Variable Handling
Insert patches can access local variables by name through matching parameter names.

## Complete Example

```java
public class Foobar {
    private boolean myPrivateField = true;

    public int foo(String str, int num, float val) {
        int localVar = num * 2;
        // ...
        return localVar;
    }
}

@SpirePatch2(clz=Foobar.class, method="foo")
public static class FoobarPatch {
    // Postfix with selective parameters
    public static void Postfix(
        Foobar __instance,
        int __result,
        String str,
        boolean ___myPrivateField
    ) {
        System.out.println("Method returned: " + __result);
        System.out.println("String param was: " + str);
        System.out.println("Private field: " + ___myPrivateField);
    }
}
```

## Advantages

- **Cleaner Code**: Only include parameters you actually use
- **More Flexible**: Arrange parameters in the most logical order for your patch
- **Easier Maintenance**: Changes to method signatures don't break your patch if you don't use those parameters
- **Better Readability**: Parameter names make it clear what values you're working with

## See Also

- [SpirePatch](SpirePatch.md) - Original patching system
- [Private Field Captures](Private-Field-Captures.md) - Accessing private fields
- [@ByRef](@ByRef.md) - Pass-by-reference parameters
