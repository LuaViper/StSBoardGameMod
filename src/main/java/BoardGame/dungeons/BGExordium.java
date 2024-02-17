package BoardGame.dungeons;

import BoardGame.monsters.bgexordium.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.random.Random;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.scenes.TheBottomScene;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BGExordium
        extends AbstractBGDungeon
{
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("Exordium");
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String NAME = TEXT[0];
    //note that this object's ID is Exordium, not BGExordium -- things are hardcoded to check for it
    public static final String ID = "Exordium";



    public static ArrayList<String> darkMapTokenPool=new ArrayList<>();
    public static ArrayList<String> lightMapTokenPool=new ArrayList<>();
    public static void shuffleMapTokens(){
        darkMapTokenPool=new ArrayList<String>(Arrays.asList("E","E","E","M","M","M","?","?"));
        lightMapTokenPool=new ArrayList<String>(Arrays.asList("M","?","$","$","R","R","R"));
        Collections.shuffle(darkMapTokenPool, new java.util.Random(mapRng.randomLong()));
        Collections.shuffle(lightMapTokenPool, new java.util.Random(mapRng.randomLong()));

    }
    public static char getDarkTokenSymbol(){
        return darkMapTokenPool.remove(0).charAt(0);
    }
    public static char getLightTokenSymbol(){
        return lightMapTokenPool.remove(0).charAt(0);
    }


    //TODO: monsters currently don't remember if they're already on the field -- so if the deck is reshuffled, the same monster can be played twice
    public static ArrayList<String> summonAcidSlimePool=new ArrayList<>();
    public static String getSummonAcidSlime(){
        if(summonAcidSlimePool.size()==0){
            //(C)orrosive Spit (daze), (L)ick (weak), (A)ttack
            summonAcidSlimePool.add("CAL");
            summonAcidSlimePool.add("LCA");
            summonAcidSlimePool.add("ALC");
            summonAcidSlimePool.add("LAC");
            Collections.shuffle(summonAcidSlimePool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonAcidSlimePool.remove(0);
    }


    public static ArrayList<String> summonGreenLousePool=new ArrayList<>();
    public static String getSummonGreenLouse(){
        if(summonGreenLousePool.size()==0){
            //(W)eak, Attack(1), Attack(2)
            summonGreenLousePool.add("1W2");
            summonGreenLousePool.add("21W");
            Collections.shuffle(summonGreenLousePool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonGreenLousePool.remove(0);
    }


    public static ArrayList<String> summonSpikeSlimePool=new ArrayList<>();
    public static String getSummonSpikeSlime(){
        if(summonSpikeSlimePool.size()==0){
            summonSpikeSlimePool.add("2DV");
            summonSpikeSlimePool.add("V2D");
            summonSpikeSlimePool.add("DV2");
            summonSpikeSlimePool.add("VD2");
            Collections.shuffle(summonSpikeSlimePool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonSpikeSlimePool.remove(0);
    }

    public static ArrayList<String> summonLargeSlimePool=new ArrayList<>();
    public static String getSummonLargeSlime(){
        if(summonLargeSlimePool.size()==0){
            summonLargeSlimePool.add("4SW");
            summonLargeSlimePool.add("SW4");
            summonLargeSlimePool.add("W4S");
            summonLargeSlimePool.add("W4S");
            Collections.shuffle(summonLargeSlimePool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonLargeSlimePool.remove(0);
    }




    public static String[] pickTwoGremlins() {
        ArrayList<String> gremlinPool = new ArrayList<>();
        gremlinPool.add("BGGremlinAngry");
        gremlinPool.add("BGGremlinAngry");
        gremlinPool.add("BGGremlinSneaky");
        gremlinPool.add("BGGremlinSneaky");
        gremlinPool.add("BGGremlinFat");
        gremlinPool.add("BGGremlinFat");
        gremlinPool.add("BGGremlinWizard");
        gremlinPool.add("BGGremlinWizard");

        String[] retVal = new String[2];


        int index = AbstractDungeon.miscRng.random(gremlinPool.size() - 1);
        String key = gremlinPool.get(index);
        gremlinPool.remove(index);
        retVal[0] = key;

        index = AbstractDungeon.miscRng.random(gremlinPool.size() - 1);
        key = gremlinPool.get(index);
        gremlinPool.remove(index);
        retVal[1] = key;

        return retVal;
    }


    public BGExordium(AbstractPlayer p, ArrayList<String> emptyList) {
        //super(NAME, "BGExordium", p, emptyList);
        super(NAME, "Exordium", p, emptyList);

        //logger.info("BGExordium constructor, no savefile");

        initializeRelicList();

        if (Settings.isEndless) {
            if (floorNum <= 1) {
                blightPool.clear();
                blightPool = new ArrayList<>();
            }
        } else {
            blightPool.clear();
        }

        if (scene != null) {
            scene.dispose();
        }
        scene = (AbstractScene)new TheBottomScene();
        scene.randomizeScene();
        fadeColor = Color.valueOf("1e0f0aff");
        sourceFadeColor = Color.valueOf("1e0f0aff");


        initializeSpecialOneTimeEventList();


        initializeLevelSpecificChances();
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + AbstractDungeon.actNum));
        generateSpecialMap();

        CardCrawlGame.music.changeBGM(id);
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        if (true) {
            AbstractDungeon.currMapNode.room = (AbstractRoom)new NeowRoom(false);
            if (AbstractDungeon.floorNum > 1) {
                SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENDLESS_NEOW);
            } else {
                SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENTER_ROOM);
            }
        }

       // logger.info("BGExordium constructor, no savefile -- DONE");
        //logger.info("btw NextDungeon is "+CardCrawlGame.nextDungeon);
    }

    public BGExordium(AbstractPlayer p, SaveFile saveFile) {
        super(NAME, p, saveFile);
        CardCrawlGame.dungeon = this;
        if (scene != null) {
            scene.dispose();
        }
        scene = (AbstractScene)new TheBottomScene();
        fadeColor = Color.valueOf("1e0f0aff");
        sourceFadeColor = Color.valueOf("1e0f0aff");


        initializeLevelSpecificChances();
        miscRng = new Random(Long.valueOf(Settings.seed.longValue() + saveFile.floor_num));
        CardCrawlGame.music.changeBGM(id);
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + saveFile.act_num));
        generateSpecialMap();
        firstRoomChosen = true;
        populatePathTaken(saveFile);


        if (isLoadingIntoNeow(saveFile)) {
            AbstractDungeon.firstRoomChosen = false;
        }
    }



    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;


        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;


        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;


        colorlessRareChance = 0.3F;
        cardUpgradedChance = 0.0F;
    }

    protected void generateMonsters() {
        generateWeakEnemies(1);
        generateStrongEnemies(12);
        generateElites(3);
    }

    @Override
    /*     */   protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();

        //TODO: add BoardGame: tag in front of all monster IDs -- probably search-and-replace "BG -> "BoardGame:BG



        //monsters.add(new MonsterInfo("BoardGame:TheGuardian", 2000.0F));
        //monsters.add(new MonsterInfo("BoardGame:Hexaghost", 2000.0F));
        //monsters.add(new MonsterInfo("BoardGame:SlimeBoss", 2000.0F));
        monsters.add(new MonsterInfo("BoardGame:Easy Small Slimes", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Jaw Worm (Easy)", 2.0F));
        monsters.add(new MonsterInfo(BGCultist.ID,2.0F));
        monsters.add(new MonsterInfo("BoardGame:2 Louse", 2.0F));

        /* 159 */     MonsterInfo.normalizeWeights(monsters);
        /* 160 */     populateMonsterList(monsters, count, false);
        /*     */   }


    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("BoardGame:Cultist and SpikeSlime", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Cultist and Louse", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Fungi Beasts", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Slime Trio", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:3 Louse (Hard)", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Large Slime",2.0F));
        monsters.add(new MonsterInfo("BoardGame:Sneaky Gremlin Team",2.0F));
        monsters.add(new MonsterInfo("BoardGame:Angry Gremlin Team",2.0F));
        monsters.add(new MonsterInfo("BoardGame:Blue Slaver",2.0F));
        monsters.add(new MonsterInfo("BoardGame:Red Slaver",2.0F));
        monsters.add(new MonsterInfo("BoardGame:Looter",2.0F));
        monsters.add(new MonsterInfo("BoardGame:Jaw Worm (Medium)", 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        populateFirstStrongEnemy(monsters, generateExclusions());
        populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        //TODO: same elite can incorrectly be encountered twice (not in a row) -- update to use deck system
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("BoardGame:Gremlin Nob", 1.0F));
        monsters.add(new MonsterInfo("BoardGame:Lagavulin", 1.0F));
        monsters.add(new MonsterInfo("BoardGame:3 Sentries", 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        populateMonsterList(monsters, count, true);
    }

    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        return retVal;
    }


    protected void initializeBoss() {
        bossList.clear();

        bossList.add("The Guardian");
        bossList.add("Hexaghost");
        bossList.add("Slime Boss");
        Collections.shuffle(bossList, new java.util.Random(monsterRng.randomLong()));

    }


    protected void initializeEventList() {
//        eventList.add("Big Fish");
//        eventList.add("The Cleric");
//        eventList.add("Dead Adventurer");
//        eventList.add("Golden Idol");
//        eventList.add("Golden Wing");
//        eventList.add("World of Goop");
//        eventList.add("Liars Game");
//        eventList.add("Living Wall");
//        eventList.add("Mushrooms");
//        eventList.add("Scrap Ooze");
//        eventList.add("Shining Light");
    }


    protected void initializeShrineList() {
//        shrineList.add("Match and Keep!");
//        shrineList.add("Golden Shrine");
//        shrineList.add("Transmorgrifier");
//        shrineList.add("Purifier");
//        shrineList.add("Upgrade Shrine");
//        shrineList.add("Wheel of Change");
    }

    public void initializeSpecialOneTimeEventList() {
//
    }


    protected void initializeEventImg() {
        if (eventBackgroundImg != null) {
            eventBackgroundImg.dispose();
            eventBackgroundImg = null;
        }
        eventBackgroundImg = ImageMaster.loadImage("images/ui/event/panel.png");
    }

    private static MapRoomNode getMapRoomNode(int x, int y, char symbol){
        //logger.info("getMapRoomNode "+x+" "+y+" "+symbol);
        //TODO: this function can be moved to AbstractBGDungeon

        MapRoomNode node = new MapRoomNode(x, y);
        switch(symbol) {
//            case '!':     DO NOT PLACE TREASUREROOMBOSS ON MAP!!
//                node.room = (AbstractRoom) new TreasureRoomBoss();
//                break;
            case 'B':
                node.room = (AbstractRoom) new MonsterRoomBoss();
                break;
            case 'R':
                node.room = (AbstractRoom) new RestRoom();
                break;
            case '$':
                node.room = (AbstractRoom) new ShopRoom();
                break;
            case '?':
                node.room = (AbstractRoom) new EventRoom();
                break;
            case 'M':
                node.room = (AbstractRoom) new MonsterRoom();
                break;
            case 'T':
                node.room = (AbstractRoom) new TreasureRoom();
                break;
            case 'E':
                node.room = (AbstractRoom) new MonsterRoomElite();
                break;
            case '.':
                //empty node, no room
                break;
            case 'D':
                return getMapRoomNode(x,y,getDarkTokenSymbol());
            case 'L':
                return getMapRoomNode(x,y,getLightTokenSymbol());


        }
        return node;
    }
    protected static void generateSpecialMap() {
        //TODO: Act 1 has a 2nd map layout, 50-50 chance



        long startTime = System.currentTimeMillis();
        shuffleMapTokens();
        map = new ArrayList<>();
        //Do not place TreasureRoomBoss on map!
        // creating it makes the game think that the current act is complete and messes everything up
        ArrayList<String>quickmap;
        ArrayList<String[]>connections;
        ArrayList<ArrayList<MapRoomNode>> rows = new ArrayList<ArrayList<MapRoomNode>>();

        if(AbstractDungeon.mapRng.random(1,2)==1) {

            quickmap = new ArrayList<String>(Arrays.asList(
                    "...B...",
                    ".RR.RR.",
                    "D.$.D.?",
                    "L.M.?.L",
                    ".D..L.D",
                    "M.?.D.M",
                    "T..T.T.",
                    ".L..L.D",
                    "D.M.D.L",
                    "M.L.?.?",
                    "?..M..M",
                    ".?.?.?.",
                    "...M..."));


            for (int i = 0; i < quickmap.size(); i += 1) {
                rows.add(new ArrayList<MapRoomNode>());
            }

            for (int i = 0, y = rows.size() - 1; y >= 0; i += 1, y -= 1) {
                //logger.info("Map: "+" "+i+" "+quickmap.get(y));
                for (int x = 0; x <= 6; x += 1) {
                    //logger.info("Node: "+" "+x+" "+i+" "+quickmap.get(y).charAt(x));
                    rows.get(i).add(getMapRoomNode(x, i, quickmap.get(y).charAt(x)));
                }
            }

            connections = new ArrayList<String[]>();
            connections.add(new String[]{"0-3", "1-1", "1-3", "1-5"});
            connections.add(new String[]{"1-1", "2-0"});
            connections.add(new String[]{"1-3", "2-3"});
            connections.add(new String[]{"1-5", "2-6"});
            connections.add(new String[]{"2-0", "3-0"});
            connections.add(new String[]{"2-3", "3-2"});
            connections.add(new String[]{"2-3", "3-4"});
            connections.add(new String[]{"2-6", "3-6"});
            connections.add(new String[]{"3-0", "4-0", "4-2"});
            connections.add(new String[]{"3-2", "4-2", "4-4"});
            connections.add(new String[]{"3-4", "4-4"});
            connections.add(new String[]{"3-6", "4-6"});
            connections.add(new String[]{"4-0", "5-1"});
            connections.add(new String[]{"4-2", "5-4"});
            connections.add(new String[]{"4-4", "5-4"});
            connections.add(new String[]{"4-6", "5-6"});
            connections.add(new String[]{"5-1", "6-0", "6-3"});
            connections.add(new String[]{"5-4", "6-3", "6-5"});
            connections.add(new String[]{"5-6", "6-5"});
            connections.add(new String[]{"6-0", "7-0"});
            connections.add(new String[]{"6-3", "7-2", "7-4"});
            connections.add(new String[]{"6-5", "7-4", "7-6"});
            connections.add(new String[]{"7-0", "8-1"});
            connections.add(new String[]{"7-2", "8-1"});
            connections.add(new String[]{"7-4", "8-4"});
            connections.add(new String[]{"7-6", "8-6"});
            connections.add(new String[]{"8-1", "9-0", "9-2"});
            connections.add(new String[]{"8-4", "9-2", "9-4"});
            connections.add(new String[]{"8-6", "9-6"});
            connections.add(new String[]{"9-0", "10-0"});
            connections.add(new String[]{"9-2", "10-2"});
            connections.add(new String[]{"9-4", "10-4"});
            connections.add(new String[]{"9-6", "10-4", "10-6"});
            connections.add(new String[]{"10-0", "11-1"});
            connections.add(new String[]{"10-2", "11-2"});
            connections.add(new String[]{"10-4", "11-4"});
            connections.add(new String[]{"10-6", "11-5"});
            connections.add(new String[]{"11-1", "12-3"});
            connections.add(new String[]{"11-2", "12-3"});
            connections.add(new String[]{"11-4", "12-3"});
            connections.add(new String[]{"11-5", "12-3"});
            //connections.add(new String[]{"12-3","13-3"});
        }else{
            quickmap = new ArrayList<String>(Arrays.asList(
                    "...B...",
                    ".RR.RR.",
                    "?.D.D.?",
                    "E.L.?.D",
                    "L.?.L.L",
                    ".M.D.D.",
                    ".T.TT.T",
                    "?.D.M.D",
                    "D.M.L.L",
                    "$.L.?.M",
                    "M..M..?",
                    ".?.?.?.",
                    "...M..."));

            for (int i = 0; i < quickmap.size(); i += 1) {
                rows.add(new ArrayList<MapRoomNode>());
            }

            for (int i = 0, y = rows.size() - 1; y >= 0; i += 1, y -= 1) {
                //logger.info("Map: "+" "+i+" "+quickmap.get(y));
                for (int x = 0; x <= 6; x += 1) {
                    //logger.info("Node: "+" "+x+" "+i+" "+quickmap.get(y).charAt(x));
                    rows.get(i).add(getMapRoomNode(x, i, quickmap.get(y).charAt(x)));
                }
            }

            connections = new ArrayList<String[]>();
            connections.add(new String[]{"0-3", "1-1", "1-3", "1-5"});
            connections.add(new String[]{"1-1", "2-0"});
            connections.add(new String[]{"1-3", "2-3"});
            connections.add(new String[]{"1-5", "2-6"});
            connections.add(new String[]{"2-0", "3-0", "3-2"});
            connections.add(new String[]{"2-3", "3-2", "3-4"});
            connections.add(new String[]{"2-6", "3-6"});
            connections.add(new String[]{"3-0", "4-0"});
            connections.add(new String[]{"3-2", "4-2"});
            connections.add(new String[]{"3-4", "4-4"});
            connections.add(new String[]{"3-6", "4-6"});
            connections.add(new String[]{"4-0", "5-0"});
            connections.add(new String[]{"4-2", "5-0", "5-2"});
            connections.add(new String[]{"4-4", "5-2","5-4"});
            connections.add(new String[]{"4-6", "5-4", "5-6"});
            connections.add(new String[]{"5-0", "6-1"});
            connections.add(new String[]{"5-2", "6-1"});
            connections.add(new String[]{"5-4", "6-3", "6-4"});
            connections.add(new String[]{"5-6", "6-6"});
            connections.add(new String[]{"6-1", "7-1","7-3"});
            connections.add(new String[]{"6-3", "7-3"});
            connections.add(new String[]{"6-4", "7-5"});
            connections.add(new String[]{"6-6", "7-5"});
            connections.add(new String[]{"7-1", "8-0","8-2"});
            connections.add(new String[]{"7-3", "8-2","8-4"});
            connections.add(new String[]{"7-5", "8-4","8-6"});
            connections.add(new String[]{"8-0", "9-0"});
            connections.add(new String[]{"8-2", "9-2"});
            connections.add(new String[]{"8-4", "9-4"});
            connections.add(new String[]{"8-6", "9-6"});
            connections.add(new String[]{"9-0", "10-0"});
            connections.add(new String[]{"9-2", "10-2","10-4"});
            connections.add(new String[]{"9-4", "10-4"});
            connections.add(new String[]{"9-6", "10-6"});
            connections.add(new String[]{"10-0", "11-1"});
            connections.add(new String[]{"10-2", "11-2"});
            connections.add(new String[]{"10-4", "11-4"});
            connections.add(new String[]{"10-6", "11-5"});
            connections.add(new String[]{"11-1", "12-3"});
            connections.add(new String[]{"11-2", "12-3"});
            connections.add(new String[]{"11-4", "12-3"});
            connections.add(new String[]{"11-5", "12-3"});

        }

        for(int i=0;i<connections.size();i+=1){
            String[] base=connections.get(i)[0].split("-");
            int y1=Integer.valueOf(base[0]);
            int x1=Integer.valueOf(base[1]);
            for(int j=1;j<connections.get(i).length;j+=1) {
                String[] dest=connections.get(i)[j].split("-");
                int y2=Integer.valueOf(dest[0]);
                int x2=Integer.valueOf(dest[1]);
                connectNode(rows.get(y1).get(x1),
                        rows.get(y2).get(x2));
            }
        }


        MapRoomNode bossNode=rows.get(12).get(3);
        int[] cols={1,2,4,5};
        for(int i=0;i<cols.length;i+=1) {
            MapRoomNode restNode=rows.get(11).get(cols[i]);
            rows.get(11).get(cols[i]).addEdge(new MapEdge(restNode.x, restNode.y, restNode.offsetX, restNode.offsetY, bossNode.x, bossNode.y, bossNode.offsetX, bossNode.offsetY, false));
        }

        for(int i=0;i<rows.size();i+=1){
            map.add(rows.get(i));
        }


        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, Boolean.valueOf(true)));
        logger.info("Game Seed: " + Settings.seed);
        logger.info("Map generation time: " + (System.currentTimeMillis() - startTime) + "ms");
        firstRoomChosen = false;

        fadeIn();
        setEmeraldElite();

    }

    private static void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }





    @SpirePatch2(clz = DungeonMap.class, method = "update",
            paramtypez={})
    public static class DungeonMapBossNodePatch {
        @SpirePostfixPatch
        public static void update(DungeonMap __instance) {
            final Logger logger = LogManager.getLogger(BGExordium.class.getName());


            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMPLETE && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP &&
                    (AbstractDungeon.getCurrMapNode().y == 11 && CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGExordium)) {
                if (__instance.bossHb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {

                    (AbstractDungeon.getCurrMapNode()).taken = true;
                    MapRoomNode node2 = AbstractDungeon.getCurrMapNode();
                    for (MapEdge e : node2.getEdges()) {
                        if (e != null) {
                            e.markAsTaken();
                        }
                    }

                    InputHelper.justClickedLeft = false;
                    CardCrawlGame.music.fadeOutTempBGM();
                    MapRoomNode node = new MapRoomNode(-1, 15);
                    node.room = (AbstractRoom) new MonsterRoomBoss();
                    AbstractDungeon.nextRoom = node;

                    if (AbstractDungeon.pathY.size() > 1) {
                        AbstractDungeon.pathX.add(AbstractDungeon.pathX.get(AbstractDungeon.pathX.size() - 1));
                        AbstractDungeon.pathY.add(Integer.valueOf(((Integer) AbstractDungeon.pathY.get(AbstractDungeon.pathY.size() - 1)).intValue() + 1));
                    } else {
                        AbstractDungeon.pathX.add(Integer.valueOf(1));
                        AbstractDungeon.pathY.add(Integer.valueOf(15));
                    }

                    AbstractDungeon.nextRoomTransitionStart();
                    __instance.bossHb.hovered = false;
                }
            }
        }
    }


    @SpirePatch2(clz = DungeonMap.class, method = "calculateMapSize",
            paramtypez={})
    public static class calculateMapSizePatch {
        @SpirePostfixPatch
        public static float calculateMapSize(float __result) {
            final Logger logger = LogManager.getLogger(BGExordium.class.getName());
            if (CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGExordium) {
                return Settings.MAP_DST_Y * 12.75F - 1380.0F * Settings.scale;
            }
            return __result;
        }

    }

    public static class DungeonMapScreenExordiumScrollPatch{
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static void open(DungeonMapScreen __instance, boolean doScrollingAnimation, @ByRef float[] ___mapScrollUpperLimit) {
            //TODO: this doesn't work for act1 -- dungeon is probably null at this point
            if(CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGExordium){
                ___mapScrollUpperLimit[0]= (float) (-2300*Settings.scale*0.75);
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class,"releaseCard");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


}


