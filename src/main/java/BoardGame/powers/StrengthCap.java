package BoardGame.powers;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.StrengthPower;

@SpirePatch2(clz= StrengthPower.class,method="stackPower",paramtypez={int.class})
public class StrengthCap {
    @SpirePostfixPatch
    public static void Postfix(StrengthPower __instance, int stackAmount){
        //TODO: dungeon must be instanceof AbstractBGDungeon!!
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
