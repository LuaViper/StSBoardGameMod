package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.List;

public class BGWindingHalls
        extends AbstractImageEvent
{
    public static final String ID = "BGWindingHalls";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGWindingHalls");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final float HP_LOSS_PERCENT = 0.125F;

    private static final float HP_MAX_LOSS_PERCENT = 0.05F;
    private static final float A_2_HP_LOSS_PERCENT = 0.18F;
    private static final float HEAL_AMT = 0.25F;
    private static final String INTRO_BODY1 = DESCRIPTIONS[0]; private static final float A_2_HEAL_AMT = 0.2F; private int hpAmt; private int healAmt; private int maxHPAmt;
    private static final String INTRO_BODY2 = DESCRIPTIONS[1];
    private static final String CHOICE_1_TEXT = DESCRIPTIONS[2];
    private static final String CHOICE_2_TEXT = DESCRIPTIONS[3];

    private int screenNum = 0;

    public BGWindingHalls() {
        super(NAME, INTRO_BODY1, "images/events/winding.jpg");

        this.hpAmt = 2;
        this.healAmt = 3;

        this.imageEventText.setDialogOption(OPTIONS[0]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON)
            CardCrawlGame.sound.play("EVENT_WINDING");
    }

    protected void buttonEffect(int buttonPressed) {
        List<String> cards;
        AbstractCard curse;
        switch (this.screenNum) {

            case 0:
                this.imageEventText.updateBodyText(INTRO_BODY2);
                this.screenNum = 1;
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                this.imageEventText.setDialogOption(OPTIONS[3] + this.healAmt + OPTIONS[5]);
                this.imageEventText.setDialogOption(OPTIONS[6] + this.hpAmt + OPTIONS[7]);
                return;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        cards = new ArrayList<>();
                        this.imageEventText.updateBodyText(CHOICE_1_TEXT);
                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_1");
                        {
                            AbstractCard card = AbstractDungeon.getCardWithoutRng(AbstractCard.CardRarity.COMMON);
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) card, Settings.WIDTH / 2.0F - 350.0F * Settings.xScale, Settings.HEIGHT / 2.0F));
                            AbstractBGDungeon.removeCardFromRewardDeck(card);
                            cards.add(card.name);
                        }
                        {
                            AbstractCard card = AbstractDungeon.getCardWithoutRng(AbstractCard.CardRarity.COMMON);
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) card, Settings.WIDTH / 2.0F + 350.0F * Settings.xScale, Settings.HEIGHT / 2.0F));
                            AbstractBGDungeon.removeCardFromRewardDeck(card);
                            cards.add(card.name);
                        }

                        logMetric("Winding Halls", "Embrace Madness", cards, null, null, null, null, null, null, this.hpAmt, 0, 0, 0, 0, 0);



                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(CHOICE_2_TEXT);
                        AbstractDungeon.player.heal(this.healAmt);
                        curse = AbstractDungeon.getCardWithoutRng(AbstractCard.CardRarity.CURSE);
                        AbstractBGDungeon.removeCardFromRewardDeck(curse);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard)curse, Settings.WIDTH / 2.0F + 10.0F * Settings.xScale, Settings.HEIGHT / 2.0F));

                        logMetricObtainCardAndHeal("Winding Halls", "Writhe", (AbstractCard)curse, this.healAmt);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2:
                        this.screenNum = 2;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        logMetricMaxHPLoss("Winding Halls", "HP", this.hpAmt);
                        AbstractDungeon.player.damage(new DamageInfo(null,this.hpAmt, DamageInfo.DamageType.HP_LOSS));
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, true);



                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }

                return;
        }

        openMap();
    }
}


