//package BoardGame.patches.bossrewards;
//
//import BoardGame.dungeons.AbstractBGDungeon;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.evacipated.cardcrawl.modthespire.lib.*;
//import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.relics.AbstractRelic;
//import com.megacrit.cardcrawl.relics.Circlet;
//import com.megacrit.cardcrawl.rewards.RewardItem;
//import com.megacrit.cardcrawl.rooms.AbstractRoom;
//import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
//import com.megacrit.cardcrawl.screens.CardRewardScreen;
//import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
//import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
//import javassist.CannotCompileException;
//import javassist.CtBehavior;
//import javassist.expr.ExprEditor;
//import javassist.expr.MethodCall;
//
//import java.util.ArrayList;
//
//public class BossCardReward {
//
//    @SpirePatch(
//            clz= TreasureRoomBoss.class,
//            method=SpirePatch.CLASS
//    )
//    public static class Field
//    {
//        public static SpireField<RewardItem> cardReward = new SpireField<>(() -> null);
//        public static SpireField<Boolean> relicAlreadyTaken = new SpireField<>(()->false);
//    }
//
//    @SpirePatch(
//            clz=RewardItem.class,
//            method=SpirePatch.CLASS
//    )
//    public static class RewardItemField
//    {
//        public static SpireField<Boolean> isBossCard = new SpireField<>(() -> false);
//    }
//
//    @SpirePatch2(clz = TreasureRoomBoss.class, method = "onPlayerEntry",
//            paramtypez={})
//    public static class AddCardReward {
//        @SpirePostfixPatch
//        public static void Foo(TreasureRoomBoss __instance) {
//            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
//            AbstractBGDungeon.forceRareRewards = true;
//            RewardItem r = new RewardItem();
//            RewardItemField.isBossCard.set(r, true);
//            Field.cardReward.set(__instance, r);
//            AbstractBGDungeon.forceRareRewards = false;
//            r.move(Settings.HEIGHT / 2.0F - 220.0F * Settings.scale);
//
//        }
//    }
//
//
//    @SpirePatch2(clz = BossRelicSelectScreen.class, method = "updateControllerInput",
//            paramtypez={})
//    public static class ControllerInput {
//        @SpirePostfixPatch
//        public static void Foo(BossRelicSelectScreen __instance) {
//            if(!(AbstractBGDungeon.getCurrRoom() instanceof TreasureRoomBoss))return;
//            RewardItem r=Field.cardReward.get(AbstractBGDungeon.getCurrRoom());
//            if(r!=null) {
//                r.update();
//                if (r.isDone) {
//                    r.isDone=false;
//                    AbstractDungeon.cardRewardScreen.open(r.cards,r, RewardItem.TEXT[4]);
//                    AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.BOSS_REWARD;
//                }
//            };
//            //TODO: also patch gamepad input for BossRelicSelectScreen
//        }
//    }
//
//    @SpirePatch2(clz = CardRewardScreen.class, method = "takeReward",
//            paramtypez={})
//    public static class ClickCardPatch {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> Foo(CardRewardScreen __instance) {
//            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return SpireReturn.Continue();
//            if(!(AbstractBGDungeon.getCurrRoom() instanceof TreasureRoomBoss))return SpireReturn.Continue();
//            if(__instance.rItem!=null){
//                if(!RewardItemField.isBossCard.get(__instance.rItem)) return SpireReturn.Continue();
//            }
//            Field.cardReward.set(AbstractBGDungeon.getCurrRoom(),null);
//
//            //don't closeCurrentScreen here -- CardRewardScreen.cardSelectUpdate is about to do it for us
//
//            return SpireReturn.Return();
//        }
//    }
//
//
//
//    @SpirePatch2(clz = BossRelicSelectScreen.class, method = "render",
//            paramtypez={SpriteBatch.class})
//    public static class RenderCard {
//        @SpirePostfixPatch
//        public static void Foo(BossRelicSelectScreen __instance, SpriteBatch sb) {
//            if(!(AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss))return;
//            RewardItem r=Field.cardReward.get(AbstractDungeon.getCurrRoom());
//            if(r!=null)r.render(sb);
//        }
//    }
//
//
//
//
//    public static boolean actuallyIsDoneAlsoSetProceedButton(BossRelicSelectScreen instance){
//        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))
//            return true;
//        if(Field.relicAlreadyTaken.get(AbstractDungeon.getCurrRoom())
//                && BossCardReward.Field.cardReward.get(AbstractDungeon.getCurrRoom())==null)
//        {
//            AbstractDungeon.overlayMenu.proceedButton.show();
//            return true;
//        }
//        AbstractDungeon.overlayMenu.proceedButton.hide();
//        return false;
//    }
//
//    @SpirePatch2(clz = BossRelicSelectScreen.class, method = "update")
//    public static class RememberRelicWasTaken {
//        @SpireInsertPatch(
//            locator= Locator.class,
//            localvars={}
//        )
//        public static void Foo(BossRelicSelectScreen __instance) {
//            Field.relicAlreadyTaken.set(AbstractDungeon.getCurrRoom(),true);
//        }
//        private static class Locator extends SpireInsertLocator {
//            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class,"clear");
//                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
//            }
//        }
//    }
//
//    @SpirePatch2(clz = BossRelicSelectScreen.class, method = "update")
//    public static class DontImmediatelyExitAfterTakingRelic {
//        @SpireInstrumentPatch
//        public static ExprEditor Foo() {
//            return new ExprEditor() {
//                public void edit(MethodCall m) throws CannotCompileException {
//                    if (m.getClassName().equals(AbstractDungeon.class.getName()) && m.getMethodName().equals("closeCurrentScreen")) {
//                        m.replace("{ if("+BossCardReward.class.getName()+".actuallyIsDoneAlsoSetProceedButton(this)) { $_ = $proceed($$); } }");
//                    }
//                }
//            };
//        }
//    }
//
//    @SpirePatch2(clz = BossRelicSelectScreen.class, method = "update")
//    public static class MaybeImmediatelyExitAfterTakingCard {
//        @SpireInsertPatch(
//                locator= Locator.class,
//                localvars={}
//        )
//        public static void Foo(BossRelicSelectScreen __instance) {
//            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
//                if (actuallyIsDoneAlsoSetProceedButton(__instance)) {
//                    AbstractDungeon.overlayMenu.cancelButton.hide();
//                    AbstractDungeon.closeCurrentScreen();
//                }
//            }
//        }
//        private static class Locator extends SpireInsertLocator {
//            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//                Matcher finalMatcher = new Matcher.MethodCallMatcher(BossRelicSelectScreen.class,"updateCancelButton");
//                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
//            }
//        }
//    }
//
//    @SpirePatch2(clz = BossRelicSelectScreen.class, method = "open",
//            paramtypez = {ArrayList.class})
//    public static class IfRelicsWereRemovedFromChestRemoveThemAgainUponReopening {
//        @SpirePrefixPatch
//        public static void Foo(BossRelicSelectScreen __instance, ArrayList<AbstractRelic> chosenRelics) {
//            if(Field.relicAlreadyTaken.get(AbstractDungeon.getCurrRoom())){
//                chosenRelics.clear();
//                for(int i=0;i<3;i+=1){
//                    chosenRelics.add(new Circlet());
//                }
//            }
//        }
//        @SpirePostfixPatch
//        public static void Bar(BossRelicSelectScreen __instance, ArrayList<AbstractRelic> chosenRelics) {
//            if(Field.relicAlreadyTaken.get(AbstractDungeon.getCurrRoom())){
//                __instance.relics.clear();
//            }
//        }
//    }
//
//
//
//
//
//
//    @SpirePatch2(clz = ProceedButton.class, method = "update",
//    paramtypez={})
//    public static class CheckProceedButtonContext {
//        @SpireInstrumentPatch
//        public static ExprEditor Foo() {
//            return new ExprEditor() {
//                public void edit(MethodCall m) throws CannotCompileException {
//                    if (m.getClassName().equals(ProceedButton.class.getName()) && m.getMethodName().equals("goToNextDungeon")) {
//                        m.replace("{ if("+BossCardReward.class.getName()+".proceedButtonContextChecker(this)) { $_ = $proceed($$); }" +
//                                "else "+BossCardReward.class.getName()+".proceedButtonReturnToBossRewards(this); }");
//                    }
//                }
//            };
//        }
//    }
//
//    public static boolean proceedButtonContextChecker(ProceedButton __instance){
//        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return true;
//        if(AbstractDungeon.screen==AbstractDungeon.CurrentScreen.NONE){
//            return true;
//        }
//        return false;
//    }
//

//    public static void proceedButtonReturnToBossRewards(ProceedButton __instance){
//        if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;    //note that we should never get to this function in the first place if not BGdungeon
//        if(AbstractDungeon.screen!=AbstractDungeon.CurrentScreen.NONE){
//            AbstractDungeon.previousScreen= AbstractDungeon.CurrentScreen.BOSS_REWARD;
//        }
//        AbstractDungeon.closeCurrentScreen();
//    }
//
//}
