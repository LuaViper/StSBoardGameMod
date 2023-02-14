package BoardGame.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import static com.megacrit.cardcrawl.cards.AbstractCard.CardRarity.CURSE;

public class BGBonfire
        extends AbstractImageEvent {
    public static final String ID = "BGBonfire Elementals";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGBonfire Elementals");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String DIALOG_3 = DESCRIPTIONS[2];

    private CUR_SCREEN screen = CUR_SCREEN.INTRO;
    private AbstractCard offeredCard = null;
    private boolean cardSelect = false;

    private enum CUR_SCREEN {
        INTRO, CHOOSE, BOOM, COMPLETE;
    }

    public BGBonfire() {
        super(NAME, DIALOG_1, "images/events/bonfire.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }



    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_GOOP");
        }
    }


    public void update() {
        super.update();

        if (this.cardSelect &&
                !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            int heal, heal2; this.offeredCard = AbstractDungeon.gridSelectScreen.selectedCards.remove(0);

            switch (this.offeredCard.rarity) {
                case CURSE:
                    logMetricCardRemovalAndDamage("Bonfire Elementals", "Offered Curse", this.offeredCard, 2);
                    break;
                case BASIC:
                    logMetricCardRemoval("Bonfire Elementals", "Offered Basic", this.offeredCard);
                    break;
                case COMMON:
                    logMetricCardRemoval("Bonfire Elementals", "Offered Common", this.offeredCard);
                case SPECIAL:
                    logMetricCardRemoval("Bonfire Elementals", "Offered Special", this.offeredCard);
                    break;
                case UNCOMMON:
                    heal = AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth;
                    logMetricCardRemovalAndHeal("Bonfire Elementals", "Offered Uncommon", this.offeredCard, 3);
                    break;
                case RARE:
                    heal2 = AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth;
                    logMetricCardRemovalAndHeal("Bonfire Elementals", "Offered Rare", this.offeredCard, heal2);
                    break;
            }



            setReward(this.offeredCard.rarity);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(this.offeredCard, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));

            AbstractDungeon.player.masterDeck.removeCard(this.offeredCard);
            this.imageEventText.updateDialogOption(0, OPTIONS[1]);
            if(this.offeredCard.rarity!=CURSE) {
                this.screen = CUR_SCREEN.COMPLETE;
                this.cardSelect = false;
            }else{
                this.screen = CUR_SCREEN.BOOM;
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
            }
        }
    }



    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {

            case INTRO:
                this.imageEventText.updateBodyText(DIALOG_2);
                this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                this.screen = CUR_SCREEN.CHOOSE;
                break;

            case CHOOSE:
                if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                        .size() > 0) {
                    AbstractDungeon.gridSelectScreen.open(
                            CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[3], false, false, false, true);
                    this.cardSelect = true;
                    break;
                }
                this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                this.screen = CUR_SCREEN.COMPLETE;
                break;

            case BOOM:
                if(this.offeredCard!=null && this.offeredCard.rarity==CURSE){
                    //CardCrawlGame.sound.play("ATTACK_POISON");
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    AbstractDungeon.player.damage(new DamageInfo(null,1, DamageInfo.DamageType.HP_LOSS));
                }
                this.imageEventText.updateBodyText(DESCRIPTIONS[8]);
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                this.screen = CUR_SCREEN.COMPLETE;
                break;

            case COMPLETE:

                openMap();
                break;
        }
    }

    private void setReward(AbstractCard.CardRarity rarity) {
        String dialog = DIALOG_3;
        switch (rarity) {
            case CURSE:
                dialog = dialog + DESCRIPTIONS[3];
                break;
            case BASIC:
                dialog = dialog + DESCRIPTIONS[4];
                break;
            case COMMON:
            case SPECIAL:
                dialog = dialog + DESCRIPTIONS[5];

                break;
            case UNCOMMON:
                dialog = dialog + DESCRIPTIONS[6];
                AbstractDungeon.player.heal(3);

                break;
            case RARE:
                dialog = dialog + DESCRIPTIONS[7];
                AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);

                break;
        }



        this.imageEventText.updateBodyText(dialog);
    }
}


