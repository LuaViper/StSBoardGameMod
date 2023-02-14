package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WarpedTongs;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

public class BGAccursedBlacksmith extends AbstractImageEvent {
    public static final String ID = "BGAccursed Blacksmith";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGAccursed Blacksmith");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String FORGE_RESULT = DESCRIPTIONS[1]+DESCRIPTIONS[3];
    private static final String RUMMAGE_RESULT = DESCRIPTIONS[2];
    private static final String CURSE_RESULT2 = DESCRIPTIONS[4];
    private static final String LEAVE_RESULT = DESCRIPTIONS[5];

    private int screenNum = 0;
    private boolean pickCard = false;

    public BGAccursedBlacksmith() {
        super(NAME, DIALOG_1, "images/events/blacksmith.jpg");

        if (AbstractDungeon.player.masterDeck.hasUpgradableCards().booleanValue()) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4], true);
        }

        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FORGE");
        }
    }


    public void update() {
        super.update();


        if (this.pickCard &&
                !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            c.upgrade();
            logMetricCardUpgrade("Accursed Blacksmith", "Forge", c);
            AbstractDungeon.player.bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.pickCard = false;

        }
    }


    protected void buttonEffect(int buttonPressed) {
        AbstractCard pain;
        switch (this.screenNum) {

            case 0:
                switch (buttonPressed) {
                    case 0:
                        int damageAmount = (int)(2);
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));

                        this.pickCard = true;
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                .getUpgradableCards(), 1, OPTIONS[3], true, false, false, false);






                        this.imageEventText.updateBodyText(FORGE_RESULT);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;

                    case 1:
                        this.screenNum = 2;
                        this.imageEventText.updateBodyText(RUMMAGE_RESULT + CURSE_RESULT2);

                        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(
                                AbstractDungeon.returnRandomRelicTier());
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), r);

                        int random = AbstractDungeon.miscRng.random(1, 6);
                        if(random<=3) {

                            pain = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) pain, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                            AbstractBGDungeon.removeCardFromRewardDeck(pain);
                            logMetricObtainCardAndRelic("Accursed Blacksmith", "Rummage", (AbstractCard)pain, r);
                            this.imageEventText.updateBodyText(RUMMAGE_RESULT + CURSE_RESULT2);
                        }else{
                            this.imageEventText.updateBodyText(RUMMAGE_RESULT );
                            logMetricObtainRelic("Accursed Blacksmith", "Rummage",  r);
                        }





                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;

                    case 2:
                        this.screenNum = 2;
                        logMetricIgnored("Accursed Blacksmith");
                        this.imageEventText.updateBodyText(LEAVE_RESULT);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        break;
                }
                this.imageEventText.clearRemainingOptions();
                return;
        }
        openMap();
    }
}


