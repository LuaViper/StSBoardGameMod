package CoopBoardGame.multiplayer.patches;

import CoopBoardGame.multiplayer.rows.CombatRowManager;
import CoopBoardGame.multiplayer.rows.RowNetworkHelper;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

/**
 * Patch to render row backgrounds in TogetherInSpire multiplayer mode
 * when using individual BG characters.
 */
public class MultiplayerRowRenderPatch {

    // Shared instance of CombatRowManager for multiplayer mode
    public static CombatRowManager combatRowManager = new CombatRowManager();

    @SpirePatch2(clz = AbstractRoom.class, method = "render")
    public static class RenderRowBackgroundsPatch {

        @SpirePrefixPatch
        public static void Prefix(AbstractRoom __instance, SpriteBatch sb) {
            // Only render in combat phase
            if (__instance.phase != AbstractRoom.RoomPhase.COMBAT) {
                return;
            }

            // Host-only safety net: re-broadcast row assignments when roster changes mid-combat.
            RowNetworkHelper.hostCombatResyncTick();

            // Only render in multiplayer BG mode
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            // Render row backgrounds using the shared CombatRowManager
            combatRowManager.renderRowBackgrounds(sb);
        }
    }
}
