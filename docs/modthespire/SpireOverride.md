# SpireOverride

The `SpireOverride` annotation enables overriding private methods from superclasses—something normally impossible in Java.

## Purpose
For overriding private methods from a superclass.

## Standard Java Limitation
Normally, attempting to override a private method results in an error, while public and protected method overrides work correctly.

## Solution with SpireOverride
The annotation allows developers to override private parent methods by changing the access modifier to protected or public in the child class.

## Important Constraint
**Your SpireOverride method must be either `protected` or public. If you make it `private` the SpireOverride will not work.**

## Calling Parent Implementation
For accessing the original parent method, developers must use `SpireSuper.call()` rather than standard Java's `super` keyword.

## Example

```java
public class Parent {
    private void privateMethod() {
        System.out.println("Parent private method");
    }
}

public class Child extends Parent {
    @SpireOverride
    protected void privateMethod() {
        // Call parent implementation
        SpireSuper.call();

        // Add custom behavior
        System.out.println("Child override");
    }
}
```

---

*Last updated: March 23, 2023 (3 revisions)*
