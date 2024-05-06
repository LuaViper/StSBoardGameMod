package BoardGame.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class BGAstrolabe
        extends AbstractBGRelic
{
    public static final String ID = "BGAstrolabe";
    private boolean cardsSelected = true;

    public BGAstrolabe() {
        super("BGAstrolabe", "astrolabe.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
    }



    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onEquip() {
        this.cardsSelected = false;
        CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : (AbstractDungeon.player.masterDeck.getUpgradableCards()).group) {
            tmp.addToTop(card);
        }

        if (tmp.group.isEmpty()) {
            this.cardsSelected = true; return;
        }
        if (tmp.group.size() <= 3) {
            giveCards(tmp.group);
        }
        else if (!AbstractDungeon.isScreenUp) {
            AbstractDungeon.gridSelectScreen.open(tmp, 3, this.DESCRIPTIONS[1] + this.name + LocalizedStrings.PERIOD, false, false, false, false);



        }
        else {



            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
            AbstractDungeon.gridSelectScreen.open(tmp, 3, this.DESCRIPTIONS[1] + this.name + LocalizedStrings.PERIOD, false, false, false, false);
        }
    }










    public void update() {
        super.update();
        if (!this.cardsSelected &&
                AbstractDungeon.gridSelectScreen.selectedCards.size() == 3) {
            giveCards(AbstractDungeon.gridSelectScreen.selectedCards);
        }
    }


    public void giveCards(ArrayList<AbstractCard> group) {
        this.cardsSelected = true;
        float displayCount = 0.0F;
        for (Iterator<AbstractCard> i = group.iterator(); i.hasNext(); ) {
            AbstractCard card = i.next();
            card.untip();
            card.unhover();
            //AbstractDungeon.player.masterDeck.removeCard(card);
            //AbstractDungeon.transformCard(card, true, AbstractDungeon.miscRng);
            card.upgrade();

            if (true) {

//                AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(
//
//                        card, Settings.WIDTH / 3.0F + displayCount, Settings.HEIGHT / 2.0F, false));
                AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),Settings.WIDTH / 3.0F + displayCount,Settings.HEIGHT / 2.0F));


                displayCount += Settings.WIDTH / 6.0F;
            }
        }
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
    }


    public AbstractRelic makeCopy() {
        return new BGAstrolabe();
    }
}


