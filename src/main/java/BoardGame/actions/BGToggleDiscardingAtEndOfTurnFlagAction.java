package BoardGame.actions;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BGToggleDiscardingAtEndOfTurnFlagAction extends AbstractGameAction {

    boolean enabled=false;
    public BGToggleDiscardingAtEndOfTurnFlagAction(boolean enabled) {
        this.enabled=enabled;
    }
    @Override
    public void update() {
        Field.discardingCardsAtEndOfTurn.set(AbstractDungeon.actionManager,enabled);
        this.isDone=true;
    }

    @SpirePatch(
            clz= GameActionManager.class,
            method=SpirePatch.CLASS
    )
    public static class Field {
        public static SpireField<Boolean> discardingCardsAtEndOfTurn = new SpireField<>(() -> false);
    }



    @SpirePatch2(clz=AbstractRoom.class,method="endTurn")
    public static class EndTurnPatches {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Insert() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                AbstractDungeon.actionManager.addToBottom(new BGToggleDiscardingAtEndOfTurnFlagAction(true));
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.NewExprMatcher(DiscardAtEndOfTurnAction.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class,
                localvars = {}
        )
        public static void Insert2() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                AbstractDungeon.actionManager.addToBottom(new BGToggleDiscardingAtEndOfTurnFlagAction(false));
            }
        }
        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class,"drawPile");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
