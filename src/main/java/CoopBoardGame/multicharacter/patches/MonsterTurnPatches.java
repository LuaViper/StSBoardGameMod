package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.MultiCharacter;
import CoopBoardGame.multicharacter.MultiCreature;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterQueueItem;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class MonsterTurnPatches {

    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class BeforeMonsterTurnPatch {

        @SpireInsertPatch(locator = Locator.class, localvars = {})
        public static void Foo(GameActionManager __instance) {
            int whichRow = MultiCreature.Field.currentRow.get(
                ((MonsterQueueItem) __instance.monsterQueue.get(0)).monster
            );
            AbstractPlayer p = AbstractDungeon.player;
            if (MultiCharacter.getSubcharacters().size() > whichRow) {
                p = MultiCharacter.getSubcharacters().get(whichRow);
            }
            ContextPatches.pushPlayerContext(p);
        }

        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch)
                throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.TypeCastMatcher(
                    MonsterQueueItem.class.getName()
                );
                return LineFinder.findInOrder(
                    ctMethodToPatch,
                    new ArrayList<Matcher>(),
                    finalMatcher
                );
            }
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class AfterMonsterTurnPatch {

        @SpireInsertPatch(
            rloc = 211, //between "m.applyTurnPowers();" and "}"
            localvars = {}
        )
        public static void Foo(GameActionManager __instance) {
            ContextPatches.popPlayerContext();
        }
    }
}
