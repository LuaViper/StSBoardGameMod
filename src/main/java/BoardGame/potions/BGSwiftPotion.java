package BoardGame.potions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class BGSwiftPotion extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("Swift Potion");

    public static final String POTION_ID = "BGSwift Potion";

    public int getPrice() {return 2;}

    public BGSwiftPotion() {
        super(potionStrings.NAME, "BGSwift Potion", AbstractPotion.PotionRarity.COMMON, AbstractPotion.PotionSize.H, AbstractPotion.PotionColor.SWIFT);
        this.isThrown = false;
    }


    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[1] + this.potency + potionStrings.DESCRIPTIONS[2];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, this.potency));
    }


    public int getPotency(int ascensionLevel) {
        return 3;
    }


    public AbstractPotion makeCopy() {
        return new BGSwiftPotion();
    }
}


