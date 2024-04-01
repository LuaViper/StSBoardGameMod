package BoardGame.events;

import BoardGame.potions.BGGamblersBrew;
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


    private int pendingReward=-1;

    public BGScrapOoze() {
        super(NAME, DIALOG_1, "images/events/scrapOoze.jpg");

        this.imageEventText.setDialogOption(OPTIONS[0] + this.dmg + OPTIONS[1] + this.relicObtainChance + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    private boolean alreadyUsedGamblingChip,alreadyUsedTheAbacus,alreadyUsedToolbox;
    private int leaveIndex,takePrizeIndex,gamblingChipIndex, theAbacusIndex,toolboxIndex,potionIndex;
    public void onEnterRoom() {
        pendingReward=0;
        alreadyUsedToolbox=alreadyUsedGamblingChip=alreadyUsedTheAbacus=false;
        potionIndex=theAbacusIndex=toolboxIndex=gamblingChipIndex=-1;
        takePrizeIndex=0;
        leaveIndex=1;
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_OOZE");
        }
    }



    protected void buttonEffect(int buttonPressed) {
        //TODO: need to create buttons AFTER we roll for prize
        // but also need to know which button we clicked previously
        // ...i think button indexes persist? so move setup to after prize check
        switch (this.screenNum) {
            case 0:
                takePrizeIndex=0;

                this.imageEventText.clearAllDialogs();



                AbstractRelic r;
                if(buttonPressed==gamblingChipIndex){
                    //logger.info("reroll?");
                    alreadyUsedGamblingChip=true;
                    r = AbstractDungeon.player.getRelic("BGGambling Chip");
                    if(r!=null) r.flash();
                    buttonEffect(999);
                    return;
                }else if(buttonPressed==takePrizeIndex || buttonPressed == 999) {   //999 if we clicked Gambling Chip to reroll
                    if (pendingReward <= 2 || buttonPressed == 999) {               // ...so reroll, but don't take damage
                        if(buttonPressed!=999) {
                            AbstractDungeon.player.damage(new DamageInfo(null, this.dmg));
                            CardCrawlGame.sound.play("ATTACK_POISON");
                            this.totalDamageDealt += this.dmg;
                        }
                        pendingReward = AbstractDungeon.miscRng.random(1, 6);
                        showReward();
                        setupButtons();
                    } else {
                        takeReward();
                    }
                }else if(buttonPressed==leaveIndex){
                    //logger.info("leave?");
                    //leave
                    AbstractEvent.logMetricTakeDamage("Scrap Ooze", "Fled", this.totalDamageDealt);
                    this.imageEventText.updateBodyText(ESCAPE_MSG);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                    this.screenNum = 1;
                }else if(buttonPressed == theAbacusIndex){
                    alreadyUsedTheAbacus=true;
                    r = AbstractDungeon.player.getRelic("BGTheAbacus");
                    if (r != null) {
                        r.flash();
                        pendingReward+=1;if(pendingReward>6)pendingReward=1;
                        takeReward();
                    }
                }else if(buttonPressed == toolboxIndex){
                    alreadyUsedToolbox=true;
                    r = AbstractDungeon.player.getRelic("BGToolbox");
                    if (r != null) {
                        r.flash();
                        pendingReward-=1;if(pendingReward<1)pendingReward=6;
                        takeReward();
                    }
                }else if(buttonPressed == potionIndex) {
                    int slot = BGGamblersBrew.doesPlayerHaveGamblersBrew();
                    if (slot>-1) {
                        if (buttonPressed == potionIndex) {
                            AbstractDungeon.topPanel.destroyPotion(slot);
                            this.imageEventText.clearAllDialogs();
                            this.imageEventText.setDialogOption(OPTIONS[12]);
                            this.imageEventText.setDialogOption(OPTIONS[13]);
                            this.screenNum = 200;
                            return;
                        }
                    }
                }

                break;
            case 1:
                openMap();
                break;
            case 200:   //gamblers brew
                if(buttonPressed==0){
                    pendingReward=3;takeReward();
                }else{
                    pendingReward=5;takeReward();
                }
        }

    }

    protected void setupButtons(){
        leaveIndex=potionIndex=theAbacusIndex=toolboxIndex=gamblingChipIndex=-1;
        int i = 1;
        if(pendingReward<=2){
            leaveIndex=i;
            i += 1;
        }
        if(pendingReward>0) {
            int pendingRewardTextIndex=(pendingReward+1)/2;
            if (AbstractDungeon.player.hasRelic("BGGambling Chip")) {
                if (!alreadyUsedGamblingChip) {
                    this.imageEventText.setDialogOption("[Gambling Chip] Reroll.");
                    gamblingChipIndex = i;
                    i += 1;
                }
            }
            if (AbstractDungeon.player.hasRelic("BGTheAbacus")) {
                if(!alreadyUsedTheAbacus) {
                    int r2 = pendingReward + 1;
                    if (r2 > 6) r2 = 1;
                    if (r2 == 1 || r2 == 3 || r2 == 5) {  //only show option if new reward is (A) successful, and (B) different
                        this.imageEventText.setDialogOption("[The Abacus] " + OPTIONS[(r2 + 1) / 2 + 10]);
                        theAbacusIndex = i;
                        i += 1;
                    }
                }
            }
            if (AbstractDungeon.player.hasRelic("BGToolbox")) {
                if(!alreadyUsedToolbox) {
                    int r2 = pendingReward - 1;
                    if (r2 < 1) r2 = 6;
                    if (r2 == 2 || r2 == 4 || r2 == 6) {
                        this.imageEventText.setDialogOption("[Toolbox] " + OPTIONS[(r2 + 1) / 2 + 10]);
                        toolboxIndex = i;
                        i += 1;
                    }
                }
            }
            if (BGGamblersBrew.doesPlayerHaveGamblersBrew() > -1) {
                this.imageEventText.setDialogOption("[Gambler's Brew] Choose a reward.");
                potionIndex = i;
                i += 1;
            }
        }
        int x=0;
        x+=1;
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

    protected void showReward(){
        if (pendingReward == 3 || pendingReward == 4) {
            this.imageEventText.updateBodyText(SUCCESS_GOLD_MSG);
            this.imageEventText.updateDialogOption(0,OPTIONS[5]);
            return;
        }else if (pendingReward == 5 || pendingReward == 6) {
            this.imageEventText.updateBodyText(SUCCESS_MSG);
            this.imageEventText.updateDialogOption(0,OPTIONS[6]);
            return;
        }
        this.imageEventText.updateBodyText(FAIL_MSG);
        this.imageEventText.updateDialogOption(0,OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }
    protected void takeReward() {
        if (pendingReward == 1 || pendingReward == 2) {
            AbstractDungeon.player.damage(new DamageInfo(null, this.dmg));
            CardCrawlGame.sound.play("ATTACK_POISON");
            this.totalDamageDealt += this.dmg;
            pendingReward = AbstractDungeon.miscRng.random(1, 6);
            showReward();
            setupButtons();
            return;
        }else if (pendingReward == 3 || pendingReward == 4) {
            AbstractEvent.logMetricGainGoldAndDamage("Scrap Ooze", "Success", 2, this.totalDamageDealt);
            AbstractDungeon.player.gainGold(2);
            this.screenNum = 1;
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[3]);
            openMap();
            return;
        }else if (pendingReward == 5 || pendingReward == 6) {
            this.imageEventText.updateBodyText(SUCCESS_MSG);
            AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(
                    AbstractDungeon.returnRandomRelicTier());
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);
            AbstractEvent.logMetricObtainRelicAndDamage("Scrap Ooze", "Success", r, this.totalDamageDealt);
            this.screenNum=1;
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[3]);
            openMap();
            return;
        }
    }



}


