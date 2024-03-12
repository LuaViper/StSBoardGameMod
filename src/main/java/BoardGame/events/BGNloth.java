package BoardGame.events;

import BoardGame.BoardGame;
import BoardGame.characters.BGDefect;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGSilent;
import BoardGame.characters.BGWatcher;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGPrismaticShard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.NlothsGift;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BGNloth
        extends AbstractImageEvent {
    public static final String ID = "BGNloth";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("BoardGame:BGNloth");
    public static final String NAME = eventStrings.NAME;

    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;

    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String DIALOG_1 = DESCRIPTIONS[0];

    private static final String DIALOG_2 = DESCRIPTIONS[1];

    private static final String DIALOG_3 = DESCRIPTIONS[2];

    private int screenNum = 0;

    private AbstractRelic randomRelic;


    private AbstractRelic gift;

    public BGNloth() {
        super(NAME, DIALOG_1, "images/events/nloth.jpg");
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.addAll(AbstractDungeon.player.relics);
        Collections.shuffle(relics, new Random(AbstractDungeon.miscRng.randomLong()));
        for(AbstractRelic r : relics) {
            if(!(r instanceof AbstractBGRelic)){
                randomRelic=r;
                break;
            }
            AbstractBGRelic bgr=(AbstractBGRelic)r;
            if(bgr.usableAsPayment()){
                randomRelic=bgr;
                break;
            }
        }
        if(randomRelic!=null) {
            BoardGame.logger.info("N'loth: SECRETLY picked " + randomRelic.name + ". Hey! You are not allowed to read this message! Stop this at once!");
        }

        this.gift = (AbstractRelic)new NlothsGift();
        if(randomRelic!=null)this.imageEventText.setDialogOption(OPTIONS[0]); else this.imageEventText.setDialogOption(OPTIONS[3], true);
        if(AbstractDungeon.player.hasAnyPotions()) this.imageEventText.setDialogOption(OPTIONS[1]); else this.imageEventText.setDialogOption(OPTIONS[4], true);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON)
            CardCrawlGame.sound.play("EVENT_SERPENT");
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);
                            //AbstractEvent.logMetricRelicSwap("N'loth", "Traded Relic", this.gift, this.randomRelic);
                            AbstractDungeon.player.loseRelic(randomRelic.relicId);
                        AbstractBGDungeon.forceRareRewards=true;
                        AbstractDungeon.cardRewardScreen.open(
                                AbstractDungeon.getRewardCards(), null,
                                (CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
                        AbstractBGDungeon.forceRareRewards=false;
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        this.screenNum=999;
                        this.imageEventText.clearAllDialogs();
                        for(AbstractPotion p : AbstractDungeon.player.potions) {
                            if(!(p instanceof PotionSlot)) {
                                this.imageEventText.setDialogOption(OPTIONS[5] + p.name + OPTIONS[6]);
                            }
                        }
                        return;
                    case 2:
                        AbstractEvent.logMetricIgnored("N'loth");
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                }
                this.imageEventText.updateBodyText(DIALOG_3);
                this.screenNum = 1;
                this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                this.imageEventText.clearRemainingOptions();
                return;
            case 1:
                openMap();
                return;
            case 999:
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
                    //logMetricObtainRelic("N'loth", "Gave Potion", this.gift);
                    AbstractCard card=AbstractDungeon.getCard(AbstractCard.CardRarity.RARE, NeowEvent.rng);
                    AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                            card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    //card.makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    AbstractBGDungeon.removeCardFromRewardDeck(card);
                }
                this.imageEventText.updateBodyText(DIALOG_2);
                this.screenNum = 1;
                this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                this.imageEventText.clearRemainingOptions();
                return;
        }
        openMap();
    }
}

