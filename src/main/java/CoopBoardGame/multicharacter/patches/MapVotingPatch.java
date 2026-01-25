package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multiplayer.voting.RoomVotingManager;
import CoopBoardGame.ui.map.MapVoteRenderer;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Patches for the voting-based room selection system.
 * Intercepts map node clicks to cast votes instead of entering rooms,
 * and renders vote markers on the map.
 */
public class MapVotingPatch {

    private static final Logger logger = LogManager.getLogger(MapVotingPatch.class.getName());

    /**
     * Patch to intercept map node clicks in multiplayer board game mode.
     * Instead of entering the room immediately, cast a vote.
     */
    @SpirePatch2(clz = MapRoomNode.class, method = "update")
    public static class InterceptMapClickPatch {

        @SpireInsertPatch(
            locator = ClickLocator.class
        )
        public static SpireReturn<Void> Insert(MapRoomNode __instance) {
            // Only intercept in multiplayer board game mode
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return SpireReturn.Continue();
            }

            // Check if voting system should block normal room entry
            RoomVotingManager manager = RoomVotingManager.getInstance();
            if (!manager.shouldBlockRoomEntry()) {
                return SpireReturn.Continue();
            }

            // Cast vote instead of entering room
            logger.info("Intercepted map click, casting vote for node (" + __instance.x + ", " + __instance.y + ")");
            manager.castLocalVote(__instance);

            // Block the normal room entry
            return SpireReturn.Return();
        }

        /**
         * Locator to find the point where the node click is processed.
         * We look for where playNodeSelectedSound() is called or
         * where AbstractDungeon.nextRoom is set.
         */
        private static class ClickLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                // Find the line where the click sound is played
                // This indicates a valid click has been detected
                Matcher matcher = new Matcher.MethodCallMatcher(
                    MapRoomNode.class,
                    "playNodeSelectedSfx"
                );
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    /**
     * Patch to render vote markers on map nodes.
     */
    @SpirePatch2(clz = MapRoomNode.class, method = "render")
    public static class RenderVoteMarkersPatch {

        @SpirePostfixPatch
        public static void Postfix(MapRoomNode __instance, SpriteBatch sb) {
            // Only render in multiplayer board game mode
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            // Only render if voting is active
            RoomVotingManager manager = RoomVotingManager.getInstance();
            if (!manager.isVotingActive()) {
                return;
            }

            // Render vote markers for this node
            MapVoteRenderer.renderVotesForNode(sb, __instance);
        }
    }

    /**
     * Patch to activate voting when the dungeon map screen opens.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "open", paramtypez = {boolean.class})
    public static class ActivateVotingOnMapOpenPatch {

        @SpirePostfixPatch
        public static void Postfix(DungeonMapScreen __instance, boolean doScrollingAnimation) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                logger.info("Map screen opened, activating voting");
                RoomVotingManager.getInstance().activate();
            }
        }
    }

    /**
     * Patch to deactivate voting when the dungeon map screen closes.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "close")
    public static class DeactivateVotingOnMapClosePatch {

        @SpirePrefixPatch
        public static void Prefix(DungeonMapScreen __instance) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                logger.info("Map screen closing, deactivating voting");
                RoomVotingManager.getInstance().deactivate();
            }
        }
    }

    /**
     * Patch to update the voting manager each frame when on the map.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "update")
    public static class UpdateVotingManagerPatch {

        @SpirePostfixPatch
        public static void Postfix(DungeonMapScreen __instance) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                RoomVotingManager.getInstance().update(com.badlogic.gdx.Gdx.graphics.getDeltaTime());
            }
        }
    }

    /**
     * Patch to render the voting status overlay on the map screen.
     */
    @SpirePatch2(clz = DungeonMapScreen.class, method = "render")
    public static class RenderVotingStatusPatch {

        @SpirePostfixPatch
        public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb) {
            if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                RoomVotingManager manager = RoomVotingManager.getInstance();
                if (manager.isVotingActive()) {
                    MapVoteRenderer.renderVotingStatus(sb, manager);
                }
            }
        }
    }
}
