package BoardGame.screen;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GridCardSelectScreenCallback {

    public boolean isDone = false;
    public GridCardSelectScreen gridScreen;


    public interface CallbackGridAction {
        void execute();
    }

    @SpirePatch(
            clz=GridCardSelectScreen.class,method=SpirePatch.CLASS
    )
    public static class CallbackField
    {
        public static SpireField<CallbackGridAction> callback = new SpireField<>(()->null);
    }


    @SpirePatch2(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class GridCardSelectScreenCallbackPatch {
        @SpireInsertPatch(
                locator= GridCardSelectScreenCallback.GridCardSelectScreenCallbackPatch.Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert() {
            if(CallbackField.callback.get(AbstractDungeon.gridSelectScreen)!=null){
                CallbackField.callback.get(AbstractDungeon.gridSelectScreen).execute();
                CallbackField.callback.set(AbstractDungeon.gridSelectScreen,null);
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class,"isEmpty");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }



}