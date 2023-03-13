package BoardGame.events;

import BoardGame.relics.BGSecretPortalRelic;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

public class BGSecretPortal
        extends AbstractImageEvent {
    public static final String ID = "BGSecretPortal";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGSecretPortal");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    public static final String EVENT_CHOICE_TOOK_PORTAL = "Took Portal";
    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String DIALOG_3 = DESCRIPTIONS[2];

    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, ACCEPT, LEAVE;
    }

    public BGSecretPortal() {
        super(NAME, DIALOG_1, "images/events/secretPortal.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        //this.imageEventText.setDialogOption(OPTIONS[1]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_PORTAL");
        }
    }

    protected void buttonEffect(int buttonPressed) {
        MapRoomNode node;
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);
                        this.screen = CurScreen.ACCEPT;
                        logMetric("SecretPortal", "Took Portal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");
                        break;
//                    case 1:
//                        this.imageEventText.updateBodyText(DIALOG_3);
//                        this.screen = CurScreen.LEAVE;
//                        logMetricIgnored("SecretPortal");
//                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
//                        break;
                }

                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2),
                        new BGSecretPortalRelic());
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                this.imageEventText.clearRemainingOptions();
                return;

            case ACCEPT:

//                node = new MapRoomNode(-1, 15);
//                node.room = (AbstractRoom)new MonsterRoomBoss();
//                AbstractDungeon.nextRoom = node;
//                CardCrawlGame.music.fadeOutTempBGM();
//                AbstractDungeon.pathX.add(Integer.valueOf(1));
//                AbstractDungeon.pathY.add(Integer.valueOf(15));
//                AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
//                AbstractDungeon.nextRoomTransitionStart();
                //AbstractDungeon.player.getRelic("BGSecretPortalRelic");
                openMap();
                return;
        }
        openMap();
    }
}


