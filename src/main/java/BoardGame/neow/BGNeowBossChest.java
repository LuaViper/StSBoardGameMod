package BoardGame.neow;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGNeowBossChest {
    private static final Logger logger = LogManager.getLogger(BGNeowBossChest.class.getName());

    @SpirePatch(clz = RewardItem.class, method = "claimReward",
            paramtypez = {})
    public static class PickOnlyOneRelicPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> claimReward(RewardItem __instance) {
            //logger.info("BGNeowBossChest: claimReward Patch");
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if (AbstractDungeon.getCurrRoom() instanceof NeowRoom) {
                    if (__instance.type == RewardItem.RewardType.RELIC) {
                        if(__instance.relic.tier == AbstractRelic.RelicTier.BOSS) {
                            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
                                return SpireReturn.Return(false);
                            }

                            if (!__instance.ignoreReward) {
                                __instance.relic.instantObtain();
                                CardCrawlGame.metricData.addRelicObtainData(__instance.relic);
                            }

                            BGNeowQuickStart.clearAllRewards();

                            return SpireReturn.Return(true);

                            //TODO: some choices (e.g. Cursed Key) don't remove the other two relics from the screen (but they're still marked as unrewardable)
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

//    //there is an edge case where if the player skips all relics, a warning popup will appear.
//    //upon closing the popup, the game will try to restore the cancel button, which we hid previously
//    //...so just hide it every frame.
//    //note that the first time we try to skip, the popup will actually interrupt the skip itself, but the 2nd attempt will work.
//    @SpirePatch2(clz = CancelButton.class, method = "show",
//            paramtypez = {String.class})
//    public static class CancelButtonShowPatch {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> show(String buttonText){
//            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon && AbstractDungeon.getCurrRoom() instanceof NeowRoom){
//                return SpireReturn.Return();
//            }
//            return SpireReturn.Continue();
//        }
//    }




}
