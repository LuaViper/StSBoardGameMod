package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GetNextActionPatch {
    public static void before(AbstractCard c){
        if(c!=null) {
            ContextPatches.pushTargetContext(CardTargetingPatches.CardField.lastHoveredTarget.get(c));
        }else{
            ContextPatches.pushTargetContext(null);
        }
        //REMINDER: copied cards (from Foreign Influence and Doppelganger) must manually set owner before playing
        if(CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER)return;
        if(ContextPatches.originalBGMultiCharacter==null)ContextPatches.originalBGMultiCharacter=AbstractDungeon.player;

        if(c!=null) {
            ContextPatches.pushPlayerContext(CardPatches.Field.owner.get(c));
        }else{
            ContextPatches.pushPlayerContext(ContextPatches.originalBGMultiCharacter);
        }
    }
    public static void after(){
        ContextPatches.popTargetContext();
        if(CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER)return;
        ContextPatches.popPlayerContext();
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
