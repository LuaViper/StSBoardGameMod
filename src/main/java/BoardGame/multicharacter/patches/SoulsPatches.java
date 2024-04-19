package BoardGame.multicharacter.patches;

import BoardGame.cards.AbstractBGCard;
import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.defect.AnimateOrbAction;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

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
            if(__instance.card!=null && __instance.card instanceof AbstractBGCard){
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
