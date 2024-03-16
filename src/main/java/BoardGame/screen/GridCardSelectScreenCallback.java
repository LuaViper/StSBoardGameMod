package BoardGame.screen;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.neow.BGNeowQuickStart;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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