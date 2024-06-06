package BoardGame.relics;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.relicRng;
import static com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier.*;


public abstract class AbstractBGRelic extends AbstractRelic {
    public AbstractBGRelic(String setId, String imgName, RelicTier tier, LandingSound sfx) {
        super(setId, imgName, tier, sfx);
    }

    private static final Logger logger = LogManager.getLogger(AbstractBGRelic.class.getName());
    public static ArrayList<AbstractRelic> relicDeck = new ArrayList<>();
    public static ArrayList<AbstractRelic> bossRelicDeck = new ArrayList<>();

    public boolean usableAsPayment(){
        return !(tier == BOSS || tier == STARTER || tier == SPECIAL);
    }

    public static ArrayList<AbstractRelic> getAllPayableRelics(){
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        for(AbstractRelic r : AbstractDungeon.player.relics) {
            if(!(r instanceof AbstractBGRelic)){
                relics.add(r);
            }else {
                AbstractBGRelic bgr = (AbstractBGRelic) r;
                if (bgr.usableAsPayment()) {
                    relics.add(r);
                }
            }
        }
        return relics;
    }


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
            relicDeck.add(new BGCentennialPuzzle());
            relicDeck.add(new BGCharonsAshes());
            relicDeck.add(new BGDeadBranch());
            relicDeck.add(new BGDollysMirror());
            relicDeck.add(new BGDuality());
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
            relicDeck.add(new BGNinjaScroll());
            relicDeck.add(new BGOddlySmoothStone());
            relicDeck.add(new BGOldCoin());
            relicDeck.add(new BGOmamori());
            relicDeck.add(new BGOrichalcum());
            relicDeck.add(new BGPeacePipe());
            relicDeck.add(new BGPenNib());
            relicDeck.add(new BGPocketwatch());
            relicDeck.add(new BGRedMask());
            relicDeck.add(new BGRedSkull());
            relicDeck.add(new BGRegalPillow());
            relicDeck.add(new BGRunicPyramid());
            relicDeck.add(new BGSelfFormingClay());
            relicDeck.add(new BGStoneCalendar());
            relicDeck.add(new BGStrikeDummy());
            relicDeck.add(new BGSsserpentHead());
            relicDeck.add(new BGSundial());
            relicDeck.add(new BGTheBoot());
            relicDeck.add(new BGTheAbacus());
            relicDeck.add(new BGTheCourier());
            relicDeck.add(new BGToolbox());
            relicDeck.add(new BGToxicEgg2());
            relicDeck.add(new BGTungstenRod());
            relicDeck.add(new BGVajra());
            relicDeck.add(new BGWarPaint());
            relicDeck.add(new BGWhetstone());
            relicDeck.add(new BGWingBoots());
            // = 58 relics
            Collections.shuffle(relicDeck, new java.util.Random(relicRng.randomLong()));
        }
        public static void initializeBossRelics(){
            bossRelicDeck = new ArrayList<>();
            bossRelicDeck.add(new BGAstrolabe());
            bossRelicDeck.add(new BGPandorasBox());
            bossRelicDeck.add(new BGEmptyCage());
            bossRelicDeck.add(new BGOrrery());
            bossRelicDeck.add(new BGCallingBell());
            bossRelicDeck.add(new BGEnchiridion());
            bossRelicDeck.add(new BGBlackBlood());
            //bossRelicDeck.add(new BGEternalFeather());    //this one was removed
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
            bossRelicDeck.add(new BGTinyHouse()); //TODO: this one is still broken
            bossRelicDeck.add(new BGWhiteBeast());
            bossRelicDeck.add(new BGFrozenCore());
            // = 20 boss relics


            Collections.shuffle(bossRelicDeck, new java.util.Random(relicRng.randomLong()));
        }

    public void onAboutToUseCard(AbstractCard card, AbstractCreature originalTarget) {
    }
//    }

    @SpirePatch(clz = AbstractDungeon.class, method = "initializeRelicList",
            paramtypez = {})
    public static class AbstractDungeonInitializeRelicListPatch{
            @SpirePostfixPatch
        protected static void initializeRelicList(){
                initialize();
                initializeBossRelics();
            }
    }

    public static AbstractRelic drawFromRelicDeck(){

        if(relicDeck.size()==0){
            initialize();   //TODO: if this is the second time we would have called initialize, return a circlet instead
        }
        AbstractRelic relic=relicDeck.remove(0).makeCopy();
        //TODO: remove relic from deck if player obtains it
        relicDeck.add(relic);
        return relic;
    }

    public static AbstractRelic drawFromBossRelicDeck(){

        if(bossRelicDeck.size()==0){
            initializeBossRelics();   //TODO: if this is the second time we would have called initialize, return a circlet instead
        }
        AbstractRelic relic=bossRelicDeck.remove(0).makeCopy();
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
                if(tier!= BOSS)
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
                if(tier!= BOSS)
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
                if(tier!= BOSS)
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
                if(tier!= BOSS) {
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



    public AbstractRelic makeCopy() {
        try {
            return (AbstractRelic)this.getClass().newInstance();
        } catch (IllegalAccessException | InstantiationException var2) {
            throw new RuntimeException("BaseMod (well, ok, actually AbstractBGRelic copying BaseMod's code) failed to auto-generate makeCopy for relic: " + this.relicId);
        }
    }


    public void setupObtainedDuringCombat(){
        //for use with clickable relics that can spawn mid-combat, either Courier or Shiv-style
        if(this instanceof ClickableRelic) {
            //must check that relic is clickable as others have side effects during e.g. onVictory
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                this.onVictory();
                this.atPreBattle();
                //TODO: any other events we're missing?
                this.atTurnStart();
            }
        }
    }



}
