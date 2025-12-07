package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.DrawCardMultiAction;
import CoopBoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DrawCardPatch {

    @SpirePatch2(clz = DrawCardAction.class, method = "update")
    public static class DrawCardPatch1 {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(DrawCardAction __instance) {
            if (AbstractDungeon.player instanceof MultiCharacter) {
                if (ActionPatches.Field.owner.get(__instance) == null) {
                    CoopBoardGame.CoopBoardGame.logger.warn(
                        "called DrawCardAction with BGMultiCharacter and no owner -- subbing in DrawCardMultiAction whether we want it or not"
                    );
                    AbstractDungeon.actionManager.addToTop(new DrawCardMultiAction());
                    __instance.isDone = true;
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
    //    @SpirePatch2(clz = DrawCardAction.class, method = "update")
    //    public static class DrawCardPatch2{
    //        @SpirePostfixPatch
    //        public static SpireReturn<Void> Prefix(DrawCardAction __instance) {
    //            if(AbstractDungeon.player instanceof BGMultiCharacter){
    //                if(ActionPatches.Field.owner.get(__instance)==null) {
    //                    return SpireReturn.Continue();
    //                }
    //            }
    //            return SpireReturn.Continue();
    //        }
    //    }
}
