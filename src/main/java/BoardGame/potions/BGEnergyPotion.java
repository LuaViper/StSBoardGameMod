package BoardGame.potions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class BGEnergyPotion extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("Energy Potion");

    public static final String POTION_ID = "BGEnergy Potion";

    public BGEnergyPotion() {
        super(potionStrings.NAME, "BGEnergy Potion", AbstractPotion.PotionRarity.COMMON, AbstractPotion.PotionSize.BOLT, AbstractPotion.PotionColor.ENERGY);
        this.isThrown = false;
    }

    public int getPrice() {return 2;}


    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        addToBot((AbstractGameAction)new GainEnergyAction(this.potency));
    }


    public int getPotency(int ascensionLevel) {
        return 2;
    }


    public AbstractPotion makeCopy() {
        return new BGEnergyPotion();
    }
}


