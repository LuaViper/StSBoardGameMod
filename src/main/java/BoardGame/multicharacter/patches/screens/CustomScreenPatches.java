package BoardGame.multicharacter.patches.screens;

import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.patches.ContextPatches;
import BoardGame.screen.OrbSelectScreen;
import basemod.abstracts.CustomScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CustomScreenPatches {

    @SpirePatch(
            clz= CustomScreen.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractPlayer> owner = new SpireField<>(() -> null);
//        public static SpireField<AbstractCreature> rowTarget = new SpireField<>(() -> null);
//        public static SpireField<Boolean> alreadyCalledTargetSelectScreen = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = CustomScreen.class, method = "open")
    public static class SetContextOnOpen {
        @SpirePostfixPatch
        public static void Foo(CustomScreen __instance) {
            if(CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER)return;
            if(ContextPatches.playerContextHistory.isEmpty())return;
            Field.owner.set(__instance, AbstractDungeon.player);
        }
    }

    @SpirePatch2(clz = OrbSelectScreen.class, method = "update")
    public static class SetContextOnUpdate {
        @SpirePrefixPatch
        public static void Foo(OrbSelectScreen __instance) {
            AbstractPlayer context = Field.owner.get(__instance);
            if (context != null) {
                ContextPatches.pushPlayerContext(context);
            }
        }
        @SpirePostfixPatch
        public static void Foo2(OrbSelectScreen __instance) {
            AbstractPlayer context = Field.owner.get(__instance);
            if(context!=null){
                ContextPatches.popPlayerContext();
            }
        }
    }

}
