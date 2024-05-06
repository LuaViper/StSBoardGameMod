package BoardGame.powers;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class StrengthCap {
@SpirePatch2(clz= StrengthPower.class,method=SpirePatch.CONSTRUCTOR,paramtypez={AbstractCreature.class,int.class})
public static class Patch1 {
    @SpirePrefixPatch
    public static void Foo(StrengthPower __instance, AbstractCreature owner, @ByRef int[] amount){
        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
        if(__instance instanceof BGUncappedStrengthPower) return;
        if(amount[0]>8){
            amount[0]=8;
        }
        if(amount[0]<0){    //this is theoretically possible with Wreath of Flame + Flex effects
            amount[0]=0;
        }
    }
}

@SpirePatch2(clz= StrengthPower.class,method="stackPower",paramtypez={int.class})
public static class Patch2 {
    @SpirePostfixPatch
    public static void Foo(StrengthPower __instance, int stackAmount){
        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) return;
        if(__instance instanceof BGUncappedStrengthPower) return;
        if(__instance.amount>8){
            __instance.amount=8;
        }
        if(__instance.amount<0){    //this is theoretically possible with Wreath of Flame + Flex effects
            __instance.amount=0;
        }

    }
}
}