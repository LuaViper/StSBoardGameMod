package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

public class BGTombRedMask
        extends AbstractImageEvent {
    public static final String ID = "BGTombRedMask";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGTombRedMask");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final int GOLD_AMT = 222;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String MASK_RESULT = DESCRIPTIONS[1];
    private static final String RELIC_RESULT = DESCRIPTIONS[2];
    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, RESULT;
    }

    public BGTombRedMask() {
        super(NAME, DIALOG_1, "images/events/redMaskTomb.jpg");

        this.imageEventText.setDialogOption(OPTIONS[2] + OPTIONS[3]);
        if (AbstractDungeon.player.hasRelic("BGRedMask")) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[4]);
    }



    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                if (buttonPressed == 1) {
                    AbstractDungeon.effectList.add(new RainingGoldEffect(6*20));
                    AbstractDungeon.player.gainGold(6);
                    this.imageEventText.updateBodyText(MASK_RESULT);
                    logMetricGainGold("Tomb of Lord Red Mask", "Wore Mask", 6);
                } else if (buttonPressed == 0) {
                    AbstractRelic relic= AbstractBGDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON);
                    logMetricObtainRelicAtCost("Tomb of Lord Red Mask", "Paid", (AbstractRelic)relic, AbstractDungeon.player.gold);
                    AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), (AbstractRelic)relic);
                    this.imageEventText.updateBodyText(RELIC_RESULT);
                } else {
                    openMap();
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    logMetricIgnored("Tomb of Lord Red Mask");
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]);
                this.screen = CurScreen.RESULT;
                return;
        }
        openMap();
    }
}


