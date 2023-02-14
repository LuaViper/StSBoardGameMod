package BoardGame.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGLab
        extends AbstractImageEvent {
    public static final String ID = "BGLab";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("Lab");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private enum CUR_SCREEN {
        INTRO, COMPLETE;
    }

    public BGLab() {
        super(NAME, DIALOG_1, "images/events/lab.jpg");
        this.noCardsInRewards = true;
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_LAB");
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {

            case INTRO:
                GenericEventDialog.hide();
                (AbstractDungeon.getCurrRoom()).rewards.clear();

                (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                int random = AbstractDungeon.miscRng.random(1, 6);
                if(random>=4) {
                    (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                }

                this.screen = CUR_SCREEN.COMPLETE;
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                AbstractDungeon.combatRewardScreen.open();
                logMetric("Lab", "Got Potions");
                break;
            case COMPLETE:
                openMap();
                break;
        }
    }
}


