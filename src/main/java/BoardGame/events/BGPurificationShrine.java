package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.List;

public class BGPurificationShrine
        extends AbstractImageEvent {
    public static final String ID = "BGPurificationShrine";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGPurificationShrine");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String IGNORE = DESCRIPTIONS[2];
    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private enum CUR_SCREEN {
        INTRO, COMPLETE;
    }

    public BGPurificationShrine() {
        super(NAME, DIALOG_1, "images/events/shrine3.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);    //TODO: lock option if deck has no curses
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }


    public void onEnterRoom() {
        CardCrawlGame.music.playTempBGM("SHRINE");
    }

    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            logMetricCardRemoval("Purifier", "Purged", AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards
                    .get(0), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));

            AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {

            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.gridSelectScreen.open(
                                CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                        .getPurgeableCards()), 1, OPTIONS[2], false, false, false, true);

                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1:
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        int i;
                        List<String> curses = new ArrayList<>();
                        this.screenNum = 1;

                        for (i = AbstractDungeon.player.masterDeck.group.size() - 1; i >= 0; i--) {
                            if (((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).type == AbstractCard.CardType.CURSE &&
                                    !((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).inBottleFlame &&
                                    !((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).inBottleLightning && ((AbstractCard)AbstractDungeon.player.masterDeck.group
                                    .get(i)).cardID != "AscendersBane" && ((AbstractCard)AbstractDungeon.player.masterDeck.group
                                    .get(i)).cardID != "CurseOfTheBell" && ((AbstractCard)AbstractDungeon.player.masterDeck.group
                                    .get(i)).cardID != "Necronomicurse") {

                                AbstractDungeon.effectList.add(new PurgeCardEffect(AbstractDungeon.player.masterDeck.group
                                        .get(i)));
                                curses.add(((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).cardID);
                                AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.player.masterDeck.group
                                        .get(i));
                            }
                        }
                        logMetricRemoveCards("Fountain of Cleansing", "Removed Curses", curses);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2:
                        this.screen = CUR_SCREEN.COMPLETE;
                        logMetricIgnored("Purifier");
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
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


