package BoardGame.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class BGWhetstone
        extends AbstractBGRelic implements ClickableRelic
{
    public static final String ID = "BGWhetstone";
    private static final int CARD_AMT = 1;
    private boolean cardsSelected=false;

    public boolean activated=false;

    public BGWhetstone() {
        super("BGWhetstone", "whetstone.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 7;}
    public boolean usableAsPayment(){return this.counter>-2;}

    public void setCounter(int setCounter) {
        this.counter = setCounter;
        if (this.counter == -2) {
            usedUp();
            this.counter = -2;
        }
    }



    public AbstractRelic makeCopy() {
        return new BGWhetstone();
    }


    private boolean usedThisTurn = false; // You can also have a relic be only usable once per combat. Check out Hubris for more examples, including other StSlib things.

    public String getUpdatedDescription() {
        String desc=this.DESCRIPTIONS[0];
        //if(this.usedUp)desc+=DieControlledRelic.USED_THIS_COMBAT; else desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        return desc;
    }

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || usedThisTurn || AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT ) {
            return;
        }
        if(AbstractDungeon.isScreenUp && AbstractDungeon.screen!= AbstractDungeon.CurrentScreen.MAP){
            return;
        }

        if(true){
            activated=true;
            usedThisTurn = true; // Set relic as "Used this turn"
            setCounter(-2);
            flash(); // Flash
            stopPulse(); // And stop the pulsing animation (which is started in atPreBattle() below)

            {
                CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard card : (AbstractDungeon.player.masterDeck.getPurgeableCards()).group) {
                    if (card.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                        if (card.canUpgrade()) {
                            tmp.addToTop(card);
                            break;
                        }
                    }
                }
                if (tmp.group.size() == 1) {
                    giveCards(tmp.group);
                }
            }

            this.cardsSelected = false;
            {
                CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard card : (AbstractDungeon.player.masterDeck.getPurgeableCards()).group) {
                    if (card.type == AbstractCard.CardType.ATTACK) {
                        if (card.canUpgrade()) {
                            tmp.addToTop(card);
                        }
                    }
                }
                if (tmp.group.isEmpty()) {
                    this.cardsSelected = true;
                    return;
                }
                if (tmp.group.size() <= 1) {
                    giveCards(tmp.group);
                } else if (!AbstractDungeon.isScreenUp) {
                    AbstractDungeon.gridSelectScreen.open(tmp, 1, this.DESCRIPTIONS[1], false, false, false, false);
                } else {
                    AbstractDungeon.dynamicBanner.hide();
                    AbstractDungeon.previousScreen = AbstractDungeon.screen;
                    AbstractDungeon.gridSelectScreen.open(tmp, 1, this.DESCRIPTIONS[1], false, false, false, false);
                }
            }
        }
    }


    @Override
    public void atPreBattle() {
        stopPulse();
    }



    @Override
    public void onVictory() {
        if(!usedUp) {
            beginLongPulse();
        }
    }


    public void update() {
        super.update();
        if(activated) {
            if (!this.cardsSelected &&
                    AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
                giveCards(AbstractDungeon.gridSelectScreen.selectedCards);
                setCounter(-2);
                activated=false;
            }
        }
    }
    public void giveCards(ArrayList<AbstractCard> group) {
        this.cardsSelected = true;
        float displayCount = 0.0F;
        for (Iterator<AbstractCard> i = group.iterator(); i.hasNext(); ) {
            AbstractCard c = i.next();
            c.untip();
            c.unhover();
            c.upgrade();
            //logMetricCardUpgrade("Accursed Blacksmith", "Forge", c);
            //AbstractDungeon.player.bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            this.cardsSelected = false;
        }
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
    }

}


