package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BGSensoryStone
        extends AbstractImageEvent {
    public static final String ID = "BGSensoryStone";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGSensoryStone");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String INTRO_TEXT = DESCRIPTIONS[0];
    private static final String INTRO_TEXT_2 = DESCRIPTIONS[1];
    private static final String MEMORY_1_TEXT = DESCRIPTIONS[2];
    private static final String MEMORY_2_TEXT = DESCRIPTIONS[3];
    private static final String MEMORY_3_TEXT = DESCRIPTIONS[4];
    private static final String MEMORY_4_TEXT = DESCRIPTIONS[5];

    private CurScreen screen = CurScreen.INTRO;
    private int choice;

    private enum CurScreen {
        INTRO, INTRO_2, ACCEPT, LEAVE;
    }

    public BGSensoryStone() {
        super(NAME, INTRO_TEXT, "images/events/sensoryStone.jpg");
        this.noCardsInRewards = true;
        this.imageEventText.setDialogOption(OPTIONS[5]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SENSORY");
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(INTRO_TEXT_2);
                this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                this.imageEventText.setDialogOption(OPTIONS[1] + 2 + OPTIONS[3]);
                this.screen = CurScreen.INTRO_2;
                return;
            case INTRO_2:
                getRandomMemory();
                switch (buttonPressed) {
                    case 0:
                        this.screen = CurScreen.ACCEPT;
                        logMetric("SensoryStone", "Memory 1");
                        this.choice = 1;
                        reward(this.choice);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        break;
                    case 1:
                        this.screen = CurScreen.ACCEPT;
                        logMetricTakeDamage("SensoryStone", "Memory 2", 5);
                        this.choice = 2;
                        reward(this.choice);
                        AbstractDungeon.player.damage(new DamageInfo(null, 2, DamageInfo.DamageType.HP_LOSS));
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        break;

                }

                this.imageEventText.clearRemainingOptions();
                return;
            case ACCEPT:
                reward(this.choice); break;
        }
        openMap();
    }






    private void getRandomMemory() {
        ArrayList<String> memories = new ArrayList<>();
        memories.add(MEMORY_1_TEXT);
        memories.add(MEMORY_2_TEXT);
        memories.add(MEMORY_3_TEXT);
        memories.add(MEMORY_4_TEXT);
        Collections.shuffle(memories, new Random(AbstractDungeon.miscRng.randomLong()));
        this.imageEventText.updateBodyText(memories.get(0));
    }

    private void reward(int num) {
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        for (int i = 0; i < num; i++) {
            AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));
        }
        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.LEAVE;
    }
}


