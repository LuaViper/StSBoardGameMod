package BoardGame.events;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.screen.RelicTradingScreen;
import basemod.BaseMod;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

//Point of interest: Leftover Block at the end of combat will persist into an event cheated in with console commands.

public class BGTheMoaiHead
        extends AbstractImageEvent implements LockRelicsEvent {
    public static final String ID = "BGTheMoaiHead";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGTheMoaiHead");
    public boolean reliclock=false;
    public boolean relicsLocked(){
        return reliclock;
    }

    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final float HP_LOSS_PERCENT = 0.125F;

    private static final float A_2_HP_LOSS_PERCENT = 0.18F;

    private int hpAmt = 0;

    private static final int goldAmount = 333;

    private static final String INTRO_BODY = DESCRIPTIONS[0];

    private int screenNum = 0;

    public BGTheMoaiHead() {
        super(NAME, INTRO_BODY, "images/events/moaiHead.jpg");
        reliclock=true;
        if (!AbstractBGRelic.getAllPayableRelics().isEmpty()) {
            this.imageEventText.setDialogOption(OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 1: //heal+dmg
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth)
                            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
                        AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
                        AbstractDungeon.player.damage(new DamageInfo(null, 2));
                        //TODO: logMetric
                        logMetricHealAndLoseMaxHP("The Moai Head", "Heal", AbstractDungeon.player.maxHealth, 2);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        reliclock=false;
                        return;
                    case 0: //offer relic
                        //TODO: logMetric
                        RelicTradingScreen.RelicTradingAction action = (relic) -> {
                            logMetricGainGoldAndLoseRelic("The Moai Head", "Gave Relic", (AbstractRelic) new GoldenIdol(), 333);
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            this.screenNum = 1;
                            AbstractDungeon.player.loseRelic(relic.relicId);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(333));
                            AbstractDungeon.player.gainGold(relic.getPrice());
                            this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                            this.imageEventText.clearRemainingOptions();
                            reliclock=false;
                        };
                        //TODO: localization
                        BaseMod.openCustomScreen(RelicTradingScreen.Enum.RELIC_TRADING, action, "Choose a Relic to sacrifice.", false);
                        return;
                }
                reliclock=false;
                logMetricIgnored("The Moai Head");
                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                this.screenNum = 1;
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                this.imageEventText.clearRemainingOptions();
                return;
        }
        openMap();
    }
}
