package BoardGame.multicharacter.patches.screens;

import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.patches.ContextPatches;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;

public class HandCardSelectScreenPatches {
    //TODO: whenever releaseCard is called, call it for all subcharacters
    //TODO: we will eventually need to patch grid select screen as well
    @SpirePatch(
            clz=HandCardSelectScreen.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractPlayer> owner = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = HandCardSelectScreen.class, method = "open",
    paramtypez={String.class, int.class, boolean.class, boolean.class, boolean.class, boolean.class, boolean.class})
    public static class SaveContextOnOpen1 {
        @SpirePostfixPatch
        public static void Foo(HandCardSelectScreen __instance) {
            if(CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER)return;
            if(ContextPatches.playerContextHistory.isEmpty())return;
            Field.owner.set(__instance, AbstractDungeon.player);
        }
    }
    @SpirePatch2(clz = HandCardSelectScreen.class, method = "open",
            paramtypez={String.class, int.class, boolean.class, boolean.class})
    public static class SaveContextOnOpen2 {
        @SpirePostfixPatch
        public static void Foo(HandCardSelectScreen __instance) {
            if(CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER)return;
            if(ContextPatches.playerContextHistory.isEmpty())return;
            Field.owner.set(__instance, AbstractDungeon.player);
        }
    }

    @SpirePatch2(clz = HandCardSelectScreen.class, method = "update")
    public static class SetContextOnUpdate {
        @SpirePrefixPatch
        public static void Foo(HandCardSelectScreen __instance) {
            AbstractPlayer context = Field.owner.get(__instance);
            if (context != null) {
                ContextPatches.pushPlayerContext(context);
            }
        }
        @SpirePostfixPatch
        public static void Foo2(HandCardSelectScreen __instance) {
            AbstractPlayer context = Field.owner.get(__instance);
            if(context!=null){
                ContextPatches.popPlayerContext();
            }
        }
    }

    @SpirePatch2(clz = HandCardSelectScreen.class, method = "render")
    public static class SetContextOnRender {
        @SpirePrefixPatch
        public static void Foo(HandCardSelectScreen __instance) {
            AbstractPlayer context = Field.owner.get(__instance);
            if (context != null) {
                ContextPatches.pushPlayerContext(context);
            }
        }
        @SpirePostfixPatch
        public static void Foo2(HandCardSelectScreen __instance) {
            AbstractPlayer context = Field.owner.get(__instance);
            if(context!=null){
                ContextPatches.popPlayerContext();
            }
        }
    }

}
