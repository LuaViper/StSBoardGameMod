//TODO: make sure ALL monsters are using BG damage icons correctly, we forgot to Implements them
package BoardGame.dungeons;

import BoardGame.cards.BGStatus.BGBurn;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.scenes.TheEndingScene;
import java.util.ArrayList;

public class BGTheEnding extends AbstractBGDungeon {

    //NOTE: End of act 3 cutscene persists (heart visible) behind the act 4 map when it first loads in, but this appears to be a vanilla bug
    private static final Logger logger = LogManager.getLogger(BGTheEnding.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("TheEnding");
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String NAME = TEXT[0];
    public static final String ID = "TheEnding";

    public BGTheEnding(AbstractPlayer p, ArrayList<String> theList) {
        super(NAME, "TheEnding", p, theList);

        if (scene != null) {
            scene.dispose();
        }

        scene = (AbstractScene)new TheEndingScene();
        fadeColor = Color.valueOf("140a1eff");
        sourceFadeColor = Color.valueOf("140a1eff");

        initializeLevelSpecificChances();
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + (AbstractDungeon.actNum * 300)));
        generateSpecialMap();
        CardCrawlGame.music.changeBGM(id);

        logger.info("Shuffling reward deck...");
        rewardDeck.shuffle(cardRng);
    }

    public BGTheEnding(AbstractPlayer p, SaveFile saveFile) {
        super(NAME, p, saveFile);
        CardCrawlGame.dungeon = this;

        if (scene != null) {
            scene.dispose();
        }

        scene = (AbstractScene)new TheEndingScene();
        fadeColor = Color.valueOf("140a1eff");
        sourceFadeColor = Color.valueOf("140a1eff");

        initializeLevelSpecificChances();
        miscRng = new Random(Long.valueOf(Settings.seed.longValue() + saveFile.floor_num));
        CardCrawlGame.music.changeBGM(id);
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + (saveFile.act_num * 300)));
        generateSpecialMap();
        firstRoomChosen = true;
        populatePathTaken(saveFile);

        //Saved deck is already shuffled -- don't reshuffle! (especially not after the 1st floor!)
    }

    private void generateSpecialMap() {
        long startTime = System.currentTimeMillis();

        map = new ArrayList<>();


        ArrayList<MapRoomNode> row1 = new ArrayList<>();
        MapRoomNode restNode = new MapRoomNode(3, 0);
        restNode.room = (AbstractRoom)new RestRoom();
        MapRoomNode shopNode = new MapRoomNode(3, 1);
        shopNode.room = (AbstractRoom)new ShopRoom();
        MapRoomNode enemyNode = new MapRoomNode(3, 2);
        if(ascensionLevel>=11) {
            enemyNode.room = (AbstractRoom) new MonsterRoomElite();
        }else{
            enemyNode.room = (AbstractRoom) new EventRoom();
            enemyNode.room.setMapSymbol("E");
            enemyNode.room.setMapImg(ImageMaster.MAP_NODE_ELITE,ImageMaster.MAP_NODE_ELITE_OUTLINE);
        }
        MapRoomNode bossNode = new MapRoomNode(3, 3);
        bossNode.room = (AbstractRoom)new MonsterRoomBoss();
        MapRoomNode victoryNode = new MapRoomNode(3, 4);
        victoryNode.room = (AbstractRoom)new TrueVictoryRoom();


        connectNode(restNode, shopNode);
        connectNode(shopNode, enemyNode);
        enemyNode.addEdge(new MapEdge(enemyNode.x, enemyNode.y, enemyNode.offsetX, enemyNode.offsetY, bossNode.x, bossNode.y, bossNode.offsetX, bossNode.offsetY, false));




        row1.add(new MapRoomNode(0, 0));
        row1.add(new MapRoomNode(1, 0));
        row1.add(new MapRoomNode(2, 0));
        row1.add(restNode);
        row1.add(new MapRoomNode(4, 0));
        row1.add(new MapRoomNode(5, 0));
        row1.add(new MapRoomNode(6, 0));


        ArrayList<MapRoomNode> row2 = new ArrayList<>();
        row2.add(new MapRoomNode(0, 1));
        row2.add(new MapRoomNode(1, 1));
        row2.add(new MapRoomNode(2, 1));
        row2.add(shopNode);
        row2.add(new MapRoomNode(4, 1));
        row2.add(new MapRoomNode(5, 1));
        row2.add(new MapRoomNode(6, 1));


        ArrayList<MapRoomNode> row3 = new ArrayList<>();
        row3.add(new MapRoomNode(0, 2));
        row3.add(new MapRoomNode(1, 2));
        row3.add(new MapRoomNode(2, 2));
        row3.add(enemyNode);
        row3.add(new MapRoomNode(4, 2));
        row3.add(new MapRoomNode(5, 2));
        row3.add(new MapRoomNode(6, 2));


        ArrayList<MapRoomNode> row4 = new ArrayList<>();
        row4.add(new MapRoomNode(0, 3));
        row4.add(new MapRoomNode(1, 3));
        row4.add(new MapRoomNode(2, 3));
        row4.add(bossNode);
        row4.add(new MapRoomNode(4, 3));
        row4.add(new MapRoomNode(5, 3));
        row4.add(new MapRoomNode(6, 3));


        ArrayList<MapRoomNode> row5 = new ArrayList<>();
        row5.add(new MapRoomNode(0, 4));
        row5.add(new MapRoomNode(1, 4));
        row5.add(new MapRoomNode(2, 4));
        row5.add(victoryNode);
        row5.add(new MapRoomNode(4, 4));
        row5.add(new MapRoomNode(5, 4));
        row5.add(new MapRoomNode(6, 4));


        map.add(row1);
        map.add(row2);
        map.add(row3);
        map.add(row4);
        map.add(row5);

        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, Boolean.valueOf(true)));
        logger.info("Game Seed: " + Settings.seed);
        logger.info("Map generation time: " + (System.currentTimeMillis() - startTime) + "ms");
        firstRoomChosen = false;
        fadeIn();
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }



    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;


        smallChestChance = 0;
        mediumChestChance = 100;
        largeChestChance = 0;


        commonRelicChance = 0;
        uncommonRelicChance = 100;
        rareRelicChance = 0;


        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.25F;
        } else {
            cardUpgradedChance = 0.5F;
        }
    }



    protected void generateMonsters() {
        //TODO: when it comes time to add shield+spear, remember that "one row" AOE doesn't work against them (will probably require significant changes)

        monsterList = new ArrayList<>();
        monsterList.add("BoardGame:Shield and Spear");
        monsterList.add("BoardGame:Shield and Spear");
        monsterList.add("BoardGame:Shield and Spear");

        eliteMonsterList = new ArrayList<>();
        eliteMonsterList.add("BoardGame:Shield and Spear");
        eliteMonsterList.add("BoardGame:Shield and Spear");
        eliteMonsterList.add("BoardGame:Shield and Spear");
    }



    protected void generateWeakEnemies(int count) {}



    protected void generateStrongEnemies(int count) {}



    protected void generateElites(int count) {}



    protected ArrayList<String> generateExclusions() {
        return new ArrayList<>();
    }


    protected void initializeBoss() {
        bossList.add("The Heart");
        bossList.add("The Heart");
        bossList.add("The Heart");
    }



    protected void initializeEventList() {}



    protected void initializeEventImg() {
        if (eventBackgroundImg != null) {
            eventBackgroundImg.dispose();
            eventBackgroundImg = null;
        }
        eventBackgroundImg = ImageMaster.loadImage("images/ui/event/panel.png");
    }

    protected void initializeShrineList() {}





    @SpirePatch2(clz = DungeonMap.class, method = "calculateMapSize",
            paramtypez={})
    public static class calculateMapSizePatch {
        @SpirePostfixPatch
        public static float calculateMapSize(float __result) {
            final Logger logger = LogManager.getLogger(BGTheBeyond.class.getName());
            if (CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheEnding) {
                return Settings.MAP_DST_Y * 4.0F - 1380.0F * Settings.scale;
            }
            return __result;
        }
    }

    @SpirePatch2(clz= DungeonMapScreen.class, method="open",
            paramtypez={boolean.class})
    public static class DungeonMapScreenCityScrollPatch{
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static void open(DungeonMapScreen __instance, boolean doScrollingAnimation, @ByRef float[] ___mapScrollUpperLimit) {
            if(CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheEnding){
                ___mapScrollUpperLimit[0]= (float)(-300.0F * Settings.scale);
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class,"releaseCard");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }




    @SpirePatch2(clz= MonsterRoomElite.class,method="applyEmeraldEliteBuff",paramtypez={})
    public static class BurningEliteBuffPatch{
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                if (Settings.isFinalActAvailable && (AbstractDungeon.getCurrMapNode()).hasEmeraldKey) {
                    AbstractCard c = new BGBurn();
                    //TODO: the burns no longer overlap in center (good), but the left burn appears to be missing a "move to draw pile" animation (bad)
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)c, 2,true,true));
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz= AbstractRoom.class,method="addRelicToRewards",paramtypez={AbstractRelic.RelicTier.class})
    public static class EliteSapphireKeyPatch{
        @SpirePostfixPatch
        public static void Postfix(AbstractRoom __instance, AbstractRelic.RelicTier tier) {
            Logger logger = LogManager.getLogger(BGTheEnding.class.getName());
            logger.info("EliteSapphireKeyPatch");
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                logger.info("it's an AbstractBGDungeon");
                if (__instance instanceof MonsterRoomElite) {
                    logger.info("it's a MonsterRoomElite");
                    if (Settings.isFinalActAvailable && !Settings.hasSapphireKey) {
                        logger.info("add the key");
                        __instance.addSapphireKey(
                                __instance.rewards.get((__instance.rewards.size() - 1)));
                        logger.info("done");
                    }
                }
                //TODO: also check for Dead Adventurer and Colosseum
            }
        }
    }




}
