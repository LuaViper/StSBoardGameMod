package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.CoopBoardGame;
import CoopBoardGame.multicharacter.NullMonster;
import CoopBoardGame.patches.Ascension;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import java.util.ArrayDeque;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class OnGameResetPatch {

    @SpirePatch2(clz = CardCrawlGame.class, method = "updateFade")
    public static class Patch {

        @SpireInsertPatch(locator = Patch.Locator.class, localvars = {})
        public static void Foo() {
            ContextPatches.originalBGMultiCharacter = null;
            ContextPatches.playerContextHistory = new ArrayDeque<>();
            ContextPatches.targetContextHistory = new ArrayDeque<>();
            ContextPatches.currentTargetContext = new NullMonster();
            CoopBoardGame.alreadyShowedMaxPoisonWarning = false;
            CoopBoardGame.alreadyShowedMaxMiraclesWarning = false;
            Ascension.combineUnlockedAscensions();
        }

        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch)
                throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.NewExprMatcher(DungeonTransitionScreen.class);
                return LineFinder.findInOrder(
                    ctMethodToPatch,
                    new ArrayList<Matcher>(),
                    finalMatcher
                );
            }
        }
    }
}
