package BoardGame.events;

import BoardGame.characters.BGDefect;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGSilent;
import BoardGame.characters.BGWatcher;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.BGPrismaticShard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGANoteForYourself
        extends AbstractImageEvent {
    public static final String ID = "BGANoteForYourself";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGANoteForYourself");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private AbstractCard obtainCard = null;

    public AbstractCard saveCard = null;

    private boolean cardSelect = false;

    private static final String DIALOG_1 = DESCRIPTIONS[0];

    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private enum CUR_SCREEN {
        INTRO, CHOOSE, COMPLETE;
    }

    int whoAmI=1;       //1-indexed starting from BGIronclad

    public BGANoteForYourself() {
        super(NAME, DIALOG_1, "images/events/selfNote.jpg");
        this.noCardsInRewards = true;
        this.imageEventText.setDialogOption(OPTIONS[0]);
        //initializeObtainCard();
        if(AbstractDungeon.player instanceof BGIronclad) whoAmI=1;
        else if(AbstractDungeon.player instanceof BGSilent) whoAmI=2;
        else if(AbstractDungeon.player instanceof BGDefect) whoAmI=3;
        else if(AbstractDungeon.player instanceof BGWatcher) whoAmI=4;
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.screen = CUR_SCREEN.CHOOSE;
                int i=0;
                i+=1;if(i==whoAmI)i+=1;
                this.imageEventText.updateDialogOption(0, OPTIONS[i]);
                i+=1;if(i==whoAmI)i+=1;
                this.imageEventText.setDialogOption(OPTIONS[i]);
                i+=1;if(i==whoAmI)i+=1;
                this.imageEventText.setDialogOption(OPTIONS[i]);

                break;
            case CHOOSE:
                this.screen = CUR_SCREEN.COMPLETE;
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                int whichDeck=buttonPressed+1;          //1-indexed
                if(whichDeck>=whoAmI)whichDeck+=1;
                int whichDeckIndex=whichDeck-1;         //0-indexed
                if(whichDeckIndex==2){
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), new BGPrismaticShard());
                }
                reward(whichDeckIndex);

                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                this.imageEventText.clearRemainingOptions();
                this.screen = CUR_SCREEN.COMPLETE;
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                break;
            case COMPLETE:
                openMap();
                break;
        }
    }

    private void reward(int whichDeckIndex) {
        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractBGDungeon.forceSpecificColor=whichDeckIndex;
        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem());
        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        AbstractBGDungeon.forceSpecificColor=-999;
        this.screen = BGANoteForYourself.CUR_SCREEN.COMPLETE;
    }


    public void update() {
        super.update();
        if (this.cardSelect && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard storeCard = AbstractDungeon.gridSelectScreen.selectedCards.remove(0);
            logMetricObtainCardAndLoseCard("NoteForYourself", "Took Card", this.obtainCard, storeCard);
            AbstractDungeon.player.masterDeck.removeCard(storeCard);
            this.saveCard = storeCard;
            this.cardSelect = false;
        }
    }

//    private void initializeObtainCard() {
//        this.obtainCard = CardLibrary.getCard(CardCrawlGame.playerPref.getString("NOTE_CARD", "Iron Wave"));
//        if (this.obtainCard == null)
//            this.obtainCard = (AbstractCard)new IronWave();
//        this.obtainCard = this.obtainCard.makeCopy();
//        for (int i = 0; i < CardCrawlGame.playerPref.getInteger("NOTE_UPGRADE", 0); i++)
//            this.obtainCard.upgrade();
//    }
}
