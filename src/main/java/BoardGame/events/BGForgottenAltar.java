package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.AbstractBGRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BGForgottenAltar extends AbstractImageEvent
                        implements LockRelicsEvent {
    public static final String ID = "BGForgottenAltar";

    public boolean reliclock=false;
    public boolean relicsLocked(){
        return reliclock;
    }
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGForgottenAltar");

    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];

    private static final String DIALOG_2 = DESCRIPTIONS[1];

    private static final String DIALOG_3 = DESCRIPTIONS[2];

    private static final String DIALOG_4 = DESCRIPTIONS[3];


    private int hpLoss=2;
    private AbstractRelic randomRelic;


    public BGForgottenAltar() {
        super(NAME, DIALOG_1, "images/events/forgottenAltar.jpg");
        reliclock=true;
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.addAll(AbstractDungeon.player.relics);
        Collections.shuffle(relics, new Random(AbstractDungeon.miscRng.randomLong()));
        randomRelic=null;
        for(AbstractRelic r : relics) {
            if(!(r instanceof AbstractBGRelic)){
                randomRelic=r;
                break;
            }
            AbstractBGRelic bgr=(AbstractBGRelic)r;
            if(bgr.usableAsPayment()){
                randomRelic=bgr;
                break;
            }
        }
        if(randomRelic!=null)
            this.imageEventText.setDialogOption(OPTIONS[0] + randomRelic.name + OPTIONS[1]);
        else
            this.imageEventText.setDialogOption(OPTIONS[2], true);
        this.imageEventText.setDialogOption(OPTIONS[3]);
        this.imageEventText.setDialogOption(OPTIONS[5]);
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON)
            CardCrawlGame.sound.play("EVENT_FORGOTTEN");
    }

    protected void buttonEffect(int buttonPressed) {
        Decay decay;
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        gainChalice();
                        showProceedScreen(DIALOG_2);
                        CardCrawlGame.sound.play("HEAL_1");
                        break;
                    case 1:
                        AbstractDungeon.player.damage(new DamageInfo(null, this.hpLoss));
                        CardCrawlGame.sound.play("ORB_LIGHTNING_PASSIVE");
                        showProceedScreen(DIALOG_3);
                        logMetricDamageAndMaxHPGain("Forgotten Altar", "Shed Blood", this.hpLoss, 5);
                        break;
                    case 2:
                        CardCrawlGame.sound.play("ATTACK_PIERCING_WAIL");
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);

                        AbstractCard curse = AbstractBGDungeon.DrawFromCursesRewardDeck();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                        showProceedScreen(DIALOG_4);
                        logMetricObtainCard("Forgotten Altar", "Smashed Altar", curse);
                        break;
                }
                return;
        }
        openMap();
    }

    public void gainChalice() {
        //TODO: other events should maybe run onUnequip as well
        randomRelic.onUnequip();
        AbstractDungeon.player.loseRelic(randomRelic.relicId);
        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(
                AbstractDungeon.returnRandomRelicTier());
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);

        logMetricRelicSwap("Forgotten Altar", "Gave Relic", r.makeCopy(), randomRelic.makeCopy());

    }
}


