package BoardGame.ui;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.monsters.DieControlledMoves;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.helpers.GameTips;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class BGGameTips extends GameTips {

    public static final CharacterStrings bgtutorialStrings = CardCrawlGame.languagePack.getCharacterString("BoardGame:Random Tips");
    @SpirePatch(clz= GameTips.class, method=SpirePatch.CLASS)
    public static class ExtraGameTipsPatch{
        public static SpireField<ArrayList<String>> bgtips = new SpireField<>(() -> new ArrayList<>());
    }
    @SpirePatch2(clz=GameTips.class,method="initialize",paramtypez={})
    public static class initializePatch {
        @SpirePostfixPatch
        public static void Postfix(GameTips __instance) {
            //final Logger logger = LogManager.getLogger(BGGameTips.class.getName());
            //logger.info("Tips?: "+__instance+" "+bgtutorialStrings+" "+ExtraGameTipsPatch.bgtips.get(__instance));
            Collections.addAll(ExtraGameTipsPatch.bgtips.get(__instance), bgtutorialStrings.TEXT);
            Collections.shuffle(ExtraGameTipsPatch.bgtips.get(__instance));
        }
    }

    @SpirePatch2(clz=GameTips.class,method="getTip",paramtypez={})
    public static class getTipPatch {
        @SpirePrefixPatch
        public static SpireReturn<String> Postfix(GameTips __instance) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                String retVal = ExtraGameTipsPatch.bgtips.get(__instance).remove(MathUtils.random(ExtraGameTipsPatch.bgtips.get(__instance).size() - 1));
                if (ExtraGameTipsPatch.bgtips.get(__instance).isEmpty()) {
                    __instance.initialize();
                }

                return SpireReturn.Return(retVal);
            }
            return SpireReturn.Continue();
        }
    }

}
