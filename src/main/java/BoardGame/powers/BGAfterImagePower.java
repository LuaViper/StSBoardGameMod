package BoardGame.powers;

import BoardGame.cards.BGRed.BGSeverSoul;
import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.green.AllOutAttack;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;


//it appears that only DiscardAction is used to discard cards
// (DiscardSpecificCardAction does not apply to the board game)
// however, DiscardAtEndOfTurnAction calls DiscardAction, so check the endTurn flag
//note that even in vanilla, Scrying does not count as discarding
//TODO: in vanilla, does Distilled Chaosing an unplayable card count as discarding it?
//TODO: DiscardAction can be passed canPickZero -- does this proc AfterImage if a card is then discarded?

public class BGAfterImagePower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGAfterImagePower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGAfterImagePower");

    public BGAfterImagePower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = "BoardGame:BGAfterImagePower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("afterImage");
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }


    public void onDiscardAction(){
        //public void onDiscardAction(int amount, int handSize, boolean endTurn){
        //if(endTurn==false && amount>0 && handSize>0){
        if(true){
            if (Settings.FAST_MODE) {
                addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, this.amount, true));
            } else {
                addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, this.amount));
            }
            flash();
        }
    }



    @SpirePatch2(clz=DiscardAction.class,method=SpirePatch.CLASS)
    public static class DiscardTotalField{
        public static SpireField<Integer> discardTotal = new SpireField<>(()->0);
    }
    @SpirePatch2(clz= DiscardAction.class,method="update",paramtypez={})
    public static class RecordDiscardTotalBeforeDiscardActionPatch {
        @SpireInsertPatch(
                locator = BGAfterImagePower.RecordDiscardTotalBeforeDiscardActionPatch.Locator.class,
                localvars = {}
        )
        public static void Insert(DiscardAction __instance) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
                return;
            }
            DiscardTotalField.discardTotal.set(__instance, GameActionManager.totalDiscardedThisTurn);
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getMonsters");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz= DiscardAction.class,method="update",paramtypez={})
    public static class CheckDiscardTotalAfterDiscardActionPatch {
        @SpirePostfixPatch
        public static void Postfix(DiscardAction __instance, @ByRef boolean[] ___endTurn) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
                return;
            }
            if(___endTurn[0]){
                return;
            }
            if(__instance.isDone) {
                BoardGame.BoardGame.logger.info("BGAfterImagePower.update.postfix.isDone...");
                BoardGame.BoardGame.logger.info("...compare "+GameActionManager.totalDiscardedThisTurn+" , "+DiscardTotalField.discardTotal.get(__instance)+" ...");
                if(GameActionManager.totalDiscardedThisTurn>DiscardTotalField.discardTotal.get(__instance)){
                    BoardGame.BoardGame.logger.info("...success!  gain 1 block");
                    AbstractPower pw=AbstractDungeon.player.getPower("BoardGame:BGAfterImagePower");
                    if(pw!=null){
                        ((BGAfterImagePower)pw).onDiscardAction();
                    }
                    //and make sure this doesn't trigger a second time for whatever unforeseen reason
                    DiscardTotalField.discardTotal.set(__instance, 0);
                }
            }
        }
    }

}

