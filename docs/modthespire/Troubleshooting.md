# Troubleshooting

*Last edited by Kiooeht on Nov 16, 2018 · 5 revisions*

## Table of Contents

- [Playing With Mods](#playing-with-mods)
- [Developing Mods](#developing-mods)

---

## Playing With Mods

### Are you on the beta branch of Slay the Spire?

**Switch out of beta mode**

Both ModTheSpire and BaseMod do not officially support beta. Since almost all mods rely on one or both of these mods, there is no guarantee that the mod(s) you are playing with will work on beta.

### Do you have the latest version of ModTheSpire and BaseMod?

The latest releases can be found here:
- [ModTheSpire](https://github.com/kiooeht/ModTheSpire/releases/latest)
- [BaseMod](https://github.com/daviscook477/BaseMod/releases/latest)

### Do you have the latest version of Slay the Spire?

Update through Steam.

### Is it Thursday or Friday (Or sometimes Saturday)?

Check if all of the mods you are playing with (especially BaseMod) have been updated for the latest patch of Slay the Spire.

The developers of Slay the Spire release a weekly patch every Thursday. The patch often breaks something in BaseMod, ModTheSpire, or the mod(s) you are trying to play with.

**Are you a developer?** Consider creating a pull request to make the mod compatible with the latest patch.

### Are your folders set up correctly for mods?

Your folder setup should look something like this:

**Windows/Linux:**
```
SlayTheSpire/
    mods/
        Mod1.jar
        Mod2.jar
    desktop-1.0.jar
    ModTheSpire.jar
    MTS.cmd (if on PC)
    MTS.sh (if on Linux)
    ...
```

**Mac:**
```
SlayTheSpire/
    SlayTheSpire.app/
        Contents/
            Info.plist
            MacOS/
                run.sh
            Resources/
                mods/
                    mod1.jar
                    mod2.jar
                desktop-1.0.jar
                ModTheSpire.jar
                MTS.sh
```

### Are you starting the game with the script?

Start Slay the Spire with `MTS.cmd` if you are playing on PC and `MTS.sh` if you are playing on Linux.

### NoSuchMethodError

If you see the following error, you are most likely running ModTheSpire with Java 9 or 10. **ModTheSpire does not work on Java 9 or 10 and requires Java 8.** If you start ModTheSpire using the script (see above) you will automatically be using the install of Java 8 that Slay the Spire ships with.

```
Exception in thread "Thread-2" java.lang.NoSuchMethodError: sun.reflect.ReflectionFactory.newConstructorAccessor(Ljava/lang/reflect/Constructor;)Lsun/reflect/ConstructorAccessor;
at com.evacipated.cardcrawl.modthespire.EnumBusterReflect.findConstructorAccessor(EnumBusterReflect.java:211)
at com.evacipated.cardcrawl.modthespire.EnumBusterReflect.make(EnumBusterReflect.java:70)
at com.evacipated.cardcrawl.modthespire.EnumBusterReflect.make(EnumBusterReflect.java:49)
at com.evacipated.cardcrawl.modthespire.Patcher.patchEnums(Patcher.java:125)
at com.evacipated.cardcrawl.modthespire.Loader.runMods(Loader.java:237)
at com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow.lambda$null$1(ModSelectWindow.java:199)
at java.base/java.lang.Thread.run(Unknown Source)
```

### Crash after clicking the Play button

You may encounter this crash after clicking the Play button with several mods selected:

```
Initializing mods...
java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
```

**Solution:** Make sure BaseMod is the first mod in the modlist for ModTheSpire.

### OpenGL is not supported by the video driver

If you encounter this crash after clicking the Play button:

```
Exception in thread "LWJGL Application" com.badlogic.gdx.utils.GdxRuntimeException: OpenGL is not supported by the video driver.
    at com.badlogic.gdx.backends.lwjgl.LwjglGraphics.createDisplayPixelFormat(LwjglGraphics.java:322)
    at com.badlogic.gdx.backends.lwjgl.LwjglGraphics.setupDisplay(LwjglGraphics.java:216)
    at com.badlogic.gdx.backends.lwjgl.LwjglApplication.mainLoop(LwjglApplication.java:144)
    at com.badlogic.gdx.backends.lwjgl.LwjglApplication$1.run(LwjglApplication.java:126)
Caused by: org.lwjgl.LWJGLException: Pixel format not accelerated
    at org.lwjgl.opengl.WindowsPeerInfo.nChoosePixelFormat(Native Method)
    at org.lwjgl.opengl.WindowsPeerInfo.choosePixelFormat(WindowsPeerInfo.java:52)
    at org.lwjgl.opengl.WindowsDisplay.createWindow(WindowsDisplay.java:253)
    at org.lwjgl.opengl.Display.createWindow(Display.java:306)
    at org.lwjgl.opengl.Display.create(Display.java:848)
    at org.lwjgl.opengl.Display.create(Display.java:757)
    at com.badlogic.gdx.backends.lwjgl.LwjglGraphics.createDisplayPixelFormat(LwjglGraphics.java:314)
    ... 3 more
```

See [this issue](https://github.com/kiooeht/ModTheSpire/issues/50#issuecomment-439233888) for a solution.

---

## Developing Mods

This is a troubleshooting guide for common issues when developing your first mod. If you are simply trying to play Slay the Spire with mods, read the "Playing With Mods" section above.

If you are looking for a tutorial on writing your first mod, check out:
- [STSModSetup Tutorial](https://github.com/Kobting/STSModSetup)
- [BaseMod Getting Started Guide](https://github.com/daviscook477/BaseMod/wiki/Getting-Started-\(For-Modders\))

**All of the steps in the "Playing With Mods" section apply as well.**

### Did you package your mod into a `.jar` file?

You should be packaging your mod into a `.jar` file and placing that `.jar` file into the mods folder where you play Slay the Spire.

You can package your mod through the command line with:
```bash
mvn package
```

Or through built-in tools in IntelliJ or Eclipse.

### Are you trying to recompile the decompiled source code?

**Don't.**

Follow the instructions on how to package your mod with Maven in [the tutorial](https://github.com/Kobting/STSModSetup).

### Did you follow the troubleshooting steps for playing with mods?

Those start at the top of the page. All of those steps apply as well.
