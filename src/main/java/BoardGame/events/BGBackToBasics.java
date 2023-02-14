package BoardGame.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import java.util.ArrayList;
import java.util.List;


public class BGBackToBasics
        extends AbstractImageEvent
{
    public static final String ID = "BGBack to Basics";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGBack to Basics");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String DIALOG_3 = DESCRIPTIONS[2];
    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private List<String> cardsUpgraded = new ArrayList<>();

    private enum CUR_SCREEN {
        INTRO, COMPLETE;
    }

    public BGBackToBasics() {
        super(NAME, DIALOG_1, "images/events/backToBasics.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_ANCIENT");
        }
        this.cardsUpgraded.clear();
    }


    public void update() {
        super.update();


        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.effectList.add(new PurgeCardEffect(c));
            AbstractEvent.logMetricCardRemoval("Back to Basics", "Elegance", c);
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {

            case INTRO:
                if (buttonPressed == 0) {
                    if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                            .size() > 0) {
                        this.imageEventText.updateBodyText(DIALOG_2);
                        AbstractDungeon.gridSelectScreen.open(
                                CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                        .getPurgeableCards()), 1, OPTIONS[2], false);
                    }



                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                    this.imageEventText.clearRemainingOptions();
                } else {
                    this.imageEventText.updateBodyText(DIALOG_3);
                    upgradeStrikeAndDefends();
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                    this.imageEventText.clearRemainingOptions();
                }
                this.screen = CUR_SCREEN.COMPLETE;
                break;
            case COMPLETE:
                openMap();
                break;
        }
    }

    private void upgradeStrikeAndDefends() {
        boolean strike=true;
        boolean defend=true;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (((c.hasTag(AbstractCard.CardTags.STARTER_DEFEND) && defend)
                    || (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE) && strike))
                    && c.canUpgrade()) {
                if(c.hasTag(AbstractCard.CardTags.STARTER_DEFEND))defend=false;
                if(c.hasTag(AbstractCard.CardTags.STARTER_STRIKE))strike=false;

                c.upgrade();
                this.cardsUpgraded.add(c.cardID);
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c

                        .makeStatEquivalentCopy(),
                        MathUtils.random(0.1F, 0.9F) * Settings.WIDTH,
                        MathUtils.random(0.2F, 0.8F) * Settings.HEIGHT));
            }
        }

        AbstractEvent.logMetricUpgradeCards("Back to Basics", "Simplicity", this.cardsUpgraded);
    }
}


