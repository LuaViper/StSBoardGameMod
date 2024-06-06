package BoardGame.events;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.screen.RelicTradingScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

public class BGWeMeetAgain
        extends AbstractImageEvent implements LockRelicsEvent {
    public static final String ID = "BGWeMeetAgain";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGWeMeetAgain");

    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final int MAX_GOLD = 150;

    private AbstractPotion potionOption;

    private AbstractCard cardOption;

    private int goldAmount;

    private AbstractRelic givenRelic;

    private static final String DIALOG_1 = DESCRIPTIONS[0];

    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private enum CUR_SCREEN {
        INTRO, COMPLETE;
    }
    public boolean reliclock=false;
    public boolean relicsLocked(){
        return reliclock;
    }

    public boolean softlocked=true;

    public BGWeMeetAgain() {
        super(NAME, DIALOG_1, "images/events/weMeetAgain.jpg");
        reliclock=true;
        this.goldAmount = getGoldAmount();
        if (this.goldAmount != 0) {
            softlocked=false;
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true);
        }

        if (!AbstractBGRelic.getAllPayableRelics().isEmpty()) {
            softlocked=false;
            this.imageEventText.setDialogOption(OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], true);
        }

        CardGroup list = getRanwiddableCards();
        if (!list.isEmpty()) {
            softlocked=false;
            this.imageEventText.setDialogOption(OPTIONS[4]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5], true);
        }
        //if(softlocked)
        if(true)
            this.imageEventText.setDialogOption(OPTIONS[7]);
    }

    private CardGroup getRanwiddableCards() {
        CardGroup retVal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        ArrayList<AbstractCard> list = new ArrayList<>();
        CardGroup purgableCards = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                .getPurgeableCards());
        for (AbstractCard c : purgableCards.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE || c.rarity == AbstractCard.CardRarity.UNCOMMON)
                retVal.addToTop(c);
        }
        return retVal;
    }

    private int getGoldAmount() {
        if (AbstractDungeon.player.gold < 4)
            return 0;   //"Locked"
        return 4;
    }


    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            this.imageEventText.updateBodyText(DESCRIPTIONS[3] + DESCRIPTIONS[5]);
            this.imageEventText.clearRemainingOptions();
            this.cardOption=AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(cardOption, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard(cardOption);
            relicReward();
            logMetricRemoveCardAndObtainRelic("WeMeetAgain", "Gave Card", this.cardOption, this.givenRelic);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                this.screen = CUR_SCREEN.COMPLETE;
                switch (buttonPressed) {
                    case 0:
                        reliclock=false;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1] + DESCRIPTIONS[5]);
                        AbstractDungeon.player.loseGold(this.goldAmount);
                        relicReward();
                        logMetricObtainRelicAtCost("WeMeetAgain", "Paid Gold", this.givenRelic, this.goldAmount);
                        break;
                    case 1:
                        RelicTradingScreen.RelicTradingAction action = (relic) -> {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2] + DESCRIPTIONS[5]);
                            AbstractDungeon.player.loseRelic(relic.relicId);
                            relicReward();
                            //TODO: there's a different logmetric for specifying which relic was given away
                            logMetricObtainRelic("WeMeetAgain", "Gave Relic", relic);
                            reliclock=false;
                        };
                        //TODO: localization
                        BaseMod.openCustomScreen(RelicTradingScreen.Enum.RELIC_TRADING, action, "Choose a Relic to exhange.", false);
                        break;
                    case 2:
                        reliclock=false;
                        //TODO: localization
                        AbstractDungeon.gridSelectScreen.open(getRanwiddableCards(), 1, "Choose a card to remove from your deck.", false, false, false, true);
                        break;
                    case 3:
                        reliclock=false;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        logMetricIgnored("WeMeetAgain");
                        break;
                }
                this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                this.imageEventText.clearRemainingOptions();
                break;
            case COMPLETE:
                reliclock=false;
                openMap();
                break;
        }
    }

    private void relicReward() {
        this.givenRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.28F, Settings.HEIGHT / 2.0F, this.givenRelic);
    }
}

