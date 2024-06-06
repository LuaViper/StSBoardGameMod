package BoardGame.events;

import BoardGame.cards.BGCurse.BGAscendersBane;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class BGFalling
        extends AbstractImageEvent {
    public static final String ID = "BGFalling";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGFalling");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0]; private boolean attack; private boolean skill;
    private static final String DIALOG_2 = DESCRIPTIONS[1];
    private boolean power;
    private CurScreen screen = CurScreen.INTRO;
    private AbstractCard attackCard;
    private AbstractCard skillCard;
    private AbstractCard powerCard;

    private enum CurScreen { INTRO, CHOICE, RESULT; }


    public BGFalling() {
        super(NAME, DIALOG_1, "images/events/falling.jpg");
        setCards();
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FALLING");
        }
    }

    private void setCards() {
        CardGroup cards=new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for(AbstractCard card : AbstractDungeon.player.masterDeck.group){
            cards.addToTop(card);
        }
        cards.shuffle(AbstractDungeon.miscRng);

        this.skill = (cards.size()>=3);
        this.power = (cards.size()>=2);
        this.attack = (cards.size()>=1);

        if (this.skill) {
            this.skillCard = cards.getNCardFromTop(2);
        }
        if (this.power) {
            this.powerCard = cards.getNCardFromTop(1);
        }
        if (this.attack) {
            this.attackCard = cards.getNCardFromTop(0);
        }

    }

    private void makeDialogOption(AbstractCard card){
        //TODO: more verbs for duplicate card types!
        String verb;
        if(card.type==AbstractCard.CardType.SKILL){
            verb=OPTIONS[1];
        }else if(card.type== AbstractCard.CardType.POWER){
            verb=OPTIONS[3];
        }else if(card.type== AbstractCard.CardType.ATTACK) {
            verb = OPTIONS[5];
        }else if(card instanceof BGAscendersBane){
            verb = OPTIONS[11];
        }else{
            verb=OPTIONS[9];
        }

        if(card instanceof BGAscendersBane) {
            this.imageEventText.setDialogOption(verb + OPTIONS[10] +
                    FontHelper.colorString(card.name, "r"),true);

        }else{
            this.imageEventText.setDialogOption(verb + OPTIONS[10] +
                    FontHelper.colorString(card.name, "r"), card
                    .makeStatEquivalentCopy());
        }
    }

    private void setBodyText(AbstractCard card){
        if(card.type==AbstractCard.CardType.SKILL) {
            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
        }else if(card.type== AbstractCard.CardType.POWER){
            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
        }else if(card.type== AbstractCard.CardType.ATTACK){
            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
        }else{
            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                this.screen = CurScreen.CHOICE;
                this.imageEventText.updateBodyText(DIALOG_2);

                this.imageEventText.clearAllDialogs();

                if (!this.skill && !this.power && !this.attack) {
                    this.imageEventText.setDialogOption(OPTIONS[8]);
                } else {
                    if (this.skill) {
                        makeDialogOption(this.skillCard);
                    } else {
                        this.imageEventText.setDialogOption(OPTIONS[2], true);
                    }
                    if (this.power) {
                        makeDialogOption(this.powerCard);
                    } else {
                        this.imageEventText.setDialogOption(OPTIONS[4], true);
                    }
                    if (this.attack) {
                        makeDialogOption(this.attackCard);
                    } else {
                        this.imageEventText.setDialogOption(OPTIONS[6], true);
                    }
                }
                if(!this.skill && !this.power && this.attackCard instanceof BGAscendersBane) {
                    this.imageEventText.setDialogOption(OPTIONS[8]);
                }


                return;
            case CHOICE:
                this.screen = CurScreen.RESULT;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[7]);
                switch (buttonPressed) {
                    case 0:
                        if (!this.skill && !this.power && !this.attack) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                            logMetricIgnored("Falling"); break;
                        }
                        setBodyText(this.skillCard);
                        AbstractDungeon.effectList.add(new PurgeCardEffect(this.skillCard));
                        AbstractDungeon.player.masterDeck.removeCard(this.skillCard);
                        logMetricCardRemoval("Falling", "Removed Card", this.skillCard);
                        break;

                    case 1:
                        setBodyText(this.powerCard);
                        AbstractDungeon.effectList.add(new PurgeCardEffect(this.powerCard));
                        AbstractDungeon.player.masterDeck.removeCard(this.powerCard);
                        logMetricCardRemoval("Falling", "Removed Card", this.powerCard);
                        break;
                    case 2:
                        setBodyText(this.attackCard);
                        AbstractDungeon.effectList.add(new PurgeCardEffect(this.attackCard));
                        logMetricCardRemoval("Falling", "Removed Card", this.attackCard);
                        AbstractDungeon.player.masterDeck.removeCard(this.attackCard);
                        break;
                    case 3:
                        if(!this.skill && !this.power && this.attackCard instanceof BGAscendersBane) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                            logMetricIgnored("Falling"); break;
                        }
                }

                return;
        }
        openMap();
    }
}


