package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.potions.BGGamblersBrew;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: die relics currently work automatically; maybe give player option to take curse instead

public class BGAccursedBlacksmith extends AbstractImageEvent {
    public static final String ID = "BGAccursed Blacksmith";

    private static final Logger logger = LogManager.getLogger(BGAccursedBlacksmith.class.getName());
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
    private int pendingReward=0;
    private boolean usedGamblingChip=false;
    private boolean gamblingChipButtonWasActive=false;

    private AbstractRelic reward=null;

    public BGAccursedBlacksmith() {
        super(NAME, DIALOG_1, "images/events/blacksmith.jpg");

        if (AbstractDungeon.player.masterDeck.hasUpgradableCards().booleanValue()) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[4], true);
        }

        pendingReward=0;
        usedGamblingChip=false;
        gamblingChipButtonWasActive=false;

        this.imageEventText.setDialogOption(OPTIONS[1]);
        //this.imageEventText.setDialogOption(OPTIONS[2]);
    }


    public void onEnterRoom() {
        pendingReward=0;
        usedGamblingChip=false;
        gamblingChipButtonWasActive=false;

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
                boolean mustTakeResult = true;
                int random;
                boolean gamblingChipButtonActive = false;
//                AbstractRelic r = AbstractDungeon.player.getRelic("BGGambling Chip");
//                if (r != null) {
//                    if (!usedGamblingChip) {
//                        mustTakeResult = false;
//                        gamblingChipButtonActive = true;
//                    }
//                }
                int rerollbutton=-1;
                int upgradebutton=0;
                int relicbutton=1;
                int leavebutton=2;
//                if(gamblingChipButtonWasActive){
//                    gamblingChipButtonWasActive = false;
//                    rerollbutton++;
//                    upgradebutton++;relicbutton++;leavebutton++;
//                }
                if(buttonPressed==upgradebutton) {
                    //logger.info("upgrade?");
                    int damageAmount = (int) (2);
                    CardCrawlGame.sound.play("ATTACK_POISON");
                    AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));

                    this.pickCard = true;
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                            .getUpgradableCards(), 1, OPTIONS[3], true, false, false, false);

                    this.imageEventText.updateBodyText(FORGE_RESULT);
                    this.screenNum = 2;
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[2]);
                }else if(buttonPressed==relicbutton) {
                    //logger.info("relic?");
//                    if(pendingReward==0){
//                        pendingReward=AbstractDungeon.miscRng.random(1, 6);
//                        if(mustTakeResult){
//                            getReward();
//                        }else{
//                            this.imageEventText.clearAllDialogs();
//                            if(gamblingChipButtonActive) {
//                                gamblingChipButtonWasActive = true;
//                                this.imageEventText.setDialogOption("[Gambling Chip] Reroll.");
//                            }
//                            if (AbstractDungeon.player.masterDeck.hasUpgradableCards().booleanValue()) { this.imageEventText.setDialogOption(OPTIONS[0],true);    } else {   this.imageEventText.setDialogOption(OPTIONS[4], true);     }
//                            this.imageEventText.setDialogOption(getRewardDescription());
//                            this.imageEventText.setDialogOption(OPTIONS[2], true);
//
//                        }
//                    }else {
                        getReward();
//                    }

                }else if(buttonPressed==leavebutton){

                    this.screenNum = 2;
                    logMetricIgnored("Accursed Blacksmith");
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
                //TODO: if player wagers a die relic, does it still (incorrectly) take effect here?
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
                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.imageEventText.updateBodyText(RUMMAGE_RESULT + CURSE_RESULT2);
                return;
            }else {
                AbstractCard pain = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) pain, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                AbstractBGDungeon.removeCardFromRewardDeck(pain);
                logMetricObtainCardAndRelic("Accursed Blacksmith", "Rummage", (AbstractCard) pain, reward);
                if(doWeRoll)this.imageEventText.updateBodyText(RUMMAGE_RESULT + CURSE_RESULT2);
            }
        } else {
            logMetricObtainRelic("Accursed Blacksmith", "Rummage", reward);
            if(doWeRoll)this.imageEventText.updateBodyText(RUMMAGE_RESULT);
        }
        this.screenNum = 2;
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }
}


