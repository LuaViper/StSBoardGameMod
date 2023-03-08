package BoardGame.relics;
import BoardGame.dungeons.BGTheCity;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGWingBoots extends AbstractBGRelic {
    public static final String ID = "BGWingedGreaves";

    private static final Logger logger = LogManager.getLogger(BGWingBoots.class.getName());

    public BGWingBoots() {
        super("BGWingedGreaves", "winged.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
        this.counter = 3;
    }
    public int getPrice() {return 7;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void setCounter(int setCounter) {
        this.counter = setCounter;
        if (this.counter == -2) {
            usedUp();
            //AbstractDungeon.player.loseRelic("BGWingedGreaves");
            this.counter = -2;
        }
    }


//    public boolean canSpawn() {
//        return (Settings.isEndless || AbstractDungeon.floorNum <= 40);
//    }


    public AbstractRelic makeCopy() {
        return new BGWingBoots();
    }

    @SpirePatch2(clz = MapRoomNode.class, method = "wingedIsConnectedTo",
            paramtypez = {MapRoomNode.class})
    public static class wingedIsConnectedToPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Prefix(MapRoomNode node, ArrayList<MapEdge> ___edges){

            for (MapEdge edge : ___edges) {
                //logger.info("wingedIsConnectedToPatch "+node.y+" "+edge.dstY+" "+AbstractDungeon.player.hasRelic("BGWingedGreaves"));
                if (node.y == edge.dstY && AbstractDungeon.player.hasRelic("BGWingedGreaves") &&
                        (AbstractDungeon.player.getRelic("BGWingedGreaves")).counter > 0) {
                    logger.info("return TRUE "+node.x+" "+node.y);
                    return SpireReturn.Return(true);
                }
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(clz = MapRoomNode.class, method = "update",
            paramtypez = {})
    public static class MapRoomNodeWingedBootsUpdatePatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars = {"normalConnection", "wingedConnection"}
        )
        public static SpireReturn<Void> Insert(boolean normalConnection, boolean wingedConnection){
            logger.info("MapRoomNodeWingedBootsUpdatePatch");
            if (!normalConnection && wingedConnection &&
                    AbstractDungeon.player.hasRelic("BGWingedGreaves")) {
                (AbstractDungeon.player.getRelic("BGWingedGreaves")).counter--;
                if ((AbstractDungeon.player.getRelic("BGWingedGreaves")).counter <= 0) {
                    AbstractDungeon.player.getRelic("BGWingedGreaves").setCounter(-2);
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class,"hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    @SpirePatch2(clz = DungeonMapScreen.class, method = "updateControllerInput",
            paramtypez = {})
    public static class DungeonMapScreenControllerInputPatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars = {"flightMatters"}
        )
        public static SpireReturn<Void> Insert(@ByRef boolean[] ___flightMatters){
            //logger.info("DungeonMapScreenControllerInputPatch");
            if(AbstractDungeon.player.hasRelic("BGWingedGreaves")) {
                ___flightMatters[0]=true;   //TODO: test if DungeonMapScreenControllerInputPatch actually works
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MapRoomNode.class,"isConnectedTo");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}


