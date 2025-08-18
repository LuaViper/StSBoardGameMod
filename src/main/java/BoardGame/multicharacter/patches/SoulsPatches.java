package BoardGame.multicharacter.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.Soul;

//TODO: we've hardcoded checks for AbstractBGCard -- make it compatible with AbstractCard too
public class SoulsPatches {
    @SpirePatch(
            clz=Soul.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<Boolean> contextActive = new SpireField<>(()->false);
    }

    @SpirePatch2(clz = Soul.class, method = "update")
    public static class SoulContextBefore {
        @SpirePrefixPatch
        public static void Foo(Soul __instance) {
            //if(__instance.card!=null && __instance.card instanceof AbstractBGCard){
            if(__instance.card!=null){
                ContextPatches.pushPlayerContext(CardPatches.Field.owner.get(__instance.card));
                Field.contextActive.set(__instance,true);
            }
        }
    }

    @SpirePatch2(clz = Soul.class, method = "update")
    public static class SoulContextAfter {
        @SpirePostfixPatch
        public static void Foo(Soul __instance) {
            if(Field.contextActive.get(__instance)){
                ContextPatches.popPlayerContext();
                Field.contextActive.set(__instance,false);
            }
        }
    }

}
