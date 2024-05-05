package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;






public class BGCursedTome
        extends AbstractImageEvent
{
    public static final String ID = "BGCursed Tome";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGCursed Tome");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;


    private static final String INTRO_MSG = DESCRIPTIONS[0];
    private static final String READ_1 = DESCRIPTIONS[1];
    private static final String READ_2 = DESCRIPTIONS[2];
    private static final String READ_3 = DESCRIPTIONS[3];
    private static final String READ_4 = DESCRIPTIONS[4];
    private static final String OBTAIN_MSG = DESCRIPTIONS[5];
    private static final String IGNORE_MSG = DESCRIPTIONS[6];
    private static final String STOP_MSG = DESCRIPTIONS[7];


    private static final String OPT_READ = OPTIONS[0];
    private static final String OPT_CONTINUE_1 = OPTIONS[1];
    private static final String OPT_CONTINUE_2 = OPTIONS[2];
    private static final String OPT_CONTINUE_3 = OPTIONS[3];
    private static final String OPT_STOP = OPTIONS[4];
    private static final String OPT_LEAVE = OPTIONS[7];

    private static final int DMG_BOOK_OPEN = 1;

    private static final int DMG_SECOND_PAGE = 2;

    private static final int DMG_THIRD_PAGE = 3;
    private static final int DMG_STOP_READING = 3;
    private static final int DMG_OBTAIN_BOOK = 10;
    private static final int A_2_DMG_OBTAIN_BOOK = 15;
    private int finalDmg;
    private int damageTaken;
    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO, PAGE_1, PAGE_2, PAGE_3, LAST_PAGE, END;
    }



    public BGCursedTome() {
        super(NAME, INTRO_MSG, "images/events/cursedTome.jpg");
        this.noCardsInRewards = true;
        this.damageTaken = 0;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.finalDmg = 15;
        } else {
            this.finalDmg = 10;
        }

        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }


    public void update() {
        super.update();
        if (true &&
                !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            //AbstractCard c = ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeCopy();
            AbstractCard c = ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            //logMetricObtainCard("The Library", "Read", c);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

            AbstractBGDungeon.removeCardFromRewardDeck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                this.imageEventText.clearAllDialogs();
                CardCrawlGame.sound.play("EVENT_TOME");
                this.imageEventText.updateDialogOption(0, OPT_LEAVE);
                this.imageEventText.clearRemainingOptions();
                if (buttonPressed == 0) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                    AbstractCard curse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) curse, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
                    AbstractBGDungeon.removeCardFromRewardDeck(curse);
                    AbstractBGDungeon.forceRareRewards=true;
                    AbstractDungeon.cardRewardScreen.open(
                            AbstractDungeon.getRewardCards(), null,
                            (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
                    AbstractBGDungeon.forceRareRewards=false;
                    this.screen = CurScreen.END; break;
                }else if(buttonPressed==1){
                    this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                    CardCrawlGame.sound.play("ATTACK_POISON");
                    AbstractDungeon.player.damage(new DamageInfo(null,2, DamageInfo.DamageType.HP_LOSS));

                    AbstractCard card=AbstractDungeon.getCard(AbstractCard.CardRarity.RARE, NeowEvent.rng);
                    AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                            card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    //card.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card);

                    this.screen = CurScreen.END; break;
                }else if(buttonPressed==2){
                    this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                    CardCrawlGame.sound.play("ATTACK_POISON");
                    AbstractDungeon.player.damage(new DamageInfo(null,1, DamageInfo.DamageType.HP_LOSS));
                    AbstractDungeon.cardRewardScreen.open(
                            AbstractDungeon.getRewardCards(), null,
                            (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
                    this.screen = CurScreen.END; break;

                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPT_LEAVE);
                this.imageEventText.updateBodyText(IGNORE_MSG);
                this.screen = CurScreen.END;
                //logMetricIgnored("Cursed Tome");  //TODO: log this
                break;


            case END:
                this.imageEventText.updateDialogOption(0, OPT_LEAVE);
                this.imageEventText.clearRemainingOptions();
                openMap();
                break;
        }
    }




    private void randomBook() {
        ArrayList<AbstractRelic> possibleBooks = new ArrayList<>();

        if (!AbstractDungeon.player.hasRelic("Necronomicon")) {
            possibleBooks.add(RelicLibrary.getRelic("Necronomicon").makeCopy());
        }

        if (!AbstractDungeon.player.hasRelic("Enchiridion")) {
            possibleBooks.add(RelicLibrary.getRelic("Enchiridion").makeCopy());
        }

        if (!AbstractDungeon.player.hasRelic("Nilry's Codex")) {
            possibleBooks.add(RelicLibrary.getRelic("Nilry's Codex").makeCopy());
        }

        if (possibleBooks.size() == 0) {
            possibleBooks.add(RelicLibrary.getRelic("Circlet").makeCopy());
        }

        AbstractRelic r = possibleBooks.get(AbstractDungeon.miscRng.random(possibleBooks.size() - 1));
        logMetricTakeDamage("Cursed Tome", "Obtained Book", this.damageTaken);

        (AbstractDungeon.getCurrRoom()).rewards.clear();
        AbstractDungeon.getCurrRoom().addRelicToRewards(r);
        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.END;
    }
}


