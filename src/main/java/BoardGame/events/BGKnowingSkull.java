package BoardGame.events;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.getRewardCards;


public class BGKnowingSkull
        extends AbstractImageEvent
{
    private static final Logger logger = LogManager.getLogger(BGKnowingSkull.class.getName());
    public static final String ID = "BGKnowing Skull";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGKnowing Skull");
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String INTRO_MSG = DESCRIPTIONS[0];
    private static final String INTRO_2_MSG = DESCRIPTIONS[1];
    private static final String ASK_AGAIN_MSG = DESCRIPTIONS[2];
    private static final String POTION_MSG = DESCRIPTIONS[4];
    private static final String CARD_MSG = DESCRIPTIONS[5];
    private static final String GOLD_MSG = DESCRIPTIONS[6];
    private static final String LEAVE_MSG = DESCRIPTIONS[7];

    private int potionCost;
    private int cardCost;
    private CurScreen screen = CurScreen.INTRO_1; private int goldCost; private int leaveCost; private static final int GOLD_REWARD = 90;
    private String optionsChosen = "";

    private int damageTaken;
    private int goldEarned;
    private List<String> potions;
    private List<String> cards;
    private ArrayList<Reward> options = new ArrayList<>();

    private enum CurScreen {
        INTRO_1, ASK, COMPLETE;
    }

    private enum Reward {
        POTION, LEAVE, GOLD, CARD;
    }

    private boolean pickedPotion=false;
    private boolean pickedGold=false;
    private boolean pickedCard=false;
    //private boolean waitingForCardChoice=false;
    private int rewardsPicked=0;




    public BGKnowingSkull() {
        super(NAME, INTRO_MSG, "images/events/knowingSkull.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.options.add(Reward.CARD);
        this.options.add(Reward.GOLD);
        this.options.add(Reward.POTION);
        this.options.add(Reward.LEAVE);

        this.leaveCost = 0;
        this.cardCost = 2;
        this.potionCost = 1;
        this.goldCost = 1;

        this.damageTaken = 0;
        this.goldEarned = 0;
        this.cards = new ArrayList<>();
        this.potions = new ArrayList<>();
    }


    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SKULL");
        }
    }


    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO_1:
                this.imageEventText.updateBodyText(INTRO_2_MSG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4] + this.potionCost + OPTIONS[1]);
                this.imageEventText.setDialogOption(OPTIONS[5] + 2 + OPTIONS[6] + this.goldCost + OPTIONS[1]);
                this.imageEventText.setDialogOption(OPTIONS[3] + this.cardCost + OPTIONS[1]);
                //this.imageEventText.setDialogOption(OPTIONS[7]);
                this.screen = CurScreen.ASK;
                break;
            case ASK:
                CardCrawlGame.sound.play("DEBUFF_2");
                int cost=0;
                if(rewardsPicked>=2){
                    setLeave();
                }else {
                    switch (buttonPressed) {
                        case 0:
                            if (!pickedPotion) {
                                pickedPotion = true;
                                rewardsPicked += 1;
                                obtainReward(0);
                                cost = potionCost;
                            } else {
                                setLeave();
                            }
                            break;
                        case 1:
                            if (!pickedGold) {
                                pickedGold = true;
                                rewardsPicked += 1;
                                obtainReward(1);
                                cost = goldCost;
                            } else {
                                setLeave();
                            }
                            break;
                        case 2:
                            if (!pickedCard) {
                                pickedCard = true;
                                rewardsPicked += 1;
                                obtainReward(2);
                                cost = cardCost;
                            } else {
                                setLeave();
                            }
                            break;
                    }

                }
                //setLeave();
                break;


            case COMPLETE:
                logMetric("Knowing Skull", this.optionsChosen, this.cards, null, null, null, null, this.potions, null, this.damageTaken, 0, 0, 0, this.goldEarned, 0);

                openMap();
                break;
        }
    }

    private void obtainReward(int slot) {
        AbstractPotion p;
        AbstractCard c;
        String nextmsg = ASK_AGAIN_MSG;
        if (rewardsPicked >= 2) nextmsg = DESCRIPTIONS[8];
        switch (slot) {
            case 0:
                AbstractDungeon.player.damage(new DamageInfo(null, this.potionCost, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += this.potionCost;
                this.potionCost++;
                this.optionsChosen += "POTION ";
                this.imageEventText.updateBodyText(POTION_MSG + nextmsg);
                if (AbstractDungeon.player.hasRelic("Sozu")) {
                    AbstractDungeon.player.getRelic("Sozu").flash();
                    break;
                }
                if (AbstractDungeon.player.hasRelic("BGSozu")) {
                    AbstractDungeon.player.getRelic("BGSozu").flash();
                    break;
                }
                //TODO: in BG, player can still draw potion card while inventory is full, then choose to discard a different potion
                p = PotionHelper.getRandomPotion();
                this.potions.add(p.ID);
                AbstractDungeon.player.obtainPotion(p);
                break;

            case 1:
                AbstractDungeon.player.damage(new DamageInfo(null, this.goldCost, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += this.goldCost;
                this.goldCost++;
                this.optionsChosen += "GOLD ";
                this.imageEventText.updateBodyText(GOLD_MSG + nextmsg);
                AbstractDungeon.effectList.add(new RainingGoldEffect(90));
                AbstractDungeon.player.gainGold(2);
                this.goldEarned += 2;
                break;
            case 2:
                AbstractDungeon.player.damage(new DamageInfo(null, this.cardCost, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += this.cardCost;
                this.cardCost++;
                this.optionsChosen += "CARD ";
                this.imageEventText.updateBodyText(CARD_MSG + nextmsg);
                //this.waitingForCardChoice = true;
                AbstractDungeon.cardRewardScreen.open(getRewardCards(), null, "Choose a Card");  //TODO: localization
//            c = AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON);
//            this.cards.add(c.cardID);
//            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
//                AbstractBGDungeon.removeCardFromRewardDeck(c);
                break;

            default:
                logger.info("This should never happen.");
                break;
        }
        this.imageEventText.clearAllDialogs();
        if (rewardsPicked < 2) {
            this.imageEventText.setDialogOption(!pickedPotion ? OPTIONS[4] + this.potionCost + OPTIONS[1] : OPTIONS[7]);
            this.imageEventText.setDialogOption(!pickedGold ? OPTIONS[5] + 2 + OPTIONS[6] + this.goldCost + OPTIONS[1] : OPTIONS[7]);
            this.imageEventText.setDialogOption(!pickedCard ? OPTIONS[3] + this.cardCost + OPTIONS[1] : OPTIONS[7]);
        } else {
            this.imageEventText.clearRemainingOptions();
            this.imageEventText.setDialogOption(OPTIONS[7]);
        }

    }

    private void setLeave() {
        this.imageEventText.updateBodyText(LEAVE_MSG);
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[8]);
        this.screen = CurScreen.COMPLETE;
    }

    public void update() {
        super.update();

    }
}


