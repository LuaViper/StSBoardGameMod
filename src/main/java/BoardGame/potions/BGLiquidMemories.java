
package BoardGame.potions;

import BoardGame.powers.BGDoubleAttackPower;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGLiquidMemories
        extends AbstractPotion {
    public static final String POTION_ID = "BGLiquidMemories";

    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("LiquidMemories");

    public BGLiquidMemories() {
        super(potionStrings.NAME, "BGLiquidMemories", AbstractPotion.PotionRarity.UNCOMMON, AbstractPotion.PotionSize.EYE, AbstractPotion.PotionEffect.NONE, new Color(225754111), new Color(389060095), null);
        this.isThrown = false;
    }

    public int getPrice() {return 3;}

    public void initializeData() {
        this.potency = getPotency();
        if (this.potency == 1) {
            this.description = potionStrings.DESCRIPTIONS[0];
        } else {
            this.description = potionStrings.DESCRIPTIONS[1] + this.potency + potionStrings.DESCRIPTIONS[2];
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    public void use(AbstractCreature target) {
        addToBot((AbstractGameAction)new BetterDiscardPileToHandAction(this.potency, 0));
    }

    public int getPotency(int ascensionLevel) {
        return 1;
    }

    public AbstractPotion makeCopy() {
        return new BGLiquidMemories();
    }
}