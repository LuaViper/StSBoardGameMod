package BoardGame.events;

import BoardGame.relics.AbstractBGRelic;
import BoardGame.screen.RelicTradingScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

public class BGFaceTrader
        extends AbstractImageEvent implements LockRelicsEvent {
    public boolean reliclock=false;
    public boolean relicsLocked(){
        return reliclock;
    }

    public static final String ID = "BGFaceTrader";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGFaceTrader");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static int goldReward;

    private static int damage;

    private CurScreen screen = CurScreen.INTRO;

    private AbstractRelic givenRelic;
    private enum CurScreen {
        INTRO, MAIN, RESULT;
    }

    public BGFaceTrader() {
        super(NAME, DESCRIPTIONS[0], "images/events/facelessTrader.jpg");
        reliclock=true;
        if (AbstractDungeon.ascensionLevel >= 15) {
            goldReward = 50;
        } else {
            goldReward = 75;
        }
        damage = AbstractDungeon.player.maxHealth / 10;
        if (damage == 0)
            damage = 1;
        this.imageEventText.setDialogOption(OPTIONS[4]);
    }

    protected void buttonEffect(int buttonPressed) {
        AbstractRelic r;
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0,OPTIONS[0]);
                        //this.imageEventText.setDialogOption(OPTIONS[2]);
                        this.screen = CurScreen.MAIN;
                        break;
                }
                return;
            case MAIN:
                switch (buttonPressed) {
                    case 0:
                        relicReward();
                        boolean softlockCheck=false;
                        if(givenRelic.relicId.equals("BGOld Coin")){
                            if(AbstractBGRelic.getAllPayableRelics().isEmpty()){
                                softlockCheck=true;
                            }
                        }
                        if(!softlockCheck) {
                            //TODO: different logmetric
                            RelicTradingScreen.RelicTradingAction action = (relic) -> {
                                if (relic.relicId != givenRelic.relicId) {
                                    this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                                } else {
                                    this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                                }
                                AbstractDungeon.player.loseRelic(relic.relicId);
                                //TODO: there's a different logmetric for specifying which relic was given away
                                logMetricObtainRelic("FaceTrader", "Trade", givenRelic);
                                reliclock = false;
                            };
                            //TODO: localization
                            BaseMod.openCustomScreen(RelicTradingScreen.Enum.RELIC_TRADING, action, "Choose a Relic to exhange.", false);
                        }else{
                            this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        }

                        break;
                    case 1:
                        //TODO: different logmetric
                        logMetricGainGoldAndDamage("FaceTrader", "Touch", goldReward, damage);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.effectList.add(new RainingGoldEffect(goldReward));
                        AbstractDungeon.player.gainGold(goldReward);
                        AbstractDungeon.player.damage(new DamageInfo(null, damage));
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        break;
                    case 2:
                        logMetric("Leave");
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[3]);
                this.screen = CurScreen.RESULT;
                return;
        }
        openMap();
    }



    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric("FaceTrader", actionTaken);
    }

    private void relicReward() {
        this.givenRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
        //this.givenRelic=new BGOldCoin();
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.28F, Settings.HEIGHT / 2.0F, this.givenRelic);
    }
}
