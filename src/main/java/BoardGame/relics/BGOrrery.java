package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGOrrery extends AbstractBGRelic  {
    public static final String ID = "BGOrrery";

    public BGOrrery() {
        super("BGOrrery", "orrery.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.CLINK);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



    public void onEquip() {
        for (int i = 0; i < 3; i++) {
            AbstractDungeon.getCurrRoom().addCardToRewards();
        }

        AbstractDungeon.combatRewardScreen.open(this.DESCRIPTIONS[1]);
        (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
    }


    public AbstractRelic makeCopy() {
        return new BGOrrery();
    }
}


