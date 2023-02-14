//package BoardGame.events;
//
//import BoardGame.neow.BGNeowEvent;
//import BoardGame.neow.BGNeowRoom;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.megacrit.cardcrawl.characters.AnimatedNpc;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.events.AbstractEvent;
//import com.megacrit.cardcrawl.events.AbstractImageEvent;
//import com.megacrit.cardcrawl.localization.EventStrings;
//import com.megacrit.cardcrawl.map.MapRoomNode;
//import com.megacrit.cardcrawl.neow.NeowRoom;
//import com.megacrit.cardcrawl.rooms.AbstractRoom;
//import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
//import com.megacrit.cardcrawl.vfx.FadeWipeParticle;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public class BGSetupPortal
//        //extends AbstractImageEvent {
//        extends AbstractEvent {
//    public static final String ID = "BGSetupPortal";
//    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGSetupPortal");
//    public static final String NAME = eventStrings.NAME;
//    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
//    public static final String[] OPTIONS = eventStrings.OPTIONS;
//
//    public static final String EVENT_CHOICE_TOOK_PORTAL = "Took Portal";
//    private static final String DIALOG_1 = DESCRIPTIONS[0];
//    private static final String DIALOG_2 = DESCRIPTIONS[1];
//    private static final String DIALOG_3 = DESCRIPTIONS[2];
//
//    public static boolean neowRoomBecomesSetupRoom=true;
//
//    private CurScreen screen = CurScreen.INTRO;
//
//    private AnimatedNpc npc;
//
//    private enum CurScreen {
//        INTRO, ACCEPT, LEAVE;
//    }
//
//    public BGSetupPortal() {
//        //super(NAME, DIALOG_1, "images/events/secretPortal.jpg");
//        super();
//        //this.imageEventText.setDialogOption(OPTIONS[0]);
//        //this.imageEventText.setDialogOption(OPTIONS[1]);
//        this.roomEventText.addDialogOption(OPTIONS[0]);
//        this.roomEventText.addDialogOption(OPTIONS[1]);
//
//        if (this.npc == null) {
//            this.npc = new AnimatedNpc(1534.0F * Settings.xScale, AbstractDungeon.floorY - 60.0F * Settings.yScale, "images/npcs/neow/skeleton.atlas", "images/npcs/neow/skeleton.json", "idle");
//        }
//    }
//
//
//    public void onEnterRoom() {
////        if (Settings.AMBIANCE_ON) {
////            CardCrawlGame.sound.play("EVENT_PORTAL");
////        }
//    }
//
//    protected void buttonEffect(int buttonPressed) {
//        MapRoomNode node;
//        switch (this.screen) {
//            case INTRO:
//                switch (buttonPressed) {
//                    case 0:
////                        this.imageEventText.updateBodyText(DIALOG_2);
//                        this.screen = CurScreen.ACCEPT;
////                        logMetric("SecretPortal", "Took Portal");
//                        this.roomEventText.updateDialogOption(0, OPTIONS[1]);
////                        CardCrawlGame.screenShake.mildRumble(5.0F);
////                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");
//                        break;
//                    case 1:
//                        this.roomEventText.updateBodyText(DIALOG_3);
//                        this.screen = CurScreen.LEAVE;
//                        logMetricIgnored("SecretPortal");
//                        this.roomEventText.updateDialogOption(0, OPTIONS[1]);
//                        break;
//                }
//
//
//                this.roomEventText.clearRemainingOptions();
//                return;
//
//            case ACCEPT:
//                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
//                node = new MapRoomNode(0, -1);
//                AbstractDungeon.currMapNode = node;
//                neowRoomBecomesSetupRoom=false;
//                node.room = (AbstractRoom)new NeowRoom(false);
//                AbstractDungeon.nextRoom = node;
//
//                //CardCrawlGame.music.fadeOutTempBGM();
//                //AbstractDungeon.pathX.add(Integer.valueOf(1));
//                //AbstractDungeon.pathY.add(Integer.valueOf(15));
//
//                //AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());  // <- probably safe to use
////                AbstractDungeon.nextRoomTransitionStart();                    // <- probably not safe to use
//                node.room.event.onEnterRoom();
//                return;
//        }
//        openMap();
//    }
//
//    public void render(SpriteBatch sb) {
//        this.npc.render(sb);
//    }
//
//    private static final Logger logger = LogManager.getLogger(BGSetupPortal.class.getName());
//    public void dispose() {
//        super.dispose();
//        if (this.npc != null) {
//            logger.info("Disposing Neow asset.");
//            this.npc.dispose();
//            this.npc = null;
//        }
//    }
//
//}
//
//
