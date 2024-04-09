package BoardGame.events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;


public class BGGoldenIdolEvent
        extends AbstractImageEvent
{
    public static final String ID = "BGGolden Idol";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGGolden Idol");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_START = DESCRIPTIONS[0];
    private static final String DIALOG_BOULDER = DESCRIPTIONS[1];
    private static final String DIALOG_CHOSE_RUN = DESCRIPTIONS[2];
    private static final String DIALOG_CHOSE_FIGHT = DESCRIPTIONS[3];
    private static final String DIALOG_CHOSE_FLAT = DESCRIPTIONS[4];
    private static final String DIALOG_IGNORE = DESCRIPTIONS[5];

    private int screenNum = 0;

    private static final float HP_LOSS_PERCENT = 0.25F;
    private static final float MAX_HP_LOSS_PERCENT = 0.08F;
    private static final float A_2_HP_LOSS_PERCENT = 0.35F;
    private static final float A_2_MAX_HP_LOSS_PERCENT = 0.1F;
    private int damage;
    private int maxHpLoss;
    private AbstractRelic relicMetric = null;

    public BGGoldenIdolEvent() {
        super(NAME, DIALOG_START, "images/events/goldenIdol.jpg");

        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);

        this.damage = 1;
        this.maxHpLoss = 1;
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_GOLDEN");
        }
    }

    protected void buttonEffect(int buttonPressed) {
        Injury injury;
        switch (this.screenNum) {

            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_BOULDER);
                        this.relicMetric=AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2),
                                this.relicMetric);




                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                }

                this.imageEventText.updateBodyText(DIALOG_IGNORE);
                this.screenNum = 2;
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                this.imageEventText.clearRemainingOptions();
                AbstractEvent.logMetricIgnored("Golden Idol");
                return;



            case 1:

                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                this.imageEventText.updateBodyText(DIALOG_CHOSE_FIGHT);
                CardCrawlGame.sound.play("BLUNT_FAST");
                AbstractDungeon.player.damage(new DamageInfo(null, this.damage));
                this.screenNum = 2;
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);

                AbstractEvent.logMetricObtainRelicAndDamage("Golden Idol", "Take Damage", this.relicMetric, this.damage);

                this.imageEventText.clearRemainingOptions();
                return;




            case 2:
                openMap();
                return;
        }
        openMap();
    }
}


