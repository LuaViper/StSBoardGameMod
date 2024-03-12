package BoardGame.events;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.screen.RelicTradingScreen;
import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class BGTheJoust
        extends AbstractImageEvent implements LockRelicsEvent {
    public static final String ID = "BGTheJoust";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGTheJoust");

    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String HALT_MSG = DESCRIPTIONS[0];

    private static final String EXPL_MSG = DESCRIPTIONS[1];

    private static final String BET_AGAINST = DESCRIPTIONS[2];

    private static final String BET_FOR = DESCRIPTIONS[3];

    private static final String COMBAT_MSG = DESCRIPTIONS[4];

    private static final String NOODLES_WIN = DESCRIPTIONS[5];

    private static final String NOODLES_LOSE = DESCRIPTIONS[6];

    private static final String BET_WON_MSG = DESCRIPTIONS[7];

    private static final String BET_LOSE_MSG = DESCRIPTIONS[8];
    private boolean betFor;
    private boolean playerWins;

    private static final int WIN_OWNER = 250;

    private static final int WIN_MURDERER = 100;

    private static final int BET_AMT = 50;

    private CUR_SCREEN screen = CUR_SCREEN.HALT;

    private float joustTimer = 0.0F;

    private int clangCount = 0;

    private enum CUR_SCREEN {
        HALT, EXPLANATION, CHOOSE_POTION, PRE_JOUST, JOUST, COMPLETE;
    }

    public int knightMessageIndex;
    public int winMessageIndex;
    public int loseMessageIndex;

    public boolean reliclock=false;
    public boolean relicsLocked(){
        return reliclock;
    }

    public BGTheJoust() {
        super(NAME, HALT_MSG, "images/events/joust.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        reliclock=true;

        //TODO: MAYBE use event random number... but probably not
        knightMessageIndex=Math.random()>0.5 ? 2 : 3;
        winMessageIndex=knightMessageIndex==2 ? 6 : 5;
        loseMessageIndex=knightMessageIndex==2 ? 5 : 6;

    }

    public void update() {
        super.update();
        if (this.joustTimer != 0.0F) {
            this.joustTimer -= Gdx.graphics.getDeltaTime();
            if (this.joustTimer < 0.0F) {
                this.clangCount++;
                if (this.clangCount == 1) {
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    this.joustTimer = 1.0F;
                } else if (this.clangCount == 2) {
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    this.joustTimer = 0.25F;
                } else if (this.clangCount == 3) {
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.LONG, false);
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    this.joustTimer = 0.0F;
                }
            }
        }
    }

    protected void buttonEffect(int buttonPressed) {
        String tmp;
        switch (this.screen) {
            case HALT:
                this.imageEventText.updateBodyText(EXPL_MSG);
                this.imageEventText.clearAllDialogs();

                if (AbstractDungeon.player.gold>=2) {
                    this.imageEventText.setDialogOption(OPTIONS[1]);
                } else {
                    this.imageEventText.setDialogOption(OPTIONS[8], true);
                }

                if (!AbstractBGRelic.getAllPayableRelics().isEmpty()) {
                    this.imageEventText.setDialogOption(OPTIONS[2]);
                } else {
                    this.imageEventText.setDialogOption(OPTIONS[9], true);
                }

                if (AbstractDungeon.player.hasAnyPotions()) {
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                } else {
                    this.imageEventText.setDialogOption(OPTIONS[10], true);
                }

                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.screen = CUR_SCREEN.EXPLANATION;
                break;
            case EXPLANATION:
                //TODO: find-in-files placeholder text...
                if (buttonPressed == 3) {
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[5]);
                    this.imageEventText.updateBodyText(DESCRIPTIONS[9]);
                    this.screen = CUR_SCREEN.COMPLETE;
                    break;
                } else if(buttonPressed==0) {
                    AbstractDungeon.player.loseGold(2);
                    this.imageEventText.updateBodyText(DESCRIPTIONS[knightMessageIndex]);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    this.imageEventText.clearRemainingOptions();
                    this.screen = CUR_SCREEN.PRE_JOUST;
                    break;
                } else if(buttonPressed==1) {
                    RelicTradingScreen.RelicTradingAction action = (relic) -> {
                        AbstractDungeon.player.loseRelic(relic.relicId);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[knightMessageIndex]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        this.screen = CUR_SCREEN.PRE_JOUST;
                    };
                    //TODO: localization
                    BaseMod.openCustomScreen(RelicTradingScreen.Enum.RELIC_TRADING, action, "Choose a Relic to wager.", false);
                    break;
                } else if(buttonPressed==2) {
                    this.screen = CUR_SCREEN.CHOOSE_POTION;
                    this.imageEventText.clearAllDialogs();
                    for(AbstractPotion p : AbstractDungeon.player.potions) {
                        if(!(p instanceof PotionSlot)) {
                            this.imageEventText.setDialogOption(OPTIONS[6] + p.name + OPTIONS[7]);
                        }
                    }
                    break;
                }


            case PRE_JOUST:
                this.imageEventText.updateBodyText(COMBAT_MSG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]);
                playerWins=false;
                int random = AbstractDungeon.miscRng.random(1, 6);
                if(random>=4) {
                    playerWins=true;
                }else{
                    //TODO: if player wagers gambling chip, does it still (incorrectly) take effect here?
                    AbstractRelic r=AbstractDungeon.player.getRelic("BGGambling Chip");
                    if(r!=null){
                        r.flash();
                        random = AbstractDungeon.miscRng.random(1, 6);
                        if(random>=4) {
                            playerWins=true;
                        }
                    }
                }

                this.screen = CUR_SCREEN.JOUST;
                this.joustTimer = 0.01F;
                break;
            case JOUST:
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]);
                if (this.playerWins) {
                    tmp = DESCRIPTIONS[winMessageIndex];
                    AbstractDungeon.player.gainGold(6);
                    CardCrawlGame.sound.play("GOLD_GAIN");
                    //TODO: log item wagered
                    logMetricGainGold("The Joust", "Won Bet", 6);
                    tmp = tmp + BET_WON_MSG;
                } else {
                    tmp = DESCRIPTIONS[loseMessageIndex];
                    //TODO: log item wagered
                    logMetricLoseGold("The Joust", "Lost Bet", 2);
                    tmp = tmp + BET_LOSE_MSG;
                }
                this.imageEventText.updateBodyText(tmp);
                this.screen = CUR_SCREEN.COMPLETE;
                reliclock=false;
                break;
            case COMPLETE:
                reliclock=false;
                openMap();
                break;

            case CHOOSE_POTION:
                int i=-1;
                AbstractPotion p = null;
                //find the potion corresponding to the button we just clicked on...
                for(AbstractPotion q : AbstractDungeon.player.potions) {
                    if(!(q instanceof PotionSlot)) {
                        i+=1;
                        if(i==buttonPressed)
                            p = q;
                    }
                }
                if(p!=null) {
                    AbstractDungeon.topPanel.destroyPotion(p.slot);
                }
                //TODO: if p==null, quit event instead of proceeding to joust
                this.imageEventText.updateBodyText(DESCRIPTIONS[knightMessageIndex]);
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                this.imageEventText.clearRemainingOptions();
                this.screen = CUR_SCREEN.PRE_JOUST;
                break;
        }
    }
}
