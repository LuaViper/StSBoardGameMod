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

    private static boolean usedGamblingChip=false;
    private static boolean rerollPending=false;
    private boolean gamblingChipButtonWasActive=false;

    private int pendingReward=-1;

    public BGScrapOoze() {
        super(NAME, DIALOG_1, "images/events/scrapOoze.jpg");


        this.imageEventText.setDialogOption(OPTIONS[0] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }


    public void onEnterRoom() {
        pendingReward=0;
        usedGamblingChip=false;
        gamblingChipButtonWasActive=false;
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_OOZE");
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                boolean mustTakeResult = true;
                int random;
                boolean gamblingChipButtonActive = false;
                AbstractRelic r = AbstractDungeon.player.getRelic("BGGambling Chip");
                if (r != null) {
                    if (!usedGamblingChip) {
                        mustTakeResult = false;
                        gamblingChipButtonActive = true;
                    }
                }
                int rerollOption = -1;
                int reachOption = 0;
                int leaveOption = 1;
                if (gamblingChipButtonWasActive) {
                    rerollOption++;
                    reachOption++;
                    leaveOption++;
                    gamblingChipButtonWasActive = false;
                }


                if(buttonPressed==rerollOption){
                    //logger.info("reroll?");
                    usedGamblingChip=true;
                    r = AbstractDungeon.player.getRelic("BGGambling Chip");
                    if(r!=null) r.flash();
                    pendingReward = AbstractDungeon.miscRng.random(1, 6);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(getRewardDescription());
                    this.imageEventText.setDialogOption(OPTIONS[3], true);
                }else if(buttonPressed==reachOption) {
                    //logger.info("reach?");
                    if (pendingReward <= 0) {
                        this.imageEventText.clearAllDialogs();
                        AbstractDungeon.player.damage(new DamageInfo(null, this.dmg));
                        CardCrawlGame.sound.play("ATTACK_POISON");
                        this.totalDamageDealt += this.dmg;
                        pendingReward = AbstractDungeon.miscRng.random(1, 6);
                        if (mustTakeResult) {
                            getReward();
                        } else {
                            //change buttons to reroll/accept/(locked)
                            this.imageEventText.clearAllDialogs();
                            if(gamblingChipButtonActive) {
                                gamblingChipButtonWasActive = true;
                                this.imageEventText.setDialogOption("[Gambling Chip] Reroll.");
                            }
                            this.imageEventText.setDialogOption(getRewardDescription());
                            this.imageEventText.setDialogOption(OPTIONS[3], true);
                        }
                    } else {
                        getReward();
                    }
                }else if(buttonPressed==leaveOption){
                    //logger.info("leave?");
                    //leave
                    AbstractEvent.logMetricTakeDamage("Scrap Ooze", "Fled", this.totalDamageDealt);
                    this.imageEventText.updateBodyText(ESCAPE_MSG);
                    this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                    this.imageEventText.removeDialogOption(1);
                    this.screenNum = 1;
                }
                break;
            case 1:
                openMap();
                break;
        }

    }

    protected String getRewardDescription(){
        //TODO: localization
        if(pendingReward==1 || pendingReward==2){
            return "[Result] Unsuccessful.";
        }else if(pendingReward==3 || pendingReward==4) {
            return "[Result] #gObtain #g2 #gGold.";
        }   else if(pendingReward==5 || pendingReward==6) {
            return "[Result] #gObtain #ga #gRelic.";
        }
        return "Error: Didn't roll 1-6.";
    }

    protected void getReward(){
            if (pendingReward == 3 || pendingReward == 4) {
                this.imageEventText.updateBodyText(SUCCESS_GOLD_MSG);
                AbstractEvent.logMetricGainGoldAndDamage("Scrap Ooze", "Success", 2, this.totalDamageDealt);
                AbstractDungeon.player.gainGold(2);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[3]);
                this.screenNum = 1;
                return;
            }else if (pendingReward == 5 || pendingReward == 6) {
                this.imageEventText.updateBodyText(SUCCESS_MSG);
                AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(
                        AbstractDungeon.returnRandomRelicTier());
                AbstractEvent.logMetricObtainRelicAndDamage("Scrap Ooze", "Success", r, this.totalDamageDealt);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[3]);
                this.screenNum = 1;
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);
                return;
            }
            pendingReward=0;
            this.imageEventText.updateBodyText(FAIL_MSG);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[4] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
            this.imageEventText.setDialogOption(OPTIONS[3]);
        }
}


