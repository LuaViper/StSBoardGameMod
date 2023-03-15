//package BoardGame.dungeons;
//
//
//import BoardGame.BoardGame;
//import BoardGame.events.FakeMonsterRoomEvent;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.helpers.ImageMaster;
//import com.megacrit.cardcrawl.rooms.AbstractRoom;
//import com.megacrit.cardcrawl.rooms.EventRoom;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//
//public class FakeMonsterRoomElite extends AbstractRoom
//{
//    Logger logger = LogManager.getLogger(FakeMonsterRoomElite.class.getName());
//    public FakeMonsterRoomElite() {
//        logger.info("FMRE constructor");
//        this.phase = RoomPhase.EVENT;
//        this.mapSymbol = "E";
//        this.mapImg = ImageMaster.MAP_NODE_ELITE;
//        this.mapImgOutline = ImageMaster.MAP_NODE_ELITE_OUTLINE;
//    }
//    @Override
//    public void onPlayerEntry() {
//        logger.info("FMRE onPlayerEntry");
//        AbstractDungeon.overlayMenu.proceedButton.hide();
//        this.event = new FakeMonsterRoomEvent();
//        this.event.onEnterRoom();
//    }
//
//    public void update() {
//        super.update();
//        if (!AbstractDungeon.isScreenUp) {
//            this.event.update();
//        }
//
//        if (this.event.waitTimer == 0.0F && !this.event.hasFocus && this.phase != RoomPhase.COMBAT) {
//            this.phase = RoomPhase.COMPLETE;
//            this.event.reopen();
//        }
//
//    }
//
//    public void render(SpriteBatch sb) {
//        if (this.event != null) {
//            this.event.render(sb);
//        }
//
//        super.render(sb);
//    }
//
//    public void renderAboveTopPanel(SpriteBatch sb) {
//        super.renderAboveTopPanel(sb);
//        if (this.event != null) {
//            this.event.renderAboveTopPanel(sb);
//        }
//
//    }
//
//}
//
//
