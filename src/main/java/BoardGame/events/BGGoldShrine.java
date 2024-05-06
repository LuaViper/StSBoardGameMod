package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BGGoldShrine extends AbstractImageEvent {
    public static final String ID = "BGGolden Shrine";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGGolden Shrine");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final int GOLD_AMT = 2;

    private static final int CURSE_GOLD_AMT = 7;
    private static final int A_2_GOLD_AMT = 50;
    private int goldAmt;
    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String DIALOG_3 = DESCRIPTIONS[2];
    private static final String IGNORE = DESCRIPTIONS[3];

    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private enum CUR_SCREEN {
        INTRO, COMPLETE;
    }

    public BGGoldShrine() {
        super(NAME, DIALOG_1, "images/events/goldShrine.jpg");

            this.goldAmt = 2;


        this.imageEventText.setDialogOption(OPTIONS[0] + this.goldAmt + OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
        //this.imageEventText.setDialogOption(OPTIONS[3]);
    }


    public void onEnterRoom() {
        CardCrawlGame.music.playTempBGM("SHRINE");
    }

    public void update() {
        super.update();

        if (!RoomEventDialog.waitForInput) {
            buttonEffect(this.roomEventText.getSelectedOption());
        }
    }

    protected void buttonEffect(int buttonPressed) {
        AbstractCard regret;
        switch (this.screen) {

            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.screen = CUR_SCREEN.COMPLETE;
                        logMetricGainGold("Golden Shrine", "Pray", this.goldAmt);
                        this.imageEventText.updateBodyText(DIALOG_2);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(100));
                        AbstractDungeon.player.gainGold(this.goldAmt);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1:
                        this.screen = CUR_SCREEN.COMPLETE;
                        regret = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                        logMetricGainGoldAndCard("Golden Shrine", "Desecrate", (AbstractCard)regret, 7);
                        AbstractBGDungeon.removeCardFromRewardDeck(regret);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(275));
                        AbstractDungeon.player.gainGold(7);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard)regret, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2:
                        this.screen = CUR_SCREEN.COMPLETE;
                        logMetricIgnored("Golden Shrine");
                        this.imageEventText.updateBodyText(IGNORE);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            case COMPLETE:
                openMap();
                break;
        }
    }
}


