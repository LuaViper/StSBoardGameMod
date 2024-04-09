package BoardGame.relics;

//TODO: sometimes card fails to be added to deck during Quick Start process :(

import BoardGame.dungeons.AbstractBGDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class BGEnchiridion extends AbstractBGRelic  {
    public static final String ID = "BGEnchiridion";

    private boolean cardsSelected = true;
    private boolean gaveCard=false;

    public BGEnchiridion() {
        super("BGEnchiridion", "enchiridion.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onEquip() {
        this.cardsSelected = false;

        CardGroup group;

        group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);


        for (int i = 0; i < 5; i++) {
            //AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE).makeCopy();
            AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE);
            if (!group.contains(card)) {
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onPreviewObtainCard(card);
                }
                group.addToBottom(card);
            }
        }

        for (AbstractCard c : group.group) {
            UnlockTracker.markCardAsSeen(c.cardID);
        }

        if (!AbstractDungeon.isScreenUp) {
            AbstractDungeon.gridSelectScreen.open(group, 1, this.DESCRIPTIONS[1], false);
        }
        else {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
            AbstractDungeon.gridSelectScreen.open(group, 1, this.DESCRIPTIONS[1], false);
        }
    }

    public void update() {
        super.update();
        if (!this.cardsSelected && AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
            if(!this.gaveCard) {
                giveCards(AbstractDungeon.gridSelectScreen.selectedCards);
            }
        }
    }

    public void giveCards(ArrayList<AbstractCard> group) {
        this.cardsSelected = true;
        float displayCount = 0.0F;

        //if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            //AbstractCard c = ((AbstractCard) AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeCopy();
            AbstractCard c = ((AbstractCard) AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            //logMetricObtainCard("The Library", "Read", c);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

            AbstractBGDungeon.removeCardFromRewardDeck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));

            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            gaveCard=true;
        }
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
    }



    public AbstractRelic makeCopy() {
        return new BGEnchiridion();
    }
}


