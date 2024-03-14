
package BoardGame.potions;

import BoardGame.ui.EntropicBrewPotionButton;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect;

import java.util.Iterator;

public class BGEntropicBrew extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGEntropicBrew");

    public static final String POTION_ID = "BGEntropicBrew";

    public BGEntropicBrew() {
        super(potionStrings.NAME, "BGEntropicBrew", AbstractPotion.PotionRarity.RARE, AbstractPotion.PotionSize.M, AbstractPotion.PotionEffect.RAINBOW, Color.WHITE, null, null);
        this.isThrown = false;
    }

    public int getPrice() {return 3;}


    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        //Potion slot check occurs before brew is removed, so we only need 1 open slot
        if (countOpenPotionSlots() >= 1) {
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                for (int i = 0; i < 2; i++)
                    addToBot((AbstractGameAction) new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true)));
            } else if (AbstractDungeon.player.hasRelic("BGSozu")) {
                AbstractDungeon.player.getRelic("BGSozu").flash();
            } else if (AbstractDungeon.player.hasRelic("Sozu")) {
                AbstractDungeon.player.getRelic("Sozu").flash();
            } else {
                for (int i = 0; i < 2; i++)
                    AbstractDungeon.effectsQueue.add(new ObtainPotionEffect(AbstractDungeon.returnRandomPotion()));
            }
        }else{
            if (AbstractDungeon.player.hasRelic("BGSozu")) {
                AbstractDungeon.player.getRelic("BGSozu").flash();
                return;
            } else if (AbstractDungeon.player.hasRelic("Sozu")) {
                AbstractDungeon.player.getRelic("Sozu").flash();
                return;
            }

            for(int i=0;i<2;i+=1) {
                EntropicBrewPotionButton.SetupButton(AbstractDungeon.returnRandomPotion());
            }

        }
    }

    public boolean canUse() {
        if (AbstractDungeon.actionManager.turnHasEnded &&
                (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            return false;
        }
        if ((AbstractDungeon.getCurrRoom()).event != null &&
                (AbstractDungeon.getCurrRoom()).event instanceof com.megacrit.cardcrawl.events.shrines.WeMeetAgain) {
            return false;
        }

        return true;
    }

    public int getPotency(int ascensionLevel) {
        if (AbstractDungeon.player != null)
            return AbstractDungeon.player.potionSlots;
        return 2;
    }
    public AbstractPotion makeCopy() {
        return new BGEntropicBrew();
    }



    public static int countOpenPotionSlots(){
        if(AbstractDungeon.player!=null) {
            int count=0;
            for (AbstractPotion p : AbstractDungeon.player.potions) {
                if (p instanceof PotionSlot) {
                    count+=1;
                }
            }
            return count;
        }
        return 0;
    }
}


