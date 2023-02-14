package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BGLivingWall
        extends AbstractImageEvent {
    public static final String ID = "BGLiving Wall";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("Living Wall");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String RESULT_DIALOG = DESCRIPTIONS[1];

    private CurScreen screen = CurScreen.INTRO;
    private boolean pickCard = false;
    private Choice choice = null;

    private enum CurScreen {
        INTRO, RESULT;
    }

    private enum Choice {
        FORGET, CHANGE, GROW;
    }

    public BGLivingWall() {
        super(NAME, DIALOG_1, "images/events/livingWall.jpg");

        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        if (AbstractDungeon.player.masterDeck.hasUpgradableCards().booleanValue()) {
            this.imageEventText.setDialogOption(OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[7], true);
        }
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_LIVING_WALL");
        }
    }


    public void update() {
        super.update();
        if (this.pickCard &&
                !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c, transCard, upgCard; switch (this.choice) {
                case FORGET:
                    CardCrawlGame.sound.play("CARD_EXHAUST");
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards

                            .get(0), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));


                    AbstractEvent.logMetricCardRemoval("Living Wall", "Forget", AbstractDungeon.gridSelectScreen.selectedCards


                            .get(0));
                    AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards
                            .get(0));
                    break;
                case CHANGE:
                    c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                    transCard = AbstractDungeon.getTransformedCard();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, c.current_x, c.current_y));

                    AbstractEvent.logMetricTransformCard("Living Wall", "Change", c, transCard);
                    break;
                case GROW:
                    AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                    ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).upgrade();
                    upgCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    AbstractEvent.logMetricCardUpgrade("Living Wall", "Grow", upgCard);
                    AbstractDungeon.player.bottledCardUpgradeCheck(upgCard);
                    break;
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;
        }
    }



    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.choice = Choice.FORGET;
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                                .size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(
                                    CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                            .getPurgeableCards()), 1, OPTIONS[3], false, false, false, true);
                        }
                        break;






                    case 1:
                        this.choice = Choice.CHANGE;
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                                .size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(
                                    CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                            .getPurgeableCards()), 1, OPTIONS[4], false, true, false, false);
                        }
                        break;






                    default:
                        this.choice = Choice.GROW;
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                                .size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                    .getUpgradableCards(), 1, OPTIONS[5], true, false, false, false);
                        }
                        break;
                }






                this.pickCard = true;
                this.imageEventText.updateBodyText(RESULT_DIALOG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[6]);
                this.screen = CurScreen.RESULT;
                return;
        }
        openMap();
    }
}


