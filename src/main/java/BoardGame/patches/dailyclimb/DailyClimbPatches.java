package BoardGame.patches.dailyclimb;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.dungeons.BGExordium;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DailyClimbPatches {

    @SpirePatch2(clz = CharacterManager.class, method = "getRandomCharacter",
            paramtypez={Random.class})
    public static class RandomCharacterPatch {
        @SpirePostfixPatch
        public static AbstractPlayer Foo(AbstractPlayer __result, Random rng) {
            if(__result instanceof AbstractBGPlayer){
                ArrayList<AbstractPlayer> masterCharacterList = ReflectionHacks.getPrivateStatic(CharacterManager.class,"masterCharacterList");
                int index = rng.random(masterCharacterList.size() - 2);
                AbstractPlayer p = masterCharacterList.get(index);
                if(p instanceof AbstractBGPlayer){
                    if(index>0) {
                        p = masterCharacterList.get(index - 1);
                    }else{
                        p = masterCharacterList.get(index + 1);
                    }
                }
                return p;
            }
            return __result;
        }

    }

}
