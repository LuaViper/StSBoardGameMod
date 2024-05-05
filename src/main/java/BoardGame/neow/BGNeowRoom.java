package BoardGame.neow;


import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.dungeons.BGTheEnding;
import BoardGame.multicharacter.MultiCharacter;
import BoardGame.patches.DefaultInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


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



    @SpirePatch2(clz= DungeonTransitionScreen.class, method="setAreaName",
            paramtypez={String.class})
    public static class NeverSkipNeowInBG{
        @SpirePrefixPatch
        public static void Foo(String key) {
            if(CardCrawlGame.chosenCharacter == MultiCharacter.Enums.BG_MULTICHARACTER){
                TipTracker.pref.putBoolean("NEOW_SKIP",true);
                TipTracker.pref.flush();
                TipTracker.refresh();
            }

        }
    }


}