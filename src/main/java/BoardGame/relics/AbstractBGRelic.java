package BoardGame.relics;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.relicRng;

public abstract class AbstractBGRelic extends AbstractRelic {
    public AbstractBGRelic(String setId, String imgName, RelicTier tier, LandingSound sfx) {
        super(setId, imgName, tier, sfx);
    }

    private static final Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    public static ArrayList<AbstractRelic> relicDeck = new ArrayList<>();
    public static ArrayList<AbstractRelic> bossRelicDeck = new ArrayList<>();



    //    @SpirePatch(clz = RelicLibrary.class, method = "initialize",
//            paramtypez = {})
//    public static class RelicLibraryInitializePatch {
//        @SpirePrefixPatch
        public static void initialize() {
            logger.info("INIT RELIC DECK relicRng: " + relicRng);

            relicDeck = new ArrayList<>();

            relicDeck.add(new BGAkabeko());
            relicDeck.add(new BGAnchor());
            relicDeck.add(new BGBagOfPreparation());
            relicDeck.add(new BGBirdFacedUrn());
            relicDeck.add(new BGBloodVial());
            relicDeck.add(new BGBlueCandle());
            relicDeck.add(new BGCalipers());
            relicDeck.add(new BGCaptainsWheel());
            relicDeck.add(new BGDeadBranch());
            relicDeck.add(new BGDollysMirror());
            relicDeck.add(new BGDuVuDoll());
            relicDeck.add(new BGGamblingChip());
            relicDeck.add(new BGGoldenEye());
            relicDeck.add(new BGGoldenIdol());
            relicDeck.add(new BGGremlinHorn());
            relicDeck.add(new BGHappyFlower());
            relicDeck.add(new BGHornCleat());
            relicDeck.add(new BGIceCream());
            relicDeck.add(new BGIncenseBurner());
            relicDeck.add(new BGInkBottle());
            relicDeck.add(new BGLantern());
            relicDeck.add(new BGMeatOnTheBone());
            relicDeck.add(new BGMercuryHourglass());
            relicDeck.add(new BGMoltenEgg2());
            relicDeck.add(new BGMummifiedHand());
            relicDeck.add(new BGMutagenicStrength());
            relicDeck.add(new BGNecronomicon());
            relicDeck.add(new BGNilrysCodex());
            relicDeck.add(new BGOddlySmoothStone());
            relicDeck.add(new BGOldCoin());
            relicDeck.add(new BGOmamori());
            relicDeck.add(new BGOrichalcum());
            relicDeck.add(new BGPeacePipe());
            relicDeck.add(new BGPocketwatch());
            relicDeck.add(new BGRedSkull());
            relicDeck.add(new BGRegalPillow());
            relicDeck.add(new BGRunicPyramid());
            relicDeck.add(new BGSelfFormingClay());
            relicDeck.add(new BGStrikeDummy());
            relicDeck.add(new BGSsserpentHead());
            relicDeck.add(new BGSundial());
            relicDeck.add(new BGTheBoot());
            relicDeck.add(new BGToxicEgg2());
            relicDeck.add(new BGTungstenRod());
            relicDeck.add(new BGVajra());
            relicDeck.add(new BGWarPaint());
            relicDeck.add(new BGWhetstone());
            relicDeck.add(new BGWingBoots());
            Collections.shuffle(relicDeck, new java.util.Random(relicRng.randomLong()));
        }
        public static void initializeBoss(){
            bossRelicDeck = new ArrayList<>();
            bossRelicDeck.add(new BGAstrolabe());
            bossRelicDeck.add(new BGPandorasBox());
            bossRelicDeck.add(new BGEmptyCage());
            bossRelicDeck.add(new BGOrrery());
            bossRelicDeck.add(new BGCallingBell());
            bossRelicDeck.add(new BGEnchiridion());
            bossRelicDeck.add(new BGBlackBlood());
            bossRelicDeck.add(new BGEternalFeather());
            bossRelicDeck.add(new BGHolyWater());
            bossRelicDeck.add(new BGSneckoEye());
            bossRelicDeck.add(new BGCoffeeDripper());
            bossRelicDeck.add(new BGFusionHammer());
            bossRelicDeck.add(new BGEctoplasm());
            bossRelicDeck.add(new BGRingOfTheSerpent());
            bossRelicDeck.add(new BGWristBlade());
            bossRelicDeck.add(new BGCursedKey());
            bossRelicDeck.add(new BGSozu());
            bossRelicDeck.add(new BGMarkOfPain());
//            bossRelicDeck.add(new BGTinyHouse()); //TODO: this one is still broken
            bossRelicDeck.add(new BGWhiteBeast());
            bossRelicDeck.add(new BGFrozenCore());


            Collections.shuffle(bossRelicDeck, new java.util.Random(relicRng.randomLong()));
        }
//    }

    @SpirePatch(clz = AbstractDungeon.class, method = "initializeRelicList",
            paramtypez = {})
    public static class AbstractDungeonInitializeRelicListPatch{
            @SpirePostfixPatch
        protected static void initializeRelicList(){
                initialize();
                initializeBoss();
            }
    }

    public static AbstractRelic drawFromRelicDeck(){

        if(relicDeck.size()==0){
            initialize();   //TODO: if this is the second time we would have called initialize, return a circlet instead
        }
        AbstractRelic relic=relicDeck.remove(0);
        //TODO: remove relic from deck if player obtains it
        relicDeck.add(relic);
        return relic;
    }

    public static AbstractRelic drawFromBossRelicDeck(){

        if(bossRelicDeck.size()==0){
            initializeBoss();   //TODO: if this is the second time we would have called initialize, return a circlet instead
        }
        AbstractRelic relic=bossRelicDeck.remove(0);
        //TODO: remove relic from deck if player obtains it
        bossRelicDeck.add(relic);
        return relic;
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomRelic",
            paramtypez = {AbstractRelic.RelicTier.class})
    public static class AbstractDungeonReturnRelicPatch1 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> returnRandomRelic(AbstractRelic.RelicTier tier) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(tier!=AbstractRelic.RelicTier.BOSS)
                    return SpireReturn.Return(drawFromRelicDeck());
                else
                    return SpireReturn.Return(drawFromBossRelicDeck());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomScreenlessRelic",
            paramtypez = {AbstractRelic.RelicTier.class})
    public static class AbstractDungeonReturnRelicPatch2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> returnRandomScreenlessRelic(AbstractRelic.RelicTier tier) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(tier!=AbstractRelic.RelicTier.BOSS)
                    return SpireReturn.Return(drawFromRelicDeck());
                else
                    return SpireReturn.Return(drawFromBossRelicDeck());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomNonCampfireRelic",
            paramtypez = {AbstractRelic.RelicTier.class})
    public static class AbstractDungeonReturnRelicPatch3 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> returnRandomNonCampfireRelic(AbstractRelic.RelicTier tier) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(tier!=AbstractRelic.RelicTier.BOSS)
                    return SpireReturn.Return(drawFromRelicDeck());
                else
                    return SpireReturn.Return(drawFromBossRelicDeck());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomRelicEnd",
            paramtypez = {AbstractRelic.RelicTier.class})
    public static class AbstractDungeonReturnRelicPatch4 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> returnRandomRelicEnd(AbstractRelic.RelicTier tier) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(tier!=AbstractRelic.RelicTier.BOSS) {
                    AbstractRelic r = drawFromRelicDeck();
                    if(r instanceof BGOldCoin && AbstractDungeon.getCurrRoom() instanceof ShopRoom){
                        r = new BGDiscardedOldCoin();
                        r.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), true);
                        r.flash();
                        r = drawFromRelicDeck();
                    }
                    return SpireReturn.Return(r);
                }
                else
                    return SpireReturn.Return(drawFromBossRelicDeck());
            }
            return SpireReturn.Continue();
        }
    }

}
