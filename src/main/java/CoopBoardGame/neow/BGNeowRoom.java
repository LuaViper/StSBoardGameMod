package CoopBoardGame.neow;

import CoopBoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
public class BGNeowRoom {

    @SpirePatch(
        clz = NeowRoom.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { boolean.class }
    )
    public static class BGNeowRoomPatch {

        public BGNeowRoomPatch() {}

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(NeowRoom room, boolean isDone) {
            if (
                CardCrawlGame.dungeon != null && CardCrawlGame.dungeon instanceof AbstractBGDungeon
            ) {
                room.phase = AbstractRoom.RoomPhase.EVENT;
                room.event = new BGNeowEvent(isDone);
                room.event.onEnterRoom();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
