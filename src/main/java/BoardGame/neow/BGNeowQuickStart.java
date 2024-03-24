package BoardGame.neow;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.multicharacter.MultiCharacterSelectScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGNeowQuickStart {
    private static final Logger logger = LogManager.getLogger(BGNeowQuickStart.class.getName());
    public static int actNumber=2;
    public static int rewardIndex=1;
    public static int rewardCounter=0;

    public static int rollTheDieReward=-1;
    public static int[][] quickStartQuantities={
            {1,1,1},    //neow bonus
            {6,7,10},   //gold              //1
            {4,4,5},    //card rewards
            {1,1,1},    //transform card
            {1,3,5},    //Roll the Die
            {1,1,1},    //get 1 potion      //5
            {2,4,6},    //relics
            {1,2,2},    //rare card rewards
            {1,2,2},    //boss relics
            {0,3,5},    //more card rewards
            {0,1,2},    //remove card       //10
            {2,4,6}     //upgrade card
            //then visit the merchant
    };

    private static final CharacterStrings extraStrings = CardCrawlGame.languagePack.getCharacterString("BoardGame:BGNeow Reward");
    public static final String[] EXTRA = extraStrings.TEXT;

    public static void setRollTheDieButtons(BGNeowEvent event){
        event.roomEventText.clearRemainingOptions();
        int numRewards=BGNeowQuickStart.quickStartQuantities[4][BGNeowQuickStart.actNumber - 2];
        if(BGNeowQuickStart.rewardCounter<numRewards) {
            String suffix="";
            if(numRewards>1){
                suffix=" ("+(BGNeowQuickStart.rewardCounter+1)+"/"+numRewards+")";
            }
            switch (BGNeowQuickStart.rollTheDieReward) {
                case 1:
                    event.roomEventText.updateDialogOption(0, EXTRA[61]+suffix);
                    return;
                case 2:
                    event.roomEventText.updateDialogOption(0, EXTRA[46]+suffix);
                    event.roomEventText.addDialogOption(EXTRA[47]);
                    return;
                case 3:
                    event.roomEventText.updateDialogOption(0, EXTRA[62]+suffix);
                    return;
                case 4:
                    event.roomEventText.updateDialogOption(0, EXTRA[63]+suffix);
                    return;
                case 5:
                    event.roomEventText.updateDialogOption(0, EXTRA[3]+suffix);
                    return;
                case 6:
                    event.roomEventText.updateDialogOption(0, EXTRA[2]+suffix);
                    event.roomEventText.addDialogOption(EXTRA[47]);
                    return;
            }
        }else{
            rewardCounter=0;
            rewardIndex=5;
            event.roomEventText.updateDialogOption(0, EXTRA[51]);
        }
    }

    public static void clearAllRewards(){
        for (int j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
            boolean okToClear=true;
            RewardItem reward=AbstractDungeon.combatRewardScreen.rewards.get(j);
            if(reward.type == RewardItem.RewardType.RELIC && reward.relic.isObtained){
                okToClear=false;
            }else if(reward.type== RewardItem.RewardType.POTION && reward.potion.isObtained){
                okToClear=false;
            }
            if(okToClear) {
                reward.isDone = true;
                reward.ignoreReward = true;
                //logger.info("Cleared a reward: "+reward);
            }
        }
        AbstractDungeon.getCurrRoom().rewards.clear();
        //logger.info("ClearAllRewards done");
    }

    public static void openBossRelicScreen(){
        if (!AbstractDungeon.isScreenUp) {
            clearAllRewards();
            String suffix="";
            int numRewards=BGNeowQuickStart.quickStartQuantities[8][BGNeowQuickStart.actNumber - 2];
            if(numRewards>1){
                suffix=" ("+(BGNeowQuickStart.rewardCounter+1)+"/"+numRewards+")";
            }

            AbstractDungeon.combatRewardScreen.open("Choose ONE Relic."+suffix);    //TODO: localization
            AbstractDungeon.combatRewardScreen.rewards.clear();

            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(
                    AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS)));
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(
                    AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS)));
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(
                    AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS)));


            AbstractDungeon.combatRewardScreen.positionRewards();
            AbstractDungeon.overlayMenu.proceedButton.show();
            AbstractDungeon.overlayMenu.proceedButton.setLabel("Skip Rewards"); //TODO: localization, or copy from CallingBell.DESCRIPTIONS[2]
            AbstractDungeon.overlayMenu.cancelButton.hideInstantly();
            //(AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
            //AbstractDungeon.overlayMenu.cancelButton.buttonText="Skip Rewards";
        }
    }



    //ProceedButton expects us to be Done And Moving On when we click on it.  override that.
    @SpirePatch(clz = ProceedButton.class, method = "update",
            paramtypez = {})
    public static class ProceedButtonUpdatePatch {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> update() {
            //after button is clicked, but before it sets screen to complete...
            if (CardCrawlGame.dungeon != null && CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.neow.NeowRoom) {
                    AbstractDungeon.closeCurrentScreen();
                    AbstractDungeon.screen=AbstractDungeon.CurrentScreen.NONE;
                    AbstractDungeon.overlayMenu.hideBlackScreen();
                    AbstractDungeon.overlayMenu.proceedButton.hide();
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.InstanceOfMatcher(NeowRoom.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    @SpirePatch(clz = ProceedButton.class, method = "update",
            paramtypez = {})
    public static class ProceedButtonUpdatePatch2 {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> update(ProceedButton __instance ) {
            logger.info("BGNeowQuickStart: ProceedButtonUpdatePatch2");
            if (CardCrawlGame.dungeon != null && CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.ShopRoom) {
                    if(rewardIndex==12){
                        rewardIndex=0;
                        if(actNumber==2){
                            CardCrawlGame.nextDungeon="TheCity";
                        }else if(actNumber==3){
                            CardCrawlGame.nextDungeon="TheBeyond";
                        }else if(actNumber==4){
                            CardCrawlGame.nextDungeon="TheEnding";
                            Settings.hasRubyKey=true;
                            Settings.hasSapphireKey=true;
                            Settings.hasEmeraldKey=true;
                        }
                        CardCrawlGame.music.fadeOutBGM();
                        CardCrawlGame.music.fadeOutTempBGM();
                        AbstractDungeon.fadeOut();
                        AbstractDungeon.isDungeonBeaten = true;
                        AbstractDungeon.getCurrRoom().phase=AbstractRoom.RoomPhase.COMPLETE;
                        __instance.hide();
                    }
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.InstanceOfMatcher(MonsterRoomBoss.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
//
//
//
//    @SpirePatch(clz = ProceedButton.class, method = "update",
//            paramtypez = {})
//    public static class ProceedButtonUpdatePatch3 {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> update(ProceedButton __instance ) {
//            if (CardCrawlGame.dungeon != null && CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
//                if(AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.ShopRoom) {
//                    if(rewardIndex==12){
//                        AbstractDungeon.overlayMenu.proceedButton.show();
//                    }
//                }
//            }
//            return SpireReturn.Continue();
//        }
//    }

    @SpirePatch(
            clz = ProceedButton.class,
            method = "update",
            paramtypez = {}
    )
    public static class ProceedButtonUpdatePatch4 {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static SpireReturn<Void> update(ProceedButton __instance) {
            BGNeowQuickStart.logger.info("BGNeowQuickStart: ProceedButtonUpdatePatch4");
            if (AbstractDungeon.screen == MultiCharacterSelectScreen.Enum.MULTI_CHARACTER_SELECT) {
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.overlayMenu.hideBlackScreen();
                AbstractDungeon.overlayMenu.proceedButton.hide();
                return SpireReturn.Return();
            } else {
                return SpireReturn.Continue();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DungeonMapScreen.class, "open");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[2]};
            }
        }
    }


}
