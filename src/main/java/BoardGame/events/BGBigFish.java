package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

public class BGBigFish
        extends AbstractImageEvent {
    public static final String ID = "BGBig Fish";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGBig Fish");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String BANANA_RESULT = DESCRIPTIONS[1];
    private static final String DONUT_RESULT = DESCRIPTIONS[2];

    private static final String BOX_RESULT = DESCRIPTIONS[4];
    private static final String BOX_BAD = DESCRIPTIONS[5];

    private int healAmt = 2;
    private static final int MAX_HP_AMT = 5;
    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, RESULT;
    }

    public BGBigFish() {
        super(NAME, DIALOG_1, "images/events/fishing.jpg");
        this.healAmt = 2;
        this.imageEventText.setDialogOption(OPTIONS[0] + this.healAmt + OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[6]);
    }
    protected void buttonEffect(int buttonPressed) {
        Regret regret;
        AbstractRelic r;
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {

                    case 0:
                        AbstractDungeon.player.heal(this.healAmt, true);
                        this.imageEventText.updateBodyText(BANANA_RESULT);
                        AbstractEvent.logMetricHeal("Big Fish", "Banana", this.healAmt);
                        break;

                    case 1:
                        this.imageEventText.updateBodyText(DONUT_RESULT);
                        AbstractEvent.logMetricMaxHPGain("Big Fish", "Donut", 0);
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE) && c.canUpgrade()) {
                                c.upgrade();
                                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c
                                        .makeStatEquivalentCopy(),
                                        0.5F * Settings.WIDTH,
                                        0.5F * Settings.HEIGHT));
                                break;
                            }
                        }
                        break;
                    case 2:
                    default:
                        this.imageEventText.updateBodyText(BOX_RESULT + BOX_BAD);
                        AbstractCard randomcurse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                        r = AbstractDungeon.returnRandomScreenlessRelic(
                                AbstractDungeon.returnRandomRelicTier());
                        AbstractEvent.logMetricObtainCardAndRelic("Big Fish", "Box", (AbstractCard)randomcurse, r);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(

                                CardLibrary.getCopy(((AbstractCard)randomcurse).cardID), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));


                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), r);
                        break;
                    case 3:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        boolean success=attemptRemoveStrike(false);
                        if(!success){
                            attemptRemoveStrike(true);
                        }
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.screen = CurScreen.RESULT;
                return;
        }
        openMap();
    }



    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric("Big Fish", actionTaken);
    }


    private boolean attemptRemoveStrike(boolean upgraded){
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                if(c.upgraded==upgraded) {
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                    //AbstractEvent.logMetricCardRemovalAtCost("The Cleric", "Card Removal", c, 3);
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    return true;
                }
            }
        }
        return false;
    }
}


