# Matcher

When using Locator patches, you need to use the `Matcher` class to specify where in the game you want a patch to be inserted.

## Matcher Types

### TypeCastMatcher
Identifies type cast operations like `(Dog) myAnimal`.

**Parameters:** String for the target class name

### ConstructorCallMatcher
Detects constructor invocations (`super()` or `this()`).

**Parameters:** Class type and optional method name

### FieldAccessMatcher
Locates field access expressions such as `myObject.myField`.

**Parameters:** Class or class name plus field name

### CatchClauseMatcher
Matches exception handlers.

**Parameters:** Exception type and a boolean for catch versus finally clauses

### InstanceOfMatcher
Finds instanceof checks.

**Parameters:** Class type or type name

### MethodCallMatcher
Detects method invocations.

**Parameters:** Class/class name and method name

### NewArrayMatcher
Identifies array creation expressions like `new int[3]`.

### NewExprMatcher
Locates object instantiation statements with `new` keyword.

## Usage Notes

Each Matcher provides flexible constructor options using either Class types or string class names for broader applicability in patching scenarios.
