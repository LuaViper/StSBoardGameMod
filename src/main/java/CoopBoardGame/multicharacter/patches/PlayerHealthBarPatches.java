package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.grid.GridBackground;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class PlayerHealthBarPatches {

    //TODO LATER: note that we're technically rendering the health bar twice now; maybe don't do that
    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class RenderHealthBarAbovePlayer {

        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance, SpriteBatch sb) {
            if (GridBackground.isGridViewActive()) {
                if (
                    ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT ||
                        AbstractDungeon.getCurrRoom() instanceof
                            com.megacrit.cardcrawl.rooms.MonsterRoom) &&
                    !__instance.isDead
                ) {
                    __instance.renderHealth(sb);
                }
            }
        }
    }
}
