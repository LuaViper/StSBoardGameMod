package BoardGame.dungeons;

import BoardGame.monsters.bgexordium.*;
import BoardGame.neow.BGNeowQuickStart;
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
import com.megacrit.cardcrawl.scenes.TheCityScene;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BGTheCity
        extends AbstractBGDungeon
{

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("TheCity");
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String NAME = TEXT[0];
    public static final String ID = "TheCity";




    public static ArrayList<String> darkMapTokenPool=new ArrayList<>();
    public static ArrayList<String> lightMapTokenPool=new ArrayList<>();
    public static void shuffleMapTokens(){  //TODO: move to AbstractBGDungeon
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
    public static ArrayList<String> summonHealerPool=new ArrayList<>();
    public static String getSummonHealer(){
        if(summonHealerPool.size()==0){
            //(2)dmg (weak), (H)eal, (S)tr
            summonHealerPool.add("H2S");
            summonHealerPool.add("2SH");
            Collections.shuffle(summonHealerPool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonHealerPool.remove(0);
    }

    public static ArrayList<String> summonByrdPool=new ArrayList<>();
    public static String getSummonByrd(){
        if(summonByrdPool.size()==0){
            //(3)dmg, (1)+1, (S)tr
            summonByrdPool.add("S13");
            summonByrdPool.add("S31");
            summonByrdPool.add("31S");
            Collections.shuffle(summonByrdPool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonByrdPool.remove(0);
    }

    public static ArrayList<String> summonBlueSlaverPool=new ArrayList<>();
    public static String getSummonBlueSlaver(){
        if(summonBlueSlaverPool.size()==0){
            //(3)dmg, (W)eak, (d)aze
            summonBlueSlaverPool.add("Wd3");
            summonBlueSlaverPool.add("W3d");
            summonBlueSlaverPool.add("dW3");
            summonBlueSlaverPool.add("3Wd");
            Collections.shuffle(summonBlueSlaverPool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonBlueSlaverPool.remove(0);
    }

    public static ArrayList<String> summonRedSlaverPool=new ArrayList<>();
    public static String getSummonRedSlaver(){
        if(summonRedSlaverPool.size()==0){
            //(3)dmg, (V)uln, (D)aze
            summonRedSlaverPool.add("3VD");
            summonRedSlaverPool.add("3DV");
            summonRedSlaverPool.add("DV3");
            summonRedSlaverPool.add("V3D");
            Collections.shuffle(summonRedSlaverPool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonRedSlaverPool.remove(0);
    }

    public static ArrayList<String> summonBronzeOrbPool=new ArrayList<>();
    public static String getSummonBronzeOrb(){
        if(summonBronzeOrbPool.size()==0){
            //(3)dmg, (D)aze, (B)lock
            summonBronzeOrbPool.add("3DB");
            summonBronzeOrbPool.add("3BD");
            summonBronzeOrbPool.add("DB3");
            summonBronzeOrbPool.add("B3D");
            Collections.shuffle(summonBronzeOrbPool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonBronzeOrbPool.remove(0);
    }

    public BGTheCity(AbstractPlayer p, ArrayList<String> emptyList) {
        //super(NAME, "BGExordium", p, emptyList);
        super(NAME, "TheCity", p, emptyList);

        //logger.info("BGTheCity constructor, no savefile");

        if (scene != null) {
            scene.dispose();
        }
        scene = (AbstractScene)new TheCityScene();
        fadeColor = Color.valueOf("0a1e1eff");
        sourceFadeColor = Color.valueOf("0a1e1eff");

        initializeLevelSpecificChances();
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + (AbstractDungeon.actNum * 100)));
        generateSpecialMap();

        CardCrawlGame.music.changeBGM(id);
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = (AbstractRoom)new EmptyRoom();

        logger.info("Shuffling reward deck...");
        rewardDeck.shuffle(cardRng);
    }

    public BGTheCity(AbstractPlayer p, SaveFile saveFile) {
        super(NAME, p, saveFile);

        if (scene != null) {
            scene.dispose();
        }
        scene = (AbstractScene)new TheCityScene();
        fadeColor = Color.valueOf("0a1e1eff");
        sourceFadeColor = Color.valueOf("0a1e1eff");

        initializeLevelSpecificChances();
        miscRng = new Random(Long.valueOf(Settings.seed.longValue() + saveFile.floor_num));
        CardCrawlGame.music.changeBGM(id);
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + (saveFile.act_num * 100)));
        generateSpecialMap();
        firstRoomChosen = true;

        populatePathTaken(saveFile);

        //TODO: is this correct?  or are we reshuffling an already shuffled saved deck?
        logger.info("Shuffling reward deck...");
        rewardDeck.shuffle(cardRng);
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
        generateWeakEnemies(0);
        generateStrongEnemies(12);
        generateElites(3);
    }

    @Override
    /*     */   protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();


        monsters.add(new MonsterInfo("BoardGame:Easy Small Slimes", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Jaw Worm (Easy)", 2.0F));
        monsters.add(new MonsterInfo(BGCultist.ID,2.0F));
        monsters.add(new MonsterInfo("BoardGame:2 Louse", 2.0F));

        /* 159 */     MonsterInfo.normalizeWeights(monsters);
        /* 160 */     populateMonsterList(monsters, count, false);
        /*     */   }


    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("BoardGame:Snecko", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:SnakePlant", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:3 Byrds", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:3 Cultists", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Chosen and Cultist", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Chosen and Byrd", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Looter (Hard)", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Another Looter (Hard)", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Centurion A", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Centurion B", 2.0F));
        if(AbstractDungeon.ascensionLevel<7) {
            monsters.add(new MonsterInfo("BoardGame:Shelled Parasite", 2.0F));
            monsters.add(new MonsterInfo("BoardGame:SphericGuardian", 2.0F));
        }else{
            monsters.add(new MonsterInfo("BoardGame:A7 Shelled Parasite and Fungi Beast", 2.0F));
            monsters.add(new MonsterInfo("BoardGame:A7 Spheric Guardian and Sentry A", 2.0F));
        }
        MonsterInfo.normalizeWeights(monsters);
        //populateFirstStrongEnemy(monsters, generateExclusions());
        populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("BoardGame:Book of Stabbing", 1.0F));
        monsters.add(new MonsterInfo("BoardGame:Gremlin Leader", 1.0F));
        monsters.add(new MonsterInfo("BoardGame:Taskmaster", 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        populateMonsterList(monsters, count, true);
    }

    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        return retVal;
    }


    protected void initializeBoss() {
        bossList.clear();

        bossList.add("Automaton");
        bossList.add("Collector");
        bossList.add("Champ");
        Collections.shuffle(bossList, new java.util.Random(monsterRng.randomLong()));
    }


    protected void initializeEventList() {
    }


    protected void initializeShrineList() {
    }

    public void initializeSpecialOneTimeEventList() {
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


        long startTime = System.currentTimeMillis();

        shuffleMapTokens();

        map = new ArrayList<>();



        //Do not place TreasureRoomBoss on map!
        // creating it makes the game think that the current act is complete and messes everything up
        ArrayList<String>quickmap= new ArrayList<String>(Arrays.asList(
                "...B...",
                ".RR.RR.",
                "D.M.?.E",
                "?.L.?.?",
                "DD.D.LL",
                "L.L.D.D",
                "D.D.M.L",
                ".$.L.M.",
                ".M.?.?.",
                "...M..."));



        ArrayList<ArrayList<MapRoomNode>>rows=new ArrayList<ArrayList<MapRoomNode>>();
        for(int i=0;i<quickmap.size();i+=1){
            rows.add(new ArrayList<MapRoomNode>());
        }


        for(int i=0,y=rows.size()-1;y>=0;i+=1,y-=1){
            //logger.info("Map: "+" "+i+" "+quickmap.get(y));
            for(int x=0;x<=6;x+=1) {
                //logger.info("Node: "+" "+x+" "+i+" "+quickmap.get(y).charAt(x));
                rows.get(i).add(getMapRoomNode(x, i, quickmap.get(y).charAt(x)));
            }
        }


        ArrayList<String[]>connections= new ArrayList<String[]>();
        connections.add(new String[]{"0-3","1-1","1-3","1-5"});
        connections.add(new String[]{"1-1","2-1"});
        connections.add(new String[]{"1-3","2-3"});
        connections.add(new String[]{"1-5","2-5"});
        connections.add(new String[]{"2-1","3-0"});
        connections.add(new String[]{"2-3","3-2","3-4"});
        connections.add(new String[]{"2-5","3-4","3-6"});
        connections.add(new String[]{"3-0","4-0","4-2"});
        connections.add(new String[]{"3-2","4-2"});
        connections.add(new String[]{"3-4","4-2","4-4"});
        connections.add(new String[]{"3-6","4-6"});
        connections.add(new String[]{"4-0","5-0","5-1"});
        connections.add(new String[]{"4-2","5-1","5-3"});
        connections.add(new String[]{"4-4","5-5"});
        connections.add(new String[]{"4-6","5-5","5-6"});
        connections.add(new String[]{"5-0","6-0"});
        connections.add(new String[]{"5-1","6-2"});
        connections.add(new String[]{"5-3","6-2","6-4"});
        connections.add(new String[]{"5-5","6-4"});
        connections.add(new String[]{"5-6","6-6"});
        connections.add(new String[]{"6-0","7-0"});
        connections.add(new String[]{"6-2","7-0","7-2"});
        connections.add(new String[]{"6-4","7-4","7-6"});
        connections.add(new String[]{"6-6","7-6"});
        connections.add(new String[]{"7-0","8-1"});
        connections.add(new String[]{"7-2","8-2"});
        connections.add(new String[]{"7-4","8-4"});
        connections.add(new String[]{"7-6","8-5"});
        connections.add(new String[]{"8-1","9-3"});
        connections.add(new String[]{"8-2","9-3"});
        connections.add(new String[]{"8-4","9-3"});
        connections.add(new String[]{"8-5","9-3"});
        //connections.add(new String[]{"12-3","13-3"});

        int bossPosition=rows.size()-1;
        int restBeforeBossPosition=bossPosition-1;

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

        MapRoomNode bossNode=rows.get(bossPosition).get(3);

        int[] cols={1,2,4,5};
        for(int i=0;i<cols.length;i+=1) {
            MapRoomNode restNode=rows.get(restBeforeBossPosition).get(cols[i]);
            rows.get(restBeforeBossPosition).get(cols[i]).addEdge(new MapEdge(restNode.x, restNode.y, restNode.offsetX, restNode.offsetY, bossNode.x, bossNode.y, bossNode.offsetX, bossNode.offsetY, false));
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
                    (AbstractDungeon.getCurrMapNode().y == 8 && CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheCity)) {
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
            final Logger logger = LogManager.getLogger(BGTheCity.class.getName());
            if (CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheCity) {
                return Settings.MAP_DST_Y * 9.75F - 1380.0F * Settings.scale;
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
            if(CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheCity){
                ___mapScrollUpperLimit[0]= (float) (-2300*Settings.scale*0.45);
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


