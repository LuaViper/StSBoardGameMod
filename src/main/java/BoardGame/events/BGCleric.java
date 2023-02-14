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
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

public class BGCleric
        extends AbstractImageEvent {
    public static final String ID = "BGThe Cleric";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGThe Cleric");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final int HEAL_COST = 35;
    private static final int PURIFY_COST = 50;
    private static final int A_2_PURIFY_COST = 75;
    private int purifyCost = 0;

    private static final float HEAL_AMT = 0.25F;

    private static final String DIALOG_INTRO = DESCRIPTIONS[0];
    private static final String DIALOG_HEAL = DESCRIPTIONS[1];
    private static final String DIALOG_UPGRADE = DESCRIPTIONS[2];
    private static final String DIALOG_REMOVE = DESCRIPTIONS[3];
    private static final String DIALOG_LEAVE = DESCRIPTIONS[4];

    private int healAmt;
private boolean pickCard;
    private int buttonchoice;

    public BGCleric() {
        super(NAME, DIALOG_INTRO, "images/events/cleric.jpg");

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.purifyCost = 75;
        } else {
            this.purifyCost = 50;
        }

        int gold = AbstractDungeon.player.gold;
        if (gold >= 1) {
            this.healAmt = 1;
            this.imageEventText.setDialogOption(OPTIONS[0], (gold < 1));
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], (gold < 1));
        }

        if (gold >= 2) {
            this.imageEventText.setDialogOption(OPTIONS[2], (gold < 2));
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], (gold < 2));
        }

        if (gold >= 3) {
            this.imageEventText.setDialogOption(OPTIONS[4], (gold < 3));
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5], (gold < 3));
        }

        this.imageEventText.setDialogOption(OPTIONS[6]);
    }


    public void update() {
        super.update();


        if (this.pickCard &&
                !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            if(buttonchoice==1) {
                c.upgrade();
                AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractEvent.logMetricCardUpgradeAtCost("The Cleric", "Upgrade", c, 2);

            }else if(buttonchoice==2){
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                AbstractEvent.logMetricCardRemovalAtCost("The Cleric", "Card Removal", c, 3);
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {

            case 0:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.player.loseGold(1);
                        AbstractDungeon.player.heal(3);
                        showProceedScreen(DIALOG_HEAL);
                        AbstractEvent.logMetricHealAtCost("The Cleric", "Healed", 1, 3);
                        return;
                    case 1:
                        buttonchoice=1;
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                .getUpgradableCards(), 1, OPTIONS[8], true, false, false, false);
                        this.pickCard = true;
                        showProceedScreen(DIALOG_UPGRADE);
                        return;
                    case 2:
                        buttonchoice=2;
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                                .size() > 0) {
                            AbstractDungeon.player.loseGold(3);
                            AbstractDungeon.gridSelectScreen.open(
                                    CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                            .getPurgeableCards()), 1, OPTIONS[7], false, false, false, true);
                        }
                        this.pickCard = true;
                        showProceedScreen(DIALOG_REMOVE);
                        return;
                }
                showProceedScreen(DIALOG_LEAVE);
                AbstractEvent.logMetric("The Cleric", "Leave");
                return;
        }



        openMap();
    }
}


