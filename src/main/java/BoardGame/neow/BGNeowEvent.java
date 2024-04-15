package BoardGame.neow;

//TODO: if possible, move quickstart handlers to BGNeowQuickStart
//TODO: several rewards can softlock if player doesn't have enough cards (not just limited to Quick Start)

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.multicharacter.BGMultiCharacter;
import BoardGame.multicharacter.MultiCharacterSelectScreen;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.scene.LevelTransitionTextOverlayEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static BoardGame.neow.BGNeowQuickStart.clearAllRewards;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.*;


public class BGNeowEvent
        extends AbstractEvent
{
    private static final Logger logger = LogManager.getLogger(BGNeowEvent.class.getName());
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("Neow Event");

    public static final String[] NAMES = characterStrings.NAMES;
    public static final String[] TEXT = characterStrings.TEXT;
    public static final String[] OPTIONS = characterStrings.OPTIONS;

    private static final CharacterStrings extraStrings = CardCrawlGame.languagePack.getCharacterString("BoardGame:BGNeow Reward");
    public static final String[] EXTRA = extraStrings.TEXT;
    public static final String choose_a_card = CardCrawlGame.languagePack.getCharacterString("Neow Reward").TEXT[0];
    public static final String text22 = CardCrawlGame.languagePack.getCharacterString("Neow Reward").TEXT[22];
    private AnimatedNpc npc;
    public static final String NAME = NAMES[0];
    private int screenNum = 2;
    private int bossCount;
    private boolean setPhaseToEvent = false;
    private ArrayList<BGNeowReward> rewards = new ArrayList<>();
    //public static Random rng = null;

    private boolean pickCard = false;
    //public static boolean waitingToSave = false;
    private static final float DIALOG_X = 1100.0F * Settings.xScale, DIALOG_Y = AbstractDungeon.floorY + 60.0F * Settings.yScale;


    public enum NeowCardOperation
    {  NONE,TRANSFORM,UPGRADE,REMOVE };

    private static NeowCardOperation cardOperation=NeowCardOperation.NONE;


    public BGNeowEvent(boolean isDone) {
        NeowEvent.waitingToSave = false;

        if (this.npc == null) {
            this.npc = new AnimatedNpc(1534.0F * Settings.xScale, AbstractDungeon.floorY - 60.0F * Settings.yScale, "images/npcs/neow/skeleton.atlas", "images/npcs/neow/skeleton.json", "idle");
        }

        this.roomEventText.clear();


        if (!Settings.isEndless || AbstractDungeon.floorNum <= 1)
        {
            if (Settings.isStandardRun() || (Settings.isEndless && AbstractDungeon.floorNum <= 1)) {
                this.bossCount = CardCrawlGame.playerPref.getInteger(AbstractDungeon.player.chosenClass.name() + "_SPIRITS", 0);
                AbstractDungeon.bossCount = this.bossCount;
            } else if (Settings.seedSet) {
                this.bossCount = 1;
            } else {
                this.bossCount = 0;
            }
        }
        this.body = "";

        if(false){
        //if (Settings.isEndless && AbstractDungeon.floorNum > 1) {

            talk(TEXT[MathUtils.random(12, 14)]);
            this.screenNum = 999;
            this.roomEventText.addDialogOption(OPTIONS[0]);
        } else if (shouldSkipNeowDialog()) {
            this.screenNum = 10;
            talk(TEXT[10]);     //Time for a CHALLENGE...
            this.roomEventText.addDialogOption(OPTIONS[1]);
        }
        else if (!isDone) {     //normally, boardgame starts HERE
//            this.screenNum = 2;       //standard intro
//            talk(TEXT[MathUtils.random(1, 3)]);
//            this.roomEventText.addDialogOption(OPTIONS[1]);

             if (AbstractDungeon.player instanceof BGMultiCharacter) {
                BaseMod.openCustomScreen(MultiCharacterSelectScreen.Enum.MULTI_CHARACTER_SELECT);
            }

            this.screenNum = -1;    //disclaimer intro
            this.roomEventText.addDialogOption(EXTRA[68]);
            //character screen is still open, we can't set disclaimer text until the player clicks proceed

            AbstractDungeon.topLevelEffects.add(new LevelTransitionTextOverlayEffect(AbstractDungeon.name, AbstractDungeon.levelNum, true));
        } else {
            this.screenNum = 99;
            talk(TEXT[8]);
            this.roomEventText.addDialogOption(OPTIONS[3]);
        }

        this.hasDialog = true;
        this.hasFocus = true;



//        this.roomEventText.updateBodyText("");
//        playSfx();
//        this.roomEventText.addDialogOption(EXTRA[0]);
//        talk(TEXT[MathUtils.random(1, 3)]);


    }

    public BGNeowEvent() {
        this(false);
    }

    private boolean shouldSkipNeowDialog() {
        if (Settings.seedSet && !Settings.isTrial && !Settings.isDailyRun) {
            return false;
        }
        return !Settings.isStandardRun();
    }


    public void update() {
        super.update();

        if (this.pickCard &&        //pickCard only applies to dailyBlessing
                !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                group.addToBottom(c.makeCopy());
            }
            AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, TEXT[11]);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

        //logger.info("NEOW UPDATE SPAM "+this.screenNum+" "+BGNeowQuickStart.rewardIndex+" "+AbstractDungeon.isScreenUp);
        if(this.screenNum==100) {
            if (BGNeowQuickStart.rewardIndex == 3 || BGNeowQuickStart.rewardIndex == 10) {
                if (!AbstractDungeon.isScreenUp) {
                    int effectiveRewardIndex = BGNeowQuickStart.rewardIndex - 1;
                    int totalCards = BGNeowQuickStart.quickStartQuantities[effectiveRewardIndex][BGNeowQuickStart.actNumber - 2];
                    BGNeowQuickStart.rewardCounter += 1;
                    if (BGNeowQuickStart.rewardCounter < totalCards) {
                        AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22 + " (" + (BGNeowQuickStart.rewardCounter + 1) + "/" + totalCards + ")");
                    }
                }
            }else if(BGNeowQuickStart.rewardIndex == 8){
                if (!AbstractDungeon.isScreenUp) {
                    int effectiveRewardIndex = BGNeowQuickStart.rewardIndex - 1;
                    int totalCards = BGNeowQuickStart.quickStartQuantities[effectiveRewardIndex][BGNeowQuickStart.actNumber - 2];
                    BGNeowQuickStart.rewardCounter += 1;
                    if (BGNeowQuickStart.rewardCounter < totalCards) {
                        AbstractBGDungeon.forceRareRewards=true;
                        AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22 + (totalCards==2 ? " (2/2)" : ""));
                        AbstractBGDungeon.forceRareRewards=false;
                    }
                }
            }else if(BGNeowQuickStart.rewardIndex == 9){
                if (!AbstractDungeon.isScreenUp) {
                    int effectiveRewardIndex = BGNeowQuickStart.rewardIndex - 1;
                    int numRewards = BGNeowQuickStart.quickStartQuantities[effectiveRewardIndex][BGNeowQuickStart.actNumber - 2];
                    BGNeowQuickStart.rewardCounter += 1;
                    //logger.info("Boss Relic Reward Counter: "+BGNeowQuickStart.rewardCounter);
                    if (BGNeowQuickStart.rewardCounter < numRewards) {
                        BGNeowQuickStart.openBossRelicScreen();
                    }
                }


            }

            if(cardOperation!=NeowCardOperation.NONE){
                AbstractCard c;
                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                    switch(cardOperation){
                        case TRANSFORM:
                            for(int i=0;i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i+=1) {
                                c=AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                                AbstractDungeon.transformCard(c, false, NeowEvent.rng);
                                AbstractDungeon.player.masterDeck.removeCard(c);
                                AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                                        AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                            }
                            break;
                        case UPGRADE:
                            for(int i=0;i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i+=1) {
                                c=AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                                c.upgrade();
                                //TODO: if there's more than one card, spread them out
                                AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                            }
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                            break;
                        case REMOVE:
                            for(int i=0;i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i+=1) {
                                c=AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                                AbstractDungeon.player.masterDeck.removeCard(c);
                            }
                            break;
                    }
                    cardOperation=NeowCardOperation.NONE;
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    AbstractDungeon.overlayMenu.cancelButton.hide();


                }
            }

        }


        for (BGNeowReward r : this.rewards) {
            r.update();
        }

        if (!this.setPhaseToEvent) {
            (getCurrRoom()).phase = AbstractRoom.RoomPhase.EVENT;
            this.setPhaseToEvent = true;
        }


        if (!RoomEventDialog.waitForInput) {
            buttonEffect(this.roomEventText.getSelectedOption());
        }


        if (NeowEvent.waitingToSave && !AbstractDungeon.isScreenUp && AbstractDungeon.topLevelEffects.isEmpty() && AbstractDungeon.player
                .relicsDoneAnimating()) {
            boolean doneAnims = true;

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (!r.isDone) {
                    doneAnims = false;

                    break;
                }
            }
            if (doneAnims) {
                NeowEvent.waitingToSave = false;
                SaveHelper.saveIfAppropriate(SaveFile.SaveType.POST_NEOW);
            }
        }
    }

    private void talk(String msg) {
        AbstractDungeon.effectList.add(new InfiniteSpeechBubble(DIALOG_X, DIALOG_Y, msg));
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case -1:    //DISCLAIMER
                this.roomEventText.updateBodyText("");
                playSfx();
                talk(TEXT[MathUtils.random(1, 3)]);
                this.screenNum = 2;
                this.roomEventText.updateDialogOption(0,EXTRA[0]);
                return;


            case 0:     //"Greetings..."
                dismissBubble();
                talk(TEXT[4]);

                if (true) {
                    AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22);
                    blessing();
                } else {
                    miniBlessing();
                }
                return;


            case 1:     //"Hello... again..."
                dismissBubble();
                logger.info(Integer.valueOf(this.bossCount));

                if (false) {
                    miniBlessing();
                } else {
                    AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22);
                    blessing();
                }
                return;


            case 2:

                if (buttonPressed == 0) {
                    AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22);
                    blessing();
                } else {
                    openMap();  //change phase to COMPLETE
                }
                return;


            case 3: //reward chosen
                dismissBubble();
                this.roomEventText.clearRemainingOptions();

                switch (buttonPressed) {
                    case 0:
                        ((BGNeowReward)this.rewards.get(0)).activate();
                        talk(TEXT[8]);  //Granted...
                        break;
                    case 1:
                        ((BGNeowReward)this.rewards.get(1)).activate();
                        talk(TEXT[8]);  //Granted...
                        break;
                    case 2:
                        ((BGNeowReward)this.rewards.get(2)).activate();
                        talk(TEXT[9]);  //Risk... reward...
                        break;
                    case 3:
                        ((BGNeowReward)this.rewards.get(3)).activate();
                        talk(TEXT[9]);  //Risk... reward...
                        break;
                }




                this.screenNum = 99;
                this.roomEventText.clearRemainingOptions();
                this.roomEventText.updateDialogOption(0, EXTRA[38]);
                this.roomEventText.addDialogOption(EXTRA[39]);
                this.roomEventText.addDialogOption(EXTRA[40]);
                this.roomEventText.addDialogOption(EXTRA[41]);
                    //TODO: if we choose a quick start option, set phase back to incomplete (but this tends to mess up other interfaces)

                NeowEvent.waitingToSave = true;
                return;


            case 10:
                dailyBlessing();
                this.roomEventText.clearRemainingOptions();
                this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                this.screenNum = 99;
                return;



            case 99:    //leave event or choose quickskip
                dismissBubble();
                this.roomEventText.clearRemainingOptions();
                switch (buttonPressed) {
                    case 0:
                        this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                        openMap();  //change phase to COMPLETE
                        return;
                    case 1:
                    case 2:
                    case 3:
                        BGNeowQuickStart.actNumber=buttonPressed+1;
                        BGNeowQuickStart.rewardIndex=1;
                        this.screenNum = 100;
                        talk(TEXT[10]);     //Time for a CHALLENGE...
                        this.roomEventText.updateDialogOption(0, EXTRA[42]+BGNeowQuickStart.quickStartQuantities[1][BGNeowQuickStart.actNumber-2]+EXTRA[43]);
                }
                return;
            case 100:
                int numRewards=0;
                int upcomingRewards=0;
                switch(BGNeowQuickStart.rewardIndex){
                    case 1: //gain gold
                        clearAllRewards();
                        int goldAmount=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        CardCrawlGame.sound.play("GOLD_JINGLE");
                        AbstractDungeon.player.gainGold(goldAmount);
                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        this.roomEventText.updateDialogOption(0, EXTRA[44]+upcomingRewards+EXTRA[45]);
                        break;
                    case 2: //card rewards
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        if(numRewards>0) {
                            BGNeowQuickStart.rewardCounter = 0;
                            AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22 + " (1/"+numRewards+")");
                        }
                        BGNeowQuickStart.rewardIndex+=1;
                        this.roomEventText.updateDialogOption(0, EXTRA[46]);
                        this.roomEventText.addDialogOption(EXTRA[47]);
                        break;
                    case 3: //transform
                        if(buttonPressed==0) {
                            cardOperation=NeowCardOperation.TRANSFORM;
                            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                    .getPurgeableCards(), 1, EXTRA[25], false, true, false, false);
                        }
                        this.roomEventText.clearRemainingOptions();
                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        BGNeowQuickStart.rollTheDieReward=-1;
                        if(upcomingRewards==1) {
                            this.roomEventText.updateDialogOption(0, EXTRA[48] + upcomingRewards + EXTRA[49]);
                        }else{
                            this.roomEventText.updateDialogOption(0, EXTRA[48] + upcomingRewards + EXTRA[50]);
                        }
                        break;
                    case 4: //Roll the Die
                        switch (BGNeowQuickStart.rollTheDieReward) {
                            case -1:
                                BGNeowQuickStart.rewardCounter=0;
                                break;
                            case 1: //relic
                                BGNeowQuickStart.rewardCounter+=1;
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2),
                                        AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON));
                                numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                                break;
                            case 2: //transform
                                BGNeowQuickStart.rewardCounter+=1;
                                if(buttonPressed==0){
                                    cardOperation=NeowCardOperation.TRANSFORM;
                                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                            .getPurgeableCards(), 1, EXTRA[25], false, true, false, false);
                                }
                                break;
                            case 3: //card reward + 1G
                                BGNeowQuickStart.rewardCounter+=1;
                                CardCrawlGame.sound.play("GOLD_JINGLE");
                                AbstractDungeon.player.gainGold(1);
                                AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22);
                                numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                                break;
                            case 4: //potion + 2G
                                clearAllRewards();
                                BGNeowQuickStart.rewardCounter+=1;
                                CardCrawlGame.sound.play("GOLD_JINGLE");
                                AbstractDungeon.player.gainGold(2);
                                CardCrawlGame.sound.play("POTION_1");
                                AbstractDungeon.combatRewardScreen.rewards.clear();
                                for (int i = 0; i < 1; i++) {
                                    AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion());
                                }
                                AbstractDungeon.combatRewardScreen.open();
                                (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;

                                int remove = -1;
                                for (int j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
                                    if (((RewardItem) AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
                                        remove = j;
                                        break;
                                    }
                                }
                                if (remove != -1) {
                                    AbstractDungeon.combatRewardScreen.rewards.remove(remove);
                                }
                                numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                                break;
                            case 5: //upgrade
                                BGNeowQuickStart.rewardCounter+=1;
                                cardOperation=NeowCardOperation.UPGRADE;
                                AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                        .getUpgradableCards(), 1, EXTRA[27], true, false, false, false);
                                break;
                            case 6: //remove
                                BGNeowQuickStart.rewardCounter+=1;
                                if(buttonPressed==0){
                                    cardOperation=NeowCardOperation.REMOVE;
                                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                            .getPurgeableCards(), 1, EXTRA[23], false, false, false, true);
                                }
                                break;
                        }
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        //logger.info("RollTheDie reward counter: "+BGNeowQuickStart.rewardCounter);
                        if(BGNeowQuickStart.rewardCounter<numRewards) {
                            int r = monsterRng.random(1, 6);
                            BGNeowQuickStart.rollTheDieReward = r;
                        }
                        BGNeowQuickStart.setRollTheDieButtons(this);    //also handles moving to next reward index

                        break;
                    case 5: //always 1 potion
                        clearAllRewards();
                        CardCrawlGame.sound.play("POTION_1");
                        AbstractDungeon.combatRewardScreen.rewards.clear();
                        for (int i = 0; i < 1; i++) {
                            AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion());
                        }
                        AbstractDungeon.combatRewardScreen.open();
                        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;

                        int remove = -1;
                        for (int j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
                            if (((RewardItem) AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
                                remove = j;
                                break;
                            }
                        }
                        if (remove != -1) {
                            AbstractDungeon.combatRewardScreen.rewards.remove(remove);
                        }

                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        this.roomEventText.updateDialogOption(0, EXTRA[52]+upcomingRewards+EXTRA[53]);
                        break;
                    case 6: //relics
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        for(int i=0;i<numRewards;i+=1){
                            BGNeowQuickStart.rewardCounter+=1;
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2),
                                    AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON));
                        }
                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        this.roomEventText.updateDialogOption(0, EXTRA[54]+upcomingRewards+(upcomingRewards==1 ? EXTRA[55] : EXTRA[56]));
                        break;
                    case 7: //rare cards
                        talk(EXTRA[71]);
                        BGNeowQuickStart.rewardCounter = 0;
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        AbstractBGDungeon.forceRareRewards=true;
                        AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22 + (numRewards==2 ? " (1/2)" : ""));
                        AbstractBGDungeon.forceRareRewards=false;
                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        this.roomEventText.updateDialogOption(0, EXTRA[64]+upcomingRewards+(upcomingRewards==1 ? EXTRA[65] : EXTRA[66]));
                        break;
                    case 8: //boss relics
                        BGNeowQuickStart.rewardCounter = 0;
                        BGNeowQuickStart.openBossRelicScreen();
                        BGNeowQuickStart.rewardIndex+=1;

                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        if(upcomingRewards>0) {
                            //more card rewards
                            this.roomEventText.updateDialogOption(0, EXTRA[44]+upcomingRewards+EXTRA[45]);
                        }else{
                            //skip to upgrade
                            BGNeowQuickStart.rewardIndex=11;
                            upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                            this.roomEventText.updateDialogOption(0, EXTRA[58]+upcomingRewards+EXTRA[59]);
                        }
                        break;
                    case 9: //more cards
                        this.roomEventText.clearRemainingOptions();
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        if(numRewards>0) {
                            BGNeowQuickStart.rewardCounter = 0;
                            AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, text22 + " (1/"+numRewards+")");
                        }
                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        if(upcomingRewards==1) {
                            this.roomEventText.updateDialogOption(0, EXTRA[2]);
                        }else{
                            this.roomEventText.updateDialogOption(0, EXTRA[57]);
                            this.roomEventText.addDialogOption(EXTRA[2]);
                        }
                        this.roomEventText.addDialogOption(EXTRA[47]);
                        break;
                    case 10: //remove
                        this.roomEventText.clearRemainingOptions();
                        AbstractDungeon.combatRewardScreen.rewards.clear();
                        clearAllRewards();
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];

                        int actualRewards=0;
                        if(buttonPressed==0){
                            if(numRewards==2)actualRewards=2; else actualRewards=1;
                        }else if(buttonPressed==1){
                            if(numRewards==2)actualRewards=1; else actualRewards=0;
                        }else if(buttonPressed==2){
                            numRewards=0;
                        }
                        logger.info("numRewards: "+numRewards+" chosen: "+actualRewards);
                        if(actualRewards>0) {
                            String title=actualRewards==2 ? EXTRA[24] : EXTRA[23];
                            cardOperation=NeowCardOperation.REMOVE;
                            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                    .getPurgeableCards(), actualRewards, EXTRA[23], false, false, false, true);
                        }
                        BGNeowQuickStart.rewardIndex+=1;
                        upcomingRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        this.roomEventText.updateDialogOption(0,EXTRA[58]+upcomingRewards+EXTRA[59]);
                        break;

                    case 11: //upgrade
                        //TODO: player can be left in front of a blank choose-boss-relics screen.  not a softlock, but close to it
                        this.roomEventText.clearRemainingOptions();
                        AbstractDungeon.combatRewardScreen.rewards.clear();
                        clearAllRewards();
                        numRewards=BGNeowQuickStart.quickStartQuantities[BGNeowQuickStart.rewardIndex][BGNeowQuickStart.actNumber-2];
                        BGNeowQuickStart.rewardCounter+=1;
                        cardOperation=NeowCardOperation.UPGRADE;
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
                                .getUpgradableCards(), numRewards, EXTRA[67], false, false, false, false);
                        BGNeowQuickStart.rewardIndex+=1;
                        this.roomEventText.updateDialogOption(0, EXTRA[60]);
                        talk(TEXT[8]);  //Granted...
                        break;
                    case 12:
                        //to merchant
                        AbstractDungeon.combatRewardScreen.rewards.clear();
                        clearAllRewards();
                        MapRoomNode node;
                        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                        node = new MapRoomNode(-1, 15);
                        node.room = (AbstractRoom)new ShopRoom();
                        AbstractDungeon.nextRoom = node;
                        CardCrawlGame.music.fadeOutTempBGM();
                        AbstractDungeon.pathX.add(Integer.valueOf(1));
                        AbstractDungeon.pathY.add(Integer.valueOf(15));
                        AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
                        AbstractDungeon.nextRoomTransitionStart();
                        //TODO: somehow prevent player from escaping quickstart by opening map from merchscreen and clicking floor 1
                        break;
                }

                return;
            case 999:
                endlessBlight();
                this.roomEventText.clearRemainingOptions();
                this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                this.screenNum = 99;
                return;
        }
        openMap();  //change phase to COMPLETE

    }




    private void endlessBlight() {
        if (AbstractDungeon.player.hasBlight("DeadlyEnemies")) {
            AbstractBlight tmp = AbstractDungeon.player.getBlight("DeadlyEnemies");
            tmp.incrementUp();
            tmp.flash();
        } else {
            getCurrRoom().spawnBlightAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractBlight)new Spear());
        }

        if (AbstractDungeon.player.hasBlight("ToughEnemies")) {
            AbstractBlight tmp = AbstractDungeon.player.getBlight("ToughEnemies");
            tmp.incrementUp();
            tmp.flash();
        } else {
            getCurrRoom().spawnBlightAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractBlight)new Shield());
        }


        uniqueBlight();
    }


    private void uniqueBlight() {
        AbstractBlight temp = AbstractDungeon.player.getBlight("MimicInfestation");
        if (temp != null) {
            temp = AbstractDungeon.player.getBlight("TimeMaze");
            if (temp != null) {
                temp = AbstractDungeon.player.getBlight("FullBelly");
                if (temp != null) {
                    temp = AbstractDungeon.player.getBlight("GrotesqueTrophy");
                    if (temp != null) {

                        AbstractDungeon.player.getBlight("GrotesqueTrophy").stack();
                    } else {

                        getCurrRoom().spawnBlightAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractBlight)new GrotesqueTrophy());

                    }

                }
                else {

                    getCurrRoom().spawnBlightAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractBlight)new Muzzle());

                }

            }
            else {

                getCurrRoom().spawnBlightAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractBlight)new TimeMaze());

            }

        }
        else {

            getCurrRoom().spawnBlightAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractBlight)new MimicInfestation());
            return;
        }
    }




    private void dailyBlessing() {
        NeowEvent.rng = new Random(Settings.seed);
        dismissBubble();
        talk(TEXT[8]);




        if (ModHelper.isModEnabled("Heirloom")) {
            getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F,


                    AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE));
        }


        boolean addedCards = false;
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);


        if (ModHelper.isModEnabled("Allstar")) {
            addedCards = true;
            for (int i = 0; i < 5; i++) {
                AbstractCard colorlessCard = AbstractDungeon.getColorlessCardFromPool(
                        AbstractDungeon.rollRareOrUncommon(0.5F));
                UnlockTracker.markCardAsSeen(colorlessCard.cardID);
                group.addToBottom(colorlessCard.makeCopy());
            }
        }


        if (ModHelper.isModEnabled("Specialized")) {
            if (!ModHelper.isModEnabled("SealedDeck") && !ModHelper.isModEnabled("Draft")) {
                addedCards = true;
                AbstractCard rareCard = AbstractDungeon.returnTrulyRandomCard();

                UnlockTracker.markCardAsSeen(rareCard.cardID);
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
            } else {
                AbstractCard rareCard = AbstractDungeon.returnTrulyRandomCard();
                for (int i = 0; i < 5; i++) {
                    AbstractCard tmpCard = rareCard.makeCopy();
                    AbstractDungeon.topLevelEffectsQueue.add(new FastCardObtainEffect(tmpCard,


                            MathUtils.random(Settings.WIDTH * 0.2F, Settings.WIDTH * 0.8F),
                            MathUtils.random(Settings.HEIGHT * 0.3F, Settings.HEIGHT * 0.7F)));
                }
            }
        }


        if (addedCards) {
            AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, TEXT[11]);
        }


        if (ModHelper.isModEnabled("Draft")) {
            AbstractDungeon.cardRewardScreen.draftOpen();
        }


        this.pickCard = true;
        if (ModHelper.isModEnabled("SealedDeck")) {
            CardGroup sealedGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);


            for (int i = 0; i < 30; i++) {
                AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity());
                if (!sealedGroup.contains(card)) {
                    sealedGroup.addToBottom(card.makeCopy());
                } else {
                    i--;
                }
            }

            for (AbstractCard c : sealedGroup.group) {
                UnlockTracker.markCardAsSeen(c.cardID);
            }
            AbstractDungeon.gridSelectScreen.open(sealedGroup, 10, OPTIONS[4], false);
        }

        this.roomEventText.clearRemainingOptions();
        this.screenNum = 99;
    }

    private void miniBlessing() {
        logger.info("ERROR: BGNeowReward tried to call a miniBlessing instead of a Blessing!  Panic!");
    }

    private void blessing() {
        logger.info("BLESSING");
        NeowEvent.rng = new Random(Settings.seed);
        logger.info("COUNTER: " + NeowEvent.rng.counter);
        AbstractDungeon.bossCount = 0;
        dismissBubble();
        talk(TEXT[7]);

        //-: no tradeoff
        //C: gain a curse
        //D: lose 2 HP
        //3: lose 3 HP
        //G: lose 3 gold

        //4: gain 4 gold
        //5: gain 5 gold
        //8: gain 8 gold
        //X: gain 10 gold
        //r: remove 1 card
        //R: remove 2 cards
        //t: transform 1 card
        //T: transform 2 cards
        //u: upgrade 1 card
        //U: upgrade 2 RANDOM cards
        //c: choose a card
        //C: get 2 RANDOM cards
        //V: choose 2 cards
        //W: choose a card and gain 5 gold
        //P: get 3 potions
        //?: get 1 RANDOM rare card
        //!: choose a rare card
        //%: gain a relic
        //=: choose a colorless card
        //+: get 2 RANDOM colorless cards
        //): choose 2 colorless cards

        //TODO LATER: match flavor text on card to speech bubble message
        ArrayList<String> rewardoptions=new ArrayList<String>(Arrays.asList(
                "-= -t DX", //colorless unlock
                "-u -r DX",
                "-u -? CX",
                "-= -r D!", //colorless unlock
                "-u -P C!",
                "-5 -? G!", //colorless unlock, apparently
                "-= -? G%", //colorless unlock
                "-u -5 C%",
                "-P -c G%",
                "-r -t D%",
                "-= -u GR", //colorless unlock
                "-t -5 GR",
                "-? -c DR",
                "-? -t CU",
                "-r -5 GU",
                "-u -P DU",
                "-= -P CT", //colorless unlock
                "-r -t 3T",
                "-t -5 C)", //colorless unlock
                "-r -P CW"
        ));



        //OLD REWARD LIST
//        ArrayList<String> rewardoptions=new ArrayList<String>(Arrays.asList(
//                "-4 -r CU",
//                "-u -t D8",
//                "-r -4 DC",
//                "-r -4 DU",
//                "-t -r GU",
//                "-? -4 GR",
//                "-u -? D%",
//                "-u -? DX",
//                "-u -P CT",
//                "-t -P C!",
//                "-u -= DR",
//                "-u -r G+",
//                "-= -P C%",
//                "-= -4 G!",
//                "-= -P D%",
//                "-= -u G%"
//        ));



        Collections.shuffle(rewardoptions, new java.util.Random(NeowEvent.rng.randomLong()));
        String card=rewardoptions.get(0);
        //card="-r -t 3T"; //for debugging only
        //card="-r -P CW"; //for debugging only
        //logger.info("Card: "+card);
        String[] rewards=card.split(" ");
        logger.info("Neow card: "+rewards[0]+" "+rewards[1]+" "+rewards[2]);

        this.rewards.add(new BGNeowReward(rewards[0]));
        this.rewards.add(new BGNeowReward(rewards[1]));
        this.rewards.add(new BGNeowReward(rewards[2]));
        //this.rewards.add(new NeowReward(3));

        this.roomEventText.clearRemainingOptions();
        this.roomEventText.updateDialogOption(0, ((BGNeowReward)this.rewards.get(0)).optionLabel);
        this.roomEventText.addDialogOption(((BGNeowReward)this.rewards.get(1)).optionLabel);
        this.roomEventText.addDialogOption(((BGNeowReward)this.rewards.get(2)).optionLabel);
        //this.roomEventText.addDialogOption(((NeowReward)this.rewards.get(3)).optionLabel);

        this.screenNum = 3;
    }

    private void dismissBubble() {
        for (AbstractGameEffect e : AbstractDungeon.effectList) {
            if (e instanceof InfiniteSpeechBubble) {
                ((InfiniteSpeechBubble)e).dismiss();
            }
        }
    }

    private void playSfx() {
        int roll = MathUtils.random(3);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_NEOW_1A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_NEOW_1B");
        } else if (roll == 2) {
            CardCrawlGame.sound.play("VO_NEOW_2A");
        } else {
            CardCrawlGame.sound.play("VO_NEOW_2B");
        }
    }

    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric(NAME, actionTaken);
    }


    public void render(SpriteBatch sb) {
        this.npc.render(sb);

    }



    public void dispose() {
        super.dispose();
        if (this.npc != null) {
            logger.info("Disposing Neow asset.");
            this.npc.dispose();
            this.npc = null;
        }
    }






//    @SpirePatch2
//            (clz=RoomEventDialog.class,method="render",paramtypez={SpriteBatch.class})
//    public static class TempPatch{
//        @SpirePrefixPatch
//        public static void Insert(RoomEventDialog __instance, SpriteBatch sb, ArrayList<DialogWord> ___words,
//                                  float ___curLineWidth, boolean ___show){
//            if(___words.size()>0){
//                //logger.info("Test: "+___words.get(___words.size()-1).word);
//            }
//            //logger.info("Test: "+___words.size()+" "+___show);
//        }
//    }
//
//    @SpirePatch2
//            (clz=RoomEventDialog.class,method="clear",paramtypez={})
//    public static class TempPatch2{
//        @SpirePrefixPatch
//        public static void Insert(RoomEventDialog __instance, ArrayList<DialogWord> ___words,
//                                  float ___curLineWidth, boolean ___show){
////            if(___words.size()>0){
////                logger.info("Test: "+___curLineWidth);
////            }
//            //logger.info("CLEAR:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        }
//    }


}


