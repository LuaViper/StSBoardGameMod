package BoardGame.neow;


import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.patches.DefaultInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BGNeowRoom {

    @SpirePatch(clz = NeowRoom.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {boolean.class})
    public static class BGNeowRoomPatch {
        public BGNeowRoomPatch(){}
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(NeowRoom room, boolean isDone) {
            if (CardCrawlGame.dungeon != null && CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                final Logger logger = LogManager.getLogger(DefaultInsertPatch.class.getName());
                room.phase = AbstractRoom.RoomPhase.EVENT;
                //if(BGSetupPortal.neowRoomBecomesSetupRoom){
                    //room.event = new BGSetupPortal();
                //}else {
                    room.event = new BGNeowEvent(isDone);
                //}
                room.event.onEnterRoom();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

}