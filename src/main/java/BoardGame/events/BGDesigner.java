package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BGDesigner
        extends AbstractImageEvent {
    public static final String ID = "BGDesigner";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGDesigner");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESC = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private CurrentScreen curScreen = CurrentScreen.INTRO; public static final int GOLD_REQ = 75; public static final int UPG_AMT = 2; public static final int REMOVE_AMT = 2; private boolean adjustmentUpgradesOne; private boolean cleanUpRemovesCards;
    private OptionChosen option = null;
    private int adjustCost;
    private int cleanUpCost;
    private int fullServiceCost;
    private int hpLoss;

    private enum CurrentScreen { INTRO, MAIN, DONE; }


    private enum OptionChosen {
        UPGRADE, REMOVE, REMOVE_AND_UPGRADE,REMOVE_AND_UPGRADE_2, TRANSFORM, NONE;
    }

    public BGDesigner() {
        super(NAME, DESC[0], "images/events/designer2.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.option = OptionChosen.NONE;


        this.adjustmentUpgradesOne = true;
        this.cleanUpRemovesCards = true;


        this.adjustCost = 2;
        this.cleanUpCost = 3;
        this.fullServiceCost = 5;
        this.hpLoss = 1;

    }


    public void update() {
        super.update();


        if (this.option != OptionChosen.NONE) {
            switch (this.option) {
                case REMOVE:
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        CardCrawlGame.sound.play("CARD_EXHAUST");
                        AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                        logMetricCardRemovalAtCost("Designer", "Single Remove", c, this.cleanUpCost);
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                        AbstractDungeon.player.masterDeck.removeCard(c);
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        this.option = OptionChosen.NONE;
                    }
                    break;
                case REMOVE_AND_UPGRADE:
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {


                        AbstractCard removeCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

                        CardCrawlGame.sound.play("CARD_EXHAUST");
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(removeCard, Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH - 20.0F * Settings.scale, Settings.HEIGHT / 2.0F));

                        AbstractDungeon.player.masterDeck.removeCard(removeCard);
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();

                        //TODO: this easy workaround will prevent logging from showing the removed card.  fix it.  also, very possibly chance of softlock at 0 cards remaining.
                        this.option = OptionChosen.UPGRADE;
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                .getUpgradableCards(), 1, OPTIONS[15], true, false, false, false);

//                        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
//                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
//                            if (c.canUpgrade()) {
//                                upgradableCards.add(c);
//                            }
//                        }
//
//                        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng
//
//                                .randomLong()));
//
//                        if (!upgradableCards.isEmpty()) {
//                            AbstractCard upgradeCard = upgradableCards.get(0);
//                            upgradeCard.upgrade();
//                            AbstractDungeon.player.bottledCardUpgradeCheck(upgradeCard);
//                            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(upgradeCard
//                                    .makeStatEquivalentCopy()));
//                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
//
//                            logMetricCardUpgradeAndRemovalAtCost("Designer", "Upgrade and Remove", upgradeCard, removeCard, this.fullServiceCost);
//
//
//                        }
//                        else {
//
//
//                            logMetricCardRemovalAtCost("Designer", "Removal", removeCard, this.fullServiceCost);
//                        }
//
//                        this.option = OptionChosen.NONE;
                    }
                    break;

                case UPGRADE:
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        logMetricCardUpgradeAtCost("Designer", "Upgrade", AbstractDungeon.gridSelectScreen.selectedCards


                                .get(0), this.adjustCost);

                        ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).upgrade();
                        AbstractDungeon.player.bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards
                                .get(0));
                        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards

                                .get(0)).makeStatEquivalentCopy()));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        this.option = OptionChosen.NONE;
                    }
                    break;
            }
        }
    }




    protected void buttonEffect(int buttonPressed) {
        switch (this.curScreen) {
            case INTRO:
                this.imageEventText.updateBodyText(DESC[1]);

                this.imageEventText.removeDialogOption(0);


                if (true) {
                    this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.adjustCost + OPTIONS[6] + OPTIONS[9], (AbstractDungeon.player.gold < this.adjustCost ||
                            !AbstractDungeon.player.masterDeck
                                    .hasUpgradableCards().booleanValue()));
                }


                if (true) {
                    this.imageEventText.setDialogOption(OPTIONS[2] + this.cleanUpCost + OPTIONS[6] + OPTIONS[10], (AbstractDungeon.player.gold < this.cleanUpCost ||

                            CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck)
                                    .size() == 0));
                }


                this.imageEventText.setDialogOption(OPTIONS[3] + this.fullServiceCost + OPTIONS[6] + OPTIONS[13], (AbstractDungeon.player.gold < this.fullServiceCost ||
                        !AbstractDungeon.player.masterDeck
                                .hasUpgradableCards().booleanValue()));


                this.imageEventText.setDialogOption(OPTIONS[4] + this.hpLoss + OPTIONS[5]);

                this.curScreen = CurrentScreen.MAIN;
                return;
            case MAIN:
                switch (buttonPressed) {

                    case 0:
                        this.imageEventText.updateBodyText(DESC[2]);
                        AbstractDungeon.player.loseGold(this.adjustCost);
                        this.option = OptionChosen.UPGRADE;
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                .getUpgradableCards(), 1, OPTIONS[15], true, false, false, false);
                        break;



                    case 1:
                        this.imageEventText.updateBodyText(DESC[2]);
                        AbstractDungeon.player.loseGold(this.cleanUpCost);
                        this.option = OptionChosen.REMOVE;
                        AbstractDungeon.gridSelectScreen.open(
                                CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                        .getPurgeableCards()), 1, OPTIONS[17], false, false, false, true);
                        break;


                    case 2:
                        this.imageEventText.updateBodyText(DESC[2]);
                        AbstractDungeon.player.loseGold(this.fullServiceCost);
                        this.option = OptionChosen.REMOVE_AND_UPGRADE;
                        AbstractDungeon.gridSelectScreen.open(
                                CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                                        .getPurgeableCards()), 1, OPTIONS[17], false, false, false, true);
                        break;








                    case 3:
                        this.imageEventText.loadImage("images/events/designerPunched2.jpg");
                        this.imageEventText.updateBodyText(DESC[3]);
                        logMetricTakeDamage("Designer", "Punched", this.hpLoss);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        AbstractDungeon.player.damage(new DamageInfo(null, this.hpLoss, DamageInfo.DamageType.HP_LOSS));
                        break;
                }





                this.imageEventText.updateDialogOption(0, OPTIONS[14]);
                this.imageEventText.clearRemainingOptions();
                this.curScreen = CurrentScreen.DONE;
                return;
        }


        openMap();
    }



    private void upgradeTwoRandomCards() {
        ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) {
                upgradableCards.add(c);
            }
        }

        Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));

        if (upgradableCards.isEmpty()) {

            logMetricLoseGold("Designer", "Tried to Upgrade", this.adjustCost);
        } else if (upgradableCards.size() == 1) {
            ((AbstractCard)upgradableCards.get(0)).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards
                    .get(0)).makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            logMetricCardUpgradeAtCost("Designer", "Tried to Upgrade", upgradableCards.get(0), this.adjustCost);
        } else {
            List<String> cards = new ArrayList<>();
            cards.add(((AbstractCard)upgradableCards.get(0)).cardID);
            cards.add(((AbstractCard)upgradableCards.get(1)).cardID);
            logMetricUpgradeCardsAtCost("Designer", "Upgraded Two", cards, this.adjustCost);
            ((AbstractCard)upgradableCards.get(0)).upgrade();
            ((AbstractCard)upgradableCards.get(1)).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
            AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards

                    .get(0)).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 20.0F * Settings.scale, Settings.HEIGHT / 2.0F));


            AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards

                    .get(1)).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 20.0F * Settings.scale, Settings.HEIGHT / 2.0F));


            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
    }
}


