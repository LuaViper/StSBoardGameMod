//eggs are surprisingly not hardcoded!
// don't deduct charges unless (1) the card wasn't already upgraded AND (2) the player actually obtains the card

package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

public class BGMoltenEgg2 extends AbstractBGRelic  {
    public static final String ID = "BGMolten Egg 2";

    public BGMoltenEgg2() {
        super("BGMolten Egg 2", "stoneEgg.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.SOLID);
        this.counter = 3;
    }
    public int getPrice() {return 8;}


    public void setCounter(int setCounter) {
        this.counter = setCounter;
        if (this.counter == -2) {
            usedUp();
            //AbstractDungeon.player.loseRelic("BGMolten Egg 2");
            this.counter = -2;
            resetCardUpgradesWhenEggsUsedUp();
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
        Logger logger = LogManager.getLogger(BGMoltenEgg2.class.getName());
        logger.info("BGMoltenEgg: onPreviewObtainCard");
        if(this.counter>0) {
            if (c.type == AbstractCard.CardType.ATTACK && c.canUpgrade() && !c.upgraded) {
                c.upgrade();
            }
        }
    }


    public void onObtainCard(AbstractCard c) {
        if(this.counter>0) {
            if (c.type == AbstractCard.CardType.ATTACK) {
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
        return new BGMoltenEgg2();
    }



    public static void resetCardUpgradesWhenEggsUsedUp(){
        if(AbstractDungeon.currMapNode!=null && AbstractDungeon.getCurrRoom()!=null && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            {
                Iterator<AbstractCard> i = AbstractDungeon.shopScreen.coloredCards.iterator();
                while (i.hasNext()) {
                    AbstractCard card = i.next();
                    if (card.upgraded) {
                        AbstractCard newcard = card.makeCopy();
                        for (AbstractRelic r : AbstractDungeon.player.relics) {
                            r.onPreviewObtainCard(newcard);
                        }
                        newcard.current_x = card.current_x;
                        newcard.current_y = card.current_y;
                        newcard.target_x = card.current_x;
                        newcard.target_y = card.current_y;
                        newcard.price = card.price;
                        AbstractDungeon.shopScreen.coloredCards.set(AbstractDungeon.shopScreen.coloredCards.indexOf(card), newcard);
                    }
                }
            }
            {
                Iterator<AbstractCard> i = AbstractDungeon.shopScreen.colorlessCards.iterator();
                while (i.hasNext()) {
                    AbstractCard card = i.next();
                    if (card.upgraded) {
                        AbstractCard newcard = card.makeCopy();
                        for (AbstractRelic r : AbstractDungeon.player.relics) {
                            r.onPreviewObtainCard(newcard);
                        }
                        newcard.current_x = card.current_x;
                        newcard.current_y = card.current_y;
                        newcard.target_x = card.current_x;
                        newcard.target_y = card.current_y;
                        newcard.price = card.price;
                        AbstractDungeon.shopScreen.coloredCards.set(AbstractDungeon.shopScreen.colorlessCards.indexOf(card), newcard);
                    }
                }
            }
        }
    }
}


