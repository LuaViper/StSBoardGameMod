package BoardGame.relics;

import BoardGame.dungeons.AbstractBGDungeon;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGCallingBell
        extends AbstractBGRelic
{
    public static final String ID = "BGCalling Bell";
    private boolean cardsReceived = true;

    public BGCallingBell() {
        super("BGCalling Bell", "bell.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.SOLID);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onEquip() {
        this.cardsReceived = false;
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        AbstractCard randomcurse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
        AbstractBGDungeon.removeCardFromRewardDeck(randomcurse);
        UnlockTracker.markCardAsSeen(((AbstractCard)randomcurse).cardID);
        group.addToBottom(randomcurse.makeCopy());

        AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, this.DESCRIPTIONS[1]);
        CardCrawlGame.sound.playA("BELL", MathUtils.random(-0.2F, -0.3F));
    }


    public void update() {
        super.update();
        if (!this.cardsReceived && !AbstractDungeon.isScreenUp) {
            AbstractDungeon.combatRewardScreen.open();
            AbstractDungeon.combatRewardScreen.rewards.clear();

            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(
                    AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON)));
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(
                    AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON)));
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(
                    AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON)));

            AbstractDungeon.combatRewardScreen.positionRewards();
            AbstractDungeon.overlayMenu.proceedButton.setLabel(this.DESCRIPTIONS[2]);

            this.cardsReceived = true;
            (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.25F;
        }

        if (this.hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.playA("BELL", MathUtils.random(-0.2F, -0.3F));
            flash();
        }
    }


    public AbstractRelic makeCopy() {
        return new BGCallingBell();
    }
}


