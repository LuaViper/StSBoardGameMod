package BoardGame.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.SmithOption;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//TODO: BGTinyHouse needs to upgrade a NON-random card
//TODO: BGTinyHouse breaks Neow's quickstart reward screen (rewards are autopicked, relic is removed)

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
        AbstractDungeon.getCurrRoom().rewards.clear();
        //card is already added to rewards by default in CombatRewardScreen.setupItemReward
        ///AbstractDungeon.getCurrRoom().addCardReward( //TODO: can we use noCardsInRewards flag to make the gained card show up at the top of the reward list?
        AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion());
        AbstractDungeon.getCurrRoom().addGoldToRewards(GOLD_AMT);  //TODO: AddGoldToRewardsPatch will incorrectly add a WhiteBeastStatue potion here if the player has it
        AbstractDungeon.getCurrRoom().addRelicToRewards(new BGUpgrade1Card());

        AbstractDungeon.combatRewardScreen.open(this.DESCRIPTIONS[3]);
        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
    }

    public AbstractRelic makeCopy() {
        return new BGTinyHouse();
    }
}

