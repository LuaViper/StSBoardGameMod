package BoardGame.relics;

import BoardGame.rewards.TinyHouseUpgrade1Card;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;

//TODO: github.com/daviscook477/BaseMod/wiki/Custom-Rewards
//TODO: BGTinyHouse needs to upgrade a NON-random card
//TODO: BGTinyHouse breaks Neow's quickstart reward screen (rewards are autopicked) (maybe not an issue since potion stays on the menu if slots are full?)

public class BGTinyHouse extends AbstractBGRelic  {
    public static final String ID = "BGTiny House";

    private static final int GOLD_AMT = 3;

    private static final int HP_AMT = 5;

    public BGTinyHouse() {
        super("BGTiny House", "tinyHouse.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + GOLD_AMT + this.DESCRIPTIONS[1] + this.DESCRIPTIONS[2];
    }

    public void onEquip() {
        boolean prevNCIR=false;
        if(AbstractDungeon.getCurrRoom().event!=null)AbstractDungeon.getCurrRoom().event.noCardsInRewards=false;


        //card is already added to rewards by default in CombatRewardScreen.setupItemReward

        //we have to set labeloverride before opening the screen because addPotionToRewards uses it to check for Tiny House

        //ReflectionHacks.setPrivate(AbstractDungeon.combatRewardScreen, CombatRewardScreen.class,"labelOverride",this.DESCRIPTIONS[3]);

        AbstractDungeon.combatRewardScreen.open(this.DESCRIPTIONS[3]);
        AbstractDungeon.getCurrRoom().rewards.clear();
        AbstractDungeon.combatRewardScreen.rewards.clear();

        AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem());
        AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
        AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(GOLD_AMT));
        AbstractDungeon.combatRewardScreen.rewards.add(new TinyHouseUpgrade1Card(1));
        AbstractDungeon.combatRewardScreen.positionRewards();

        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
        if(AbstractDungeon.getCurrRoom().event!=null)AbstractDungeon.getCurrRoom().event.noCardsInRewards=prevNCIR;

        AbstractDungeon.overlayMenu.proceedButton.show();
        AbstractDungeon.overlayMenu.proceedButton.setLabel("Skip Rewards"); //TODO: localization, or copy from CallingBell.DESCRIPTIONS[2]
        AbstractDungeon.overlayMenu.cancelButton.hideInstantly();


    }

    public AbstractRelic makeCopy() {
        return new BGTinyHouse();
    }
}

