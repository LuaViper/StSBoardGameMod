package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.MultiCharacter;
import CoopBoardGame.util.TogetherInSpireHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

/**
 * Patch to render row backgrounds in TogetherInSpire multiplayer mode
 * when using individual BG characters (not MultiCharacter).
 *
 * In MultiCharacter mode, row backgrounds are rendered in MultiCharacter.render().
 * This patch handles the case where multiple players join with individual BG characters.
 */
public class MultiplayerRowRenderPatch {

    @SpirePatch2(clz = AbstractRoom.class, method = "render")
    public static class RenderRowBackgroundsPatch {

        @SpirePrefixPatch
        public static void Prefix(AbstractRoom __instance, SpriteBatch sb) {
            // Only render in combat phase
            if (__instance.phase != AbstractRoom.RoomPhase.COMBAT) {
                return;
            }

            // Skip if player is MultiCharacter (it handles its own row rendering)
            if (AbstractDungeon.player instanceof MultiCharacter) {
                return;
            }

            // Only render in multiplayer BG mode
            if (!TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
                return;
            }

            // Render row backgrounds using the shared CombatRowManager
            MultiCharacter.combatRowManager.renderRowBackgrounds(sb);
        }
    }
}
