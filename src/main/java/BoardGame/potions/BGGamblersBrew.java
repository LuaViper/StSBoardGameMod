
package BoardGame.potions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class BGGamblersBrew
        extends AbstractPotion {
    public static final String POTION_ID = "BGGamblersBrew";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGGamblersBrew");

    public BGGamblersBrew() {
        super(potionStrings.NAME, "BGGamblersBrew", AbstractPotion.PotionRarity.UNCOMMON, AbstractPotion.PotionSize.S, AbstractPotion.PotionColor.SMOKE);
        this.isThrown = false;
    }

    public static int doesPlayerHaveGamblersBrew(){
        for(AbstractPotion p : AbstractDungeon.player.potions){
            if(p instanceof BGGamblersBrew){
                return p.slot;
            }
        }
        return -1;
    }

    public int getPrice() {return 3;}
    public int getPotency(int ascensionLevel) {
        return 1;
    }
    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    public boolean canUse() {
        return false;
    }

    public void use(AbstractCreature target) {
        //do nothing
    }







    public AbstractPotion makeCopy() {
        return new BGGamblersBrew();
    }
}


