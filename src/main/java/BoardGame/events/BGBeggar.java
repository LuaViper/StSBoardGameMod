package BoardGame.events;


import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class BGBeggar extends AbstractImageEvent {
    public static final String ID = "BGBeggar";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGBeggar");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private CurScreen screen;
    public static final int GOLD_COST = 2;
    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String CANCEL_DIALOG = DESCRIPTIONS[1];
    private static final String PURGE_DIALOG = DESCRIPTIONS[2];
    private static final String POST_PURGE_DIALOG = DESCRIPTIONS[3];

    public enum CurScreen {
        INTRO, LEAVE, GAVE_MONEY;
    }

    public BGBeggar() {
        super(NAME, DIALOG_1, "images/events/beggar.jpg");

        if (AbstractDungeon.player.gold >= 2) {
            this.imageEventText.setDialogOption(OPTIONS[0] + 2 + OPTIONS[1], (AbstractDungeon.player.gold < 2));
        }
        else {

            this.imageEventText.setDialogOption(OPTIONS[2] + 2 + OPTIONS[3], (AbstractDungeon.player.gold < 2));
        }



        this.imageEventText.setDialogOption(OPTIONS[5]);
        this.hasDialog = true;
        this.hasFocus = true;
        this.screen = CurScreen.INTRO;
    }


    public void update() {
        super.update();


        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            logMetricCardRemovalAtCost("Beggar", "Gave Gold", AbstractDungeon.gridSelectScreen.selectedCards


                    .get(0), 2);

            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards

                    .get(0), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));


            AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            openMap();
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    this.imageEventText.loadImage("images/events/cleric.jpg");
                    this.imageEventText.updateBodyText(PURGE_DIALOG);
                    AbstractDungeon.player.loseGold(2);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    this.screen = CurScreen.GAVE_MONEY; break;
                }
                this.imageEventText.updateBodyText(CANCEL_DIALOG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.screen = CurScreen.LEAVE;
                logMetricIgnored("Beggar");
                break;

            case GAVE_MONEY:
                AbstractDungeon.gridSelectScreen.open(
                        CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[6], false, false, false, true);






                this.imageEventText.updateBodyText(POST_PURGE_DIALOG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.screen = CurScreen.LEAVE;
                break;
            case LEAVE:
                this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                this.imageEventText.clearRemainingOptions();
                openMap();
                break;
        }
    }
}


