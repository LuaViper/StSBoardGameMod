package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ActionConstructorPatch {
    @SpirePatch2(clz = AbstractGameAction.class,method = SpirePatch.CONSTRUCTOR)
    public static class Patch1 {
        @SpirePrefixPatch
        public static void Prefix(AbstractGameAction __instance) {
            ActionPatches.Field.rowTarget.set(__instance,ContextPatches.currentTargetContext);
            if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
            if(ContextPatches.playerContextHistory.isEmpty())return;
            ActionPatches.Field.owner.set(__instance, AbstractDungeon.player);
        }
    }
}
