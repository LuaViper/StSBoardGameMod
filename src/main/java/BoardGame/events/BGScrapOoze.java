package BoardGame.events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGScrapOoze
        extends AbstractImageEvent {
    private static final Logger logger = LogManager.getLogger(BGScrapOoze.class.getName());
    public static final String ID = "BGScrap Ooze";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGScrap Ooze");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private int relicObtainChance = 66;
    private int dmg = 1;
    private int totalDamageDealt = 0;
    private int screenNum = 0;
    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String FAIL_MSG = DESCRIPTIONS[1];
    private static final String SUCCESS_GOLD_MSG = DESCRIPTIONS[2];
    private static final String SUCCESS_MSG = DESCRIPTIONS[3];
    private static final String ESCAPE_MSG = DESCRIPTIONS[4];

    public BGScrapOoze() {
        super(NAME, DIALOG_1, "images/events/scrapOoze.jpg");


        this.imageEventText.setDialogOption(OPTIONS[0] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_OOZE");
        }
    }

    protected void buttonEffect(int buttonPressed) {
        int random;
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.player.damage(new DamageInfo(null, this.dmg));
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        this.totalDamageDealt += this.dmg;
                        random = AbstractDungeon.miscRng.random(1, 6);

                        if (random == 3 || random == 4) {
                            this.imageEventText.updateBodyText(SUCCESS_GOLD_MSG);
                            AbstractEvent.logMetricGainGoldAndDamage("Scrap Ooze", "Success", 2, this.totalDamageDealt);
                            AbstractDungeon.player.gainGold(2);
                            this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                            this.imageEventText.removeDialogOption(1);
                            this.screenNum = 1;

                            break;
                        }else if (random == 5 || random == 6) {
                            this.imageEventText.updateBodyText(SUCCESS_MSG);
                            AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(
                                    AbstractDungeon.returnRandomRelicTier());
                            AbstractEvent.logMetricObtainRelicAndDamage("Scrap Ooze", "Success", r, this.totalDamageDealt);
                            this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                            this.imageEventText.removeDialogOption(1);
                            this.screenNum = 1;
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);

                            break;
                        }

                        this.imageEventText.updateBodyText(FAIL_MSG);

                        this.dmg++;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);


                        this.imageEventText.updateDialogOption(1, OPTIONS[3]);
                        break;


                    case 1:
                        AbstractEvent.logMetricTakeDamage("Scrap Ooze", "Fled", this.totalDamageDealt);
                        this.imageEventText.updateBodyText(ESCAPE_MSG);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.removeDialogOption(1);
                        this.screenNum = 1;
                        break;
                }
                logger.info("ERROR: case " + buttonPressed + " should never be called");
                break;


            case 1:
                openMap();
                break;
        }
    }
}


