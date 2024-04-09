package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.potions.BGGamblersBrew;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: die relics currently work automatically; maybe give player option to take curse instead

public class BGTheMausoleum extends AbstractImageEvent {
    public static final String ID = "BGTheMausoleum";

    private static final Logger logger = LogManager.getLogger(BGTheMausoleum.class.getName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGTheMausoleum");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String SUCCESS_RESULT = DESCRIPTIONS[2];
    private static final String UNLUCKY_RESULT = DESCRIPTIONS[1];
    private static final String LEAVE_RESULT = DESCRIPTIONS[3];

    private int screenNum = 0;
    private boolean pickCard = false;
    private int pendingReward=0;
    private boolean usedGamblingChip=false;
    private boolean gamblingChipButtonWasActive=false;

    private AbstractRelic reward=null;

    public BGTheMausoleum() {
        super(NAME, DIALOG_1, "images/events/mausoleum.jpg");



        pendingReward=0;
        usedGamblingChip=false;
        gamblingChipButtonWasActive=false;

        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }


    public void onEnterRoom() {
        pendingReward=0;
        usedGamblingChip=false;
        gamblingChipButtonWasActive=false;

        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FORGE");
        }
    }


    protected void buttonEffect(int buttonPressed) {
        AbstractCard pain;
        switch (this.screenNum) {
            case 0:
                boolean mustTakeResult = true;
                int random;
                boolean gamblingChipButtonActive = false;

                int rerollbutton=-1;
                int relicbutton=0;
                int leavebutton=1;

                if(buttonPressed==relicbutton) {
                        getReward();

                }else if(buttonPressed==leavebutton){

                    this.screenNum = 2;
                    logMetricIgnored("The Mausoleum");
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.updateBodyText(LEAVE_RESULT);
                    this.imageEventText.setDialogOption(OPTIONS[2]);

                }

                return;
            case 999:
                screenNum=2;
                int slot=BGGamblersBrew.doesPlayerHaveGamblersBrew();
                if(slot>-1 && buttonPressed==1) {
                    AbstractDungeon.topPanel.destroyPotion(slot);
                    getReward(false, true);
                }else{
                    getReward(false,false);
                }
                break;
        }
        openMap();
    }

    protected String getRewardDescription(){
        //TODO: localization
        if(pendingReward<=3){
            return "[Result] #pUnlucky! Relic and Curse.";
        }else if(pendingReward>=4) {
            return "[Result] #gLucky! Relic. No Curse.";
        }
        return "Error: Didn't roll 1-6.";
    }

    public void getReward() {
        getReward(true,false);
    }
    public void getReward(boolean doWeRoll,boolean didPlayerForceWin){
        if (reward == null) {
            reward = AbstractDungeon.returnRandomScreenlessRelic(
                    AbstractDungeon.returnRandomRelicTier());
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), reward);
        }

        boolean playerWins=false;
        if(doWeRoll) {
            int random = AbstractDungeon.miscRng.random(1, 6);
            logger.info("Rolled a "+random);
            if (random >= 4) {
                playerWins = true;
            } else if (random == 3 && AbstractDungeon.player.hasRelic("BGTheAbacus")) {
                AbstractDungeon.player.getRelic("BGTheAbacus").flash();
                playerWins = true;
            } else if (random == 1 && AbstractDungeon.player.hasRelic("BGToolbox")) {
                AbstractDungeon.player.getRelic("BGToolbox").flash();
                playerWins = true;
            }
            if (!playerWins) {
                AbstractRelic r = AbstractDungeon.player.getRelic("BGGambling Chip");
                if (r != null) {
                    r.flash();
                    random = AbstractDungeon.miscRng.random(1, 6);
                    if (random >= 4) {
                        playerWins = true;
                    } else if (random == 3 && AbstractDungeon.player.hasRelic("BGTheAbacus")) {
                        AbstractDungeon.player.getRelic("BGTheAbacus").flash();
                        playerWins = true;
                    } else if (random == 1 && AbstractDungeon.player.hasRelic("BGToolbox")) {
                        AbstractDungeon.player.getRelic("BGToolbox").flash();
                        playerWins = true;
                    }
                }
            }
        }else{
            playerWins=didPlayerForceWin;
        }
        if (!playerWins) {
            if(doWeRoll && BGGamblersBrew.doesPlayerHaveGamblersBrew()>-1) {
                screenNum = 999;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[2]);
                this.imageEventText.setDialogOption(OPTIONS[3]);
                this.imageEventText.updateBodyText(UNLUCKY_RESULT);
                return;
            }else {
                AbstractCard pain = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) pain, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                AbstractBGDungeon.removeCardFromRewardDeck(pain);
                logMetricObtainCardAndRelic("The Mausoleum", "Rummage", (AbstractCard) pain, reward);
                if(doWeRoll)this.imageEventText.updateBodyText(UNLUCKY_RESULT);
            }
        } else {
            logMetricObtainRelic("The Mausoleum", "Rummage", reward);
            if(doWeRoll)this.imageEventText.updateBodyText(SUCCESS_RESULT);
        }
        this.screenNum = 2;
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }
}


