package CoopBoardGame.neow;

import CoopBoardGame.dungeons.AbstractBGDungeon;
import CoopBoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
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

    @SpirePatch2(
        clz = DungeonTransitionScreen.class,
        method = "setAreaName",
        paramtypez = { String.class }
    )
    public static class NeverSkipNeowInBG {

        @SpirePrefixPatch
        public static void Foo(String key) {
            if (CardCrawlGame.chosenCharacter == MultiCharacter.Enums.BG_MULTICHARACTER) {
                TipTracker.pref.putBoolean("NEOW_SKIP", true);
                TipTracker.pref.flush();
                TipTracker.refresh();
            }
        }
    }
}
