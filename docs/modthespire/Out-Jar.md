# Out Jar

## For Debugging

ModTheSpire already includes a `debug` flag that will indicate the line numbers at which patches are applied in the game's log but even though this technically is enough information to make sure patches are being applied in the right locations, the `--out-jar` flag can make checking patch locations even easier.

When ModTheSpire is run with the `--out-jar` flag like this:

```bash
java -jar ModTheSpire.jar --out-jar
```

Instead of launching the game, ModTheSpire will patch the game and then simply dump the patched game out to `desktop-1.0-patched.jar` which you can then use `JD-GUI` and `Luyten` to decompile and check the patched files to see if patches were inserted in sensible locations that actually work.

## Summary

This debugging feature enables developers to verify patch placements using decompilation tools like JD-GUI and Luyten, providing greater visibility into modification locations than the standard debug output alone.
