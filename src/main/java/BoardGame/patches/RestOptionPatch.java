package BoardGame.patches;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.BGRegalPillow;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

//TODO: change "Heal for 30% of your max HP (3)." text to "Heal 3 HP."
public class RestOptionPatch {
    @SpirePatch2(clz= RestOption.class, method= SpirePatch.CONSTRUCTOR,
            paramtypez={boolean.class})
    public static class RestOption3HPPatch{
        @SpireInsertPatch(
                locator= RestOptionPatch.RestOption3HPPatch.Locator.class,
                localvars={}
        )
        public static void Insert(@ByRef int[] ___healAmt) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                ___healAmt[0]=3;
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class,"isEndless");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    //TODO: make sure this patch is applied before Regal Pillow patch
    @SpirePatch2(clz= CampfireSleepEffect.class, method=SpirePatch.CONSTRUCTOR,
            paramtypez={})
    public static class CampfireSleepEffectPillowPatch{
        @SpirePostfixPatch public static void Postfix(@ByRef int[] ___healAmount) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                ___healAmount[0] = 3;
            }
            if (AbstractDungeon.player.hasRelic("BGRegal Pillow")) {
                ___healAmount[0] += 3;
            }
        }
    }

}
