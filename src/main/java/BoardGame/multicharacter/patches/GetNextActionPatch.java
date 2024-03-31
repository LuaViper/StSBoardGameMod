package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GetNextActionPatch {
    public static void before(AbstractCard c){
        //REMINDER: copied cards (from Foreign Influence and Doppelganger) must manually set owner before playing
        if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
        if(ContextPatches.originalBGMultiCharacter==null)ContextPatches.originalBGMultiCharacter=AbstractDungeon.player;
        //this patch takes place before the null check, so check again here
        if(c!=null) {
            ContextPatches.pushContext(CardPatches.Field.owner.get(c));
        }else{
            ContextPatches.pushContext(null);
        }

    }
    public static void after(){
        if(CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)return;
        ContextPatches.popContext();
    }

    @SpirePatch2(clz=GameActionManager.class,method="getNextAction",paramtypez={})
    public static class Patch1 {
        @SpireInsertPatch  (
                locator = GetNextActionPatch.Patch1.Locator.class,
                localvars = {}
        )
        public static void Insert(GameActionManager __instance) {
            before(((CardQueueItem)__instance.cardQueue.get(0)).card);
        }
        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardQueueItem.class,"card");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
//    @SpirePatch2(clz=GameActionManager.class,method="getNextAction",paramtypez={})
//    public static class Patch2 {
//        @SpireInsertPatch(
//                locator = Patch2.Locator.class,
//                localvars = {}
//        )
//        public static void Insert() {
//
//        }
//
//        private static class Locator extends SpireInsertLocator {
//            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardQueueItem.class, "card");
//                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
//            }
//        }
//    }




    @SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
    public static class Patch2 {
        @SpireInsertPatch  (   locator = GetNextActionPatch.Patch2.Locator.class,
                localvars = {}  )
        public static void Insert() {
            after();
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameActionManager.class,"monsterAttacksQueued");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}