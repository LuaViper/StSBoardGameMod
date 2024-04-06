package BoardGame.dungeons;

import BoardGame.monsters.bgexordium.BGCultist;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.scenes.TheBeyondScene;
import com.megacrit.cardcrawl.scenes.TheCityScene;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BGTheBeyond
        extends AbstractBGDungeon
{

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("TheBeyond");
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String NAME = TEXT[0];
    public static final String ID = "TheBeyond";



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
    public static ArrayList<String> summonSpikerPool=new ArrayList<>();
    public static String getSummonSpiker(){
        if(summonSpikerPool.size()==0){
            //(2)dmg, (T)horns
            summonSpikerPool.add("2T");
            summonSpikerPool.add("T2");
            Collections.shuffle(summonSpikerPool, new java.util.Random(monsterRng.randomLong()));
        }
        return summonSpikerPool.remove(0);
    }

    public BGTheBeyond(AbstractPlayer p, ArrayList<String> emptyList) {
        //super(NAME, "BGExordium", p, emptyList);
        super(NAME, "TheBeyond", p, emptyList);

        //logger.info("BGTheBeyond constructor, no savefile");

        if (scene != null) {
            scene.dispose();
        }
        scene = (AbstractScene)new TheBeyondScene();
        fadeColor = Color.valueOf("140a1eff");
        sourceFadeColor = Color.valueOf("140a1eff");

        initializeLevelSpecificChances();
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + (AbstractDungeon.actNum * 200)));
        generateSpecialMap();

        CardCrawlGame.music.changeBGM(id);
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = (AbstractRoom)new EmptyRoom();

        logger.info("Shuffling reward deck...");
        rewardDeck.shuffle(cardRng);
    }

    public BGTheBeyond(AbstractPlayer p, SaveFile saveFile) {
        super(NAME, p, saveFile);

        if (scene != null) {
            scene.dispose();
        }
        scene = (AbstractScene)new TheBeyondScene();
        fadeColor = Color.valueOf("140a1eff");
        sourceFadeColor = Color.valueOf("140a1eff");

        initializeLevelSpecificChances();
        miscRng = new Random(Long.valueOf(Settings.seed.longValue() + saveFile.floor_num));
        CardCrawlGame.music.changeBGM(id);
        mapRng = new Random(Long.valueOf(Settings.seed.longValue() + (saveFile.act_num * 200)));
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
        generateStrongEnemies(10);
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
        monsters.add(new MonsterInfo("BoardGame:Jaw Worms (Hard)", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Spire Growth", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Orb Walker v2.3", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Orb Walker v3.2", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Transient", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Maw", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:Writhing Mass", 2.0F));
        monsters.add(new MonsterInfo("BoardGame:3 Darklings", 2.0F));
        if(AbstractDungeon.ascensionLevel<7) {
            monsters.add(new MonsterInfo("BoardGame:Exploder and Friends", 2.0F));
            monsters.add(new MonsterInfo("BoardGame:Repulsor and Friends", 2.0F));
        }else{
            monsters.add(new MonsterInfo("BoardGame:A7 Exploder and Friends", 2.0F));
            monsters.add(new MonsterInfo("BoardGame:A7 Repulsor and Friends", 2.0F));
        }

        MonsterInfo.normalizeWeights(monsters);
        //populateFirstStrongEnemy(monsters, generateExclusions());
        populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("BoardGame:Giant Head", 1.0F));
        monsters.add(new MonsterInfo("BoardGame:Nemesis", 1.0F));
        monsters.add(new MonsterInfo("BoardGame:Reptomancer", 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        populateMonsterList(monsters, count, true);
    }

    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        return retVal;
    }


    protected void initializeBoss() {
        bossList.clear();

        bossList.add("Awakened One");
        bossList.add("Time Eater");
        bossList.add("Donu and Deca");
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
                "D.D.$D.",
                ".LL.L.?",
                "D.MDD.D",
                "?D.?.LL",
                ".L.L.M.",
                ".?.M.?.",
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
        connections.add(new String[]{"2-1","3-0","3-1","3-3"});
        connections.add(new String[]{"2-3","3-3"});
        connections.add(new String[]{"2-5","3-5","3-6"});
        connections.add(new String[]{"3-0","4-0"});
        connections.add(new String[]{"3-1","4-2"});
        connections.add(new String[]{"3-3","4-2","4-3"});
        connections.add(new String[]{"3-5","4-3","4-4"});
        connections.add(new String[]{"3-6","4-6"});
        connections.add(new String[]{"4-0","5-1"});
        connections.add(new String[]{"4-2","5-1"});
        connections.add(new String[]{"4-3","5-2","5-4"});
        connections.add(new String[]{"4-4","5-4"});
        connections.add(new String[]{"4-6","5-6"});
        connections.add(new String[]{"5-1","6-0","6-2"});
        connections.add(new String[]{"5-2","6-2"});
        connections.add(new String[]{"5-4","6-4","6-5"});
        connections.add(new String[]{"5-6","6-5"});
        connections.add(new String[]{"6-0","7-1"});
        connections.add(new String[]{"6-2","7-2"});
        connections.add(new String[]{"6-4","7-4"});
        connections.add(new String[]{"6-5","7-5"});
        connections.add(new String[]{"7-1","8-3"});
        connections.add(new String[]{"7-2","8-3"});
        connections.add(new String[]{"7-4","8-3"});
        connections.add(new String[]{"7-5","8-3"});


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
                    (AbstractDungeon.getCurrMapNode().y == 7 && CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheBeyond)) {
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
            final Logger logger = LogManager.getLogger(BGTheBeyond.class.getName());
            if (CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheBeyond) {
                return Settings.MAP_DST_Y * 9.75F * (8.125F/9.0F) - 1380.0F * Settings.scale;
            }
            return __result;
        }
    }

    @SpirePatch2(clz= DungeonMapScreen.class, method="open",
            paramtypez={boolean.class})
    public static class DungeonMapScreenCityScrollPatch{
        @SpireInsertPatch(
                locator= BGTheBeyond.DungeonMapScreenCityScrollPatch.Locator.class,
                localvars={}
        )
        public static void open(DungeonMapScreen __instance, boolean doScrollingAnimation, @ByRef float[] ___mapScrollUpperLimit) {
            if(CardCrawlGame.dungeon!=null && CardCrawlGame.dungeon instanceof BGTheBeyond){
                ___mapScrollUpperLimit[0]= (float) (-2300*Settings.scale*0.45* (8.5/9.0F));
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
