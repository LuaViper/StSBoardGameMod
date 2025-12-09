# PatchingException

## Exceptions During Patching

When ModTheSpire is reading your mod's patches, if it encounters a badly formed patch it will throw a `PatchingException` with information about what is wrong with the patch.

## Possible Causes

* You defined an `Insert` method and did not provide a `@SpireInsertPatch` annotation for it
* You defined a `@SpireInsertPatch` without specifying a line number or a locator
* You defined a `Locator` that didn't find any lines
