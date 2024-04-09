package BoardGame.events;

import BoardGame.potions.BGGamblersBrew;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.events.shrines.Lab;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGLab
        extends Lab {   //game is hardcoded to check for Lab when completing event
        //extends AbstractImageEvent {
    public static final String ID = "BGLab";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("Lab");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private enum CUR_SCREEN {
        INTRO, COMPLETE, POTION;
    }

    public BGLab() {
        super();
//        super(NAME, DIALOG_1, "images/events/lab.jpg");
//        this.noCardsInRewards = true;
//        this.imageEventText.setDialogOption(OPTIONS[0]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_LAB");
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {

            case INTRO:

                (AbstractDungeon.getCurrRoom()).rewards.clear();
                (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                int random = AbstractDungeon.miscRng.random(1, 6);
                boolean playerWins=false;
                if(random>=4) {
                    playerWins = true;
                }else if(random==3 && AbstractDungeon.player.hasRelic("BGTheAbacus")){
                    AbstractDungeon.player.getRelic("BGTheAbacus").flash();
                    playerWins=true;
                }else if(random==1 && AbstractDungeon.player.hasRelic("BGToolbox")){
                    AbstractDungeon.player.getRelic("BGToolbox").flash();
                    playerWins=true;
                }
                if(!playerWins){
                    //TODO: if player wagers a die relic, does it still (incorrectly) take effect here?
                    AbstractRelic r=AbstractDungeon.player.getRelic("BGGambling Chip");
                    if(r!=null){
                        r.flash();
                        random = AbstractDungeon.miscRng.random(1, 6);
                        if(random>=4) {
                            playerWins = true;
                        }else if(random==3 && AbstractDungeon.player.hasRelic("BGTheAbacus")){
                            AbstractDungeon.player.getRelic("BGTheAbacus").flash();
                            playerWins=true;
                        }else if(random==1 && AbstractDungeon.player.hasRelic("BGToolbox")){
                            AbstractDungeon.player.getRelic("BGToolbox").flash();
                            playerWins=true;
                        }
                    }
                }
                if(playerWins) {
                    (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                }else{
                    if(BGGamblersBrew.doesPlayerHaveGamblersBrew()>-1) {
                        this.screen = CUR_SCREEN.POTION;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[1]);
                        this.imageEventText.setDialogOption(OPTIONS[2]);
                        break;
                    }
                }
                GenericEventDialog.hide();
                this.screen = CUR_SCREEN.COMPLETE;
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                AbstractDungeon.combatRewardScreen.open();
                logMetric("Lab", "Got Potions");
                break;
            case COMPLETE:
                openMap();
                break;
            case POTION:
                if(buttonPressed==1){
                    int slot=BGGamblersBrew.doesPlayerHaveGamblersBrew();
                    if(slot>-1) {
                        AbstractDungeon.topPanel.destroyPotion(slot);
                        (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                    }
                }
                this.screen = CUR_SCREEN.COMPLETE;
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                AbstractDungeon.combatRewardScreen.open();
                logMetric("Lab", "Got Potions");
                break;
        }
    }
}


