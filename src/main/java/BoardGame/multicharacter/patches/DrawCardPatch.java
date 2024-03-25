package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import BoardGame.multicharacter.DrawCardMultiAction;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DrawCardPatch {
    @SpirePatch2(clz = DrawCardAction.class, method = "update")
    public static class DrawCardPatch1{
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(DrawCardAction __instance) {
            if(AbstractDungeon.player instanceof BGMultiCharacter){
                if(ActionPatches.Field.owner.get(__instance)==null) {
                    BoardGame.BoardGame.logger.warn("called DrawCardAction with BGMultiCharacter and no owner -- subbing in DrawCardMultiAction whether we want it or not");
                    AbstractDungeon.actionManager.addToTop(new DrawCardMultiAction());
                    __instance.isDone = true;
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch2(clz = DrawCardAction.class, method = "update")
    public static class DrawCardPatch2{
        @SpirePostfixPatch
        public static SpireReturn<Void> Prefix(DrawCardAction __instance) {
            if(AbstractDungeon.player instanceof BGMultiCharacter){
                if(ActionPatches.Field.owner.get(__instance)==null) {
                    return SpireReturn.Continue();
                }
            }
            return SpireReturn.Continue();
        }
    }
}
