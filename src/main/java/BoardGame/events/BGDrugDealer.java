package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.MutagenicStrength;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGDrugDealer
        extends AbstractImageEvent
{
    private static final Logger logger = LogManager.getLogger(BGDrugDealer.class.getName());
    public static final String ID = "BGDrug Dealer";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGDrug Dealer");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0;
    private boolean cardsSelected = false;
    private boolean upgrade=false;
    private boolean transform=false;

    public BGDrugDealer() {
        super(NAME, DESCRIPTIONS[0], "images/events/drugDealer.jpg");

        this.imageEventText.setDialogOption(OPTIONS[0]);
        //TODO: 2-card requirement doesn't apply to BG; chance of softlock here
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }
    protected void buttonEffect(int buttonPressed) {
        JAX jAX;
        Circlet circlet;
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: {
                        int damageAmount = (int) (1);
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));
                        }
                        upgrade=true;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                .getUpgradableCards(), 1, OPTIONS[6], true, false, false, false);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: {
                        int damageAmount = (int) (2);
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));
                        }
                        transform=true;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        transform();
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        //logMetricObtainRelic("Drug Dealer", "Inject Mutagens", (AbstractRelic)circlet); //TODO: log ignore event
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default:
                        logger.info("ERROR: Unhandled case " + buttonPressed);
                        break;
                }
                this.screenNum = 1;
                break;
            case 1:
                openMap();
                break;
        }
    }

    public void update() {
        super.update();
        if (!this.cardsSelected) {
            List<String> transformedCards = new ArrayList<>();
            List<String> obtainedCards = new ArrayList<>();
            if (upgrade && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                c.upgrade();
                logMetricCardUpgrade("Drug Dealer", "Upgraded", c);
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
            }
            if (transform && AbstractDungeon.gridSelectScreen.selectedCards.size() == 2) {
                this.cardsSelected = true;
                float displayCount = 0.0F;
                Iterator<AbstractCard> i = AbstractDungeon.gridSelectScreen.selectedCards.iterator();
                while (i.hasNext()) {
                    AbstractCard card = i.next();
                    card.untip();
                    card.unhover();
                    transformedCards.add(card.cardID);
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng);

                    AbstractCard c = AbstractDungeon.getTransformedCard();
                    obtainedCards.add(c.cardID);

                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM && c != null) {
                        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(c

                                .makeCopy(), Settings.WIDTH / 3.0F + displayCount, Settings.HEIGHT / 2.0F, false));



                        displayCount += Settings.WIDTH / 6.0F;
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                logMetricTransformCards("Drug Dealer", "Became Test Subject", transformedCards, obtainedCards);
                (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
            }
        }
    }

    private void transform() {
        if (!AbstractDungeon.isScreenUp) {
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                    .getPurgeableCards(), 2, OPTIONS[5], false, false, false, false);


        }
        else {



            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                    .getPurgeableCards(), 2, OPTIONS[5], false, false, false, false);
        }
    }
}


