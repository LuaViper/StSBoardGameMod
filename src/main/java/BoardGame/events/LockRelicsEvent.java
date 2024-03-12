package BoardGame.events;

import BoardGame.relics.AbstractBGRelic;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public interface LockRelicsEvent {

    public abstract boolean relicsLocked();

    //TODO: remove "right-click to activate" message from "can't be used in combat or while otherwise occupied" when the player is occupied

    @SpirePatch2(clz= ClickableRelic.class,method="clickUpdate",paramtypez={})
    public static class DisableRelicClicksDuringEventPatch {
        @SpireInsertPatch(
                locator = LockRelicsEvent.DisableRelicClicksDuringEventPatch.Locator.class,
                localvars = {}
        )
        public static SpireReturn<Void> Insert() {
            if(AbstractDungeon.getCurrRoom()!=null){
                if(AbstractDungeon.getCurrRoom().event instanceof LockRelicsEvent){
                    if(((LockRelicsEvent)AbstractDungeon.getCurrRoom().event).relicsLocked()) {
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ClickableRelic.class, "onRightClick");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
