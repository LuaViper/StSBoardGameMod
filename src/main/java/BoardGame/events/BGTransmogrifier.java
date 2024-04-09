package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.neow.BGNeowReward;
import BoardGame.patches.TransformPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGTransmogrifier extends AbstractImageEvent {
    public static final String ID = "BGTransmorgrifier";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGTransmorgrifier");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private static final String IGNORE = DESCRIPTIONS[2];

    private static final String DIALOG_2_DOUBLE = DESCRIPTIONS[3];
    private CUR_SCREEN screen = CUR_SCREEN.INTRO;

    private boolean cursed=false;
    private int cardcount=1;
    private enum CUR_SCREEN {
        INTRO, COMPLETE;
    }

    public BGTransmogrifier() {
        super(NAME, DIALOG_1, "images/events/shrine1.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }


    public void onEnterRoom() {
        CardCrawlGame.music.playTempBGM("SHRINE");
    }

    private static final Logger logger = LogManager.getLogger(BGNeowReward.class.getName());

    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            ArrayList<AbstractCard> transCards=new ArrayList<>();
            ArrayList<String> transCardsString=new ArrayList<>();
            ArrayList<String> before=new ArrayList<>();
            logger.info("# cards: "+this.cardcount);
            for(int i=0;i<AbstractDungeon.gridSelectScreen.selectedCards.size();i+=1) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                AbstractCard transCard = AbstractDungeon.getTransformedCard();
                before.add(c.name);
                //TODO: is transCard.originalName correct here?
                transCardsString.add(transCard.originalName);
                transCards.add(transCard);

            }
            if(transCards.size()>0){
                logMetricTransformCards("Transmorgrifier", "Transformed", before, transCardsString);
            }
            if(transCards.size()==1){
                AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(transCards.get(0), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            }
            if(transCards.size()==2){
                AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(transCards.get(0), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
                AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(transCards.get(1), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        if (this.cursed) {
            this.cursed = !this.cursed;
            AbstractCard card =AbstractDungeon.getCardWithoutRng(AbstractCard.CardRarity.CURSE);
            AbstractBGDungeon.removeCardFromRewardDeck(card);

            AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                    card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

            AbstractBGDungeon.removeCardFromRewardDeck(card);
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {

            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateBodyText(DIALOG_2);
                        AbstractDungeon.gridSelectScreen.open(
                                TransformPatch.getTransformableCards(), 1, OPTIONS[2], false, true, false, false);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1:
                        this.cursed=true;
                        this.cardcount=2;
                        this.screen = CUR_SCREEN.COMPLETE;
                        this.imageEventText.updateBodyText(DIALOG_2_DOUBLE);
                        AbstractDungeon.gridSelectScreen.open(
                                TransformPatch.getTransformableCards(), this.cardcount, OPTIONS[3], false, false, false, false);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();



//                        this.screen = CUR_SCREEN.COMPLETE;
//                        logMetricIgnored("Transmorgrifier");
//                        this.imageEventText.updateBodyText(IGNORE);
//                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
//                        this.imageEventText.clearRemainingOptions();
//                        break;
                }
                break;
            case COMPLETE:
                openMap();
                break;
        }
    }
}


