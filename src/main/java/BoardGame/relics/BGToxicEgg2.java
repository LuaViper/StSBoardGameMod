package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier.BOSS;
import static com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier.STARTER;

public class BGToxicEgg2 extends AbstractBGRelic  {
    public static final String ID = "BGToxic Egg 2";

    public BGToxicEgg2() {
        super("BGToxic Egg 2", "toxicEgg.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.SOLID);
        this.counter = 3;
    }
    public int getPrice() {return 8;}

    public boolean usableAsPayment(){
        return this.counter>0;
    }

    public void setCounter(int setCounter) {
        this.counter = setCounter;
        if (this.counter == -2) {
            usedUp();
            //AbstractDungeon.player.loseRelic("BGMolten Egg 2");
            this.counter = -2;
            BGMoltenEgg2.resetCardUpgradesWhenEggsUsedUp();
        }
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onEquip() {
        for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
            if (reward.cards != null) {
                for (AbstractCard c : reward.cards) {
                    onPreviewObtainCard(c);
                }
            }
        }
    }


    public void onPreviewObtainCard(AbstractCard c) {
        Logger logger = LogManager.getLogger(BGToxicEgg2.class.getName());
        //logger.info("BGToxicEgg: onPreviewObtainCard");
        if(this.counter>0) {
            if (c.type == AbstractCard.CardType.SKILL && c.canUpgrade() && !c.upgraded) {
                c.upgrade();
            }
        }
    }


    public void onObtainCard(AbstractCard c) {
        if(this.counter>0) {
            if (c.type == AbstractCard.CardType.SKILL) {
                if (c.canUpgrade() && !c.upgraded) {
                    c.upgrade();
                }
                if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite && AbstractDungeon.actNum >= 2)) {
                    this.flash();
                    this.counter -= 1;
                    if (this.counter <= 0) {
                        this.counter = -2;
                    }
                    setCounter(this.counter);
                }
            }
        }
    }




    public AbstractRelic makeCopy() {
        return new BGToxicEgg2();
    }
}


