package CoopBoardGame.events;

import CoopBoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BGCursedTome extends AbstractImageEvent {

    public static final String ID = "BGCursed Tome";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(
        "CoopBoardGame:BGCursed Tome"
    );
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String INTRO_MSG = DESCRIPTIONS[0];
    private static final String IGNORE_MSG = DESCRIPTIONS[6];

    private static final String OPT_LEAVE = OPTIONS[7];

    private CurScreen screen = CurScreen.INTRO;

    private enum CurScreen {
        INTRO,
        PAGE_1,
        PAGE_2,
        PAGE_3,
        LAST_PAGE,
        END,
    }

    public BGCursedTome() {
        super(NAME, INTRO_MSG, "images/events/cursedTome.jpg");
        this.noCardsInRewards = true;

        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    public void update() {
        super.update();
        if (
            true &&
            !AbstractDungeon.isScreenUp &&
            !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()
        ) {
            //AbstractCard c = ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeCopy();
            AbstractCard c = ((AbstractCard) AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            //logMetricObtainCard("The Library", "Read", c);
            AbstractDungeon.effectList.add(
                new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F)
            );

            AbstractBGDungeon.removeCardFromRewardDeck(
                AbstractDungeon.gridSelectScreen.selectedCards.get(0)
            );

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
                    AbstractDungeon.effectList.add(
                        new ShowCardAndObtainEffect(
                            (AbstractCard) curse,
                            (Settings.WIDTH / 2),
                            (Settings.HEIGHT / 2)
                        )
                    );
                    AbstractBGDungeon.removeCardFromRewardDeck(curse);
                    AbstractBGDungeon.forceRareRewards = true;
                    AbstractDungeon.cardRewardScreen.open(
                        AbstractDungeon.getRewardCards(),
                        null,
                        (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]
                    );
                    AbstractBGDungeon.forceRareRewards = false;
                    this.screen = CurScreen.END;
                    break;
                } else if (buttonPressed == 1) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                    CardCrawlGame.sound.play("ATTACK_POISON");
                    AbstractDungeon.player.damage(
                        new DamageInfo(null, 2, DamageInfo.DamageType.HP_LOSS)
                    );

                    AbstractCard card = AbstractDungeon.getCard(
                        AbstractCard.CardRarity.RARE,
                        NeowEvent.rng
                    );
                    AbstractDungeon.topLevelEffects.add(
                        new ShowCardAndObtainEffect(
                            card,
                            Settings.WIDTH / 2.0F,
                            Settings.HEIGHT / 2.0F
                        )
                    );
                    //card.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card);

                    this.screen = CurScreen.END;
                    break;
                } else if (buttonPressed == 2) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                    CardCrawlGame.sound.play("ATTACK_POISON");
                    AbstractDungeon.player.damage(
                        new DamageInfo(null, 1, DamageInfo.DamageType.HP_LOSS)
                    );
                    AbstractDungeon.cardRewardScreen.open(
                        AbstractDungeon.getRewardCards(),
                        null,
                        (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]
                    );
                    this.screen = CurScreen.END;
                    break;
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
            default:
                break;
        }
    }
}
