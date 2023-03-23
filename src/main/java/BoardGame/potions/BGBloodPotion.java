//TODO: blood potion has different appearance in the BG

package BoardGame.potions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGBloodPotion
        extends AbstractPotion {
    public static final String POTION_ID = "BGBloodPotion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGBloodPotion");

    public BGBloodPotion() {
        super(potionStrings.NAME, "BGBloodPotion", AbstractPotion.PotionRarity.COMMON, AbstractPotion.PotionSize.H, AbstractPotion.PotionColor.WHITE);
        this.labOutlineColor = Settings.RED_RELIC_COLOR;
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
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot((AbstractGameAction)new HealAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 2));

        }
        else {


            AbstractDungeon.player.heal(2);
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
        return 2;
    }

    public AbstractPotion makeCopy() {
        return new BGBloodPotion();
    }
}


