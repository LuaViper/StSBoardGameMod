package BoardGame.events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGWomanInBlue
        extends AbstractImageEvent {
    public static final String ID = "BGThe Woman in Blue";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGThe Woman in Blue");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final int cost1 = 20;
    private static final int cost2 = 30;
    private static final int cost3 = 40;
    private static final float PUNCH_DMG_PERCENT = 0.05F;
    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, RESULT;
    }

    public BGWomanInBlue() {
        super(NAME, DIALOG_1, "images/events/ladyInBlue.jpg");
        this.noCardsInRewards = true;
        //TODO: lock options if player doesn't have enough gold

        this.imageEventText.setDialogOption(OPTIONS[0] + 2 + OPTIONS[3], (AbstractDungeon.player.gold < 2));

        this.imageEventText.setDialogOption(OPTIONS[1] + 3 + OPTIONS[3], (AbstractDungeon.player.gold < 3));

        this.imageEventText.setDialogOption(OPTIONS[5] + 1 + OPTIONS[6]);

    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {

                    case 0:
                        AbstractDungeon.player.loseGold(2);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        logMetric("Bought 1 Potion");
                        (AbstractDungeon.getCurrRoom()).rewards.clear();
                        (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                        AbstractDungeon.combatRewardScreen.open();
                        break;

                    case 1:
                        AbstractDungeon.player.loseGold(3);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        logMetric("Bought 2 Potions");
                        (AbstractDungeon.getCurrRoom()).rewards.clear();
                        (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                        (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                        AbstractDungeon.combatRewardScreen.open();
                        break;

                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);



                        CardCrawlGame.sound.play("BLUNT_FAST");

                        AbstractDungeon.player.damage(new DamageInfo(null,
                        1, DamageInfo.DamageType.HP_LOSS));


                        logMetric("Bought 0 Potions");
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]);
                this.screen = CurScreen.RESULT;
                return;
        }
        openMap();
    }



    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric("The Woman in Blue", actionTaken);
    }
}


