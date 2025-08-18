
package BoardGame.potions;

import BoardGame.powers.BGBurstPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGSkillPotion
        extends AbstractPotion {
    public static final String POTION_ID = "BGSkillPotion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGSkillPotion");

    public BGSkillPotion() {
        super(potionStrings.NAME, "BGSkillPotion", PotionRarity.COMMON, PotionSize.CARD, PotionColor.GREEN);
        this.isThrown = false;
    }

    public int getPrice() {return 2;}
    public int getPotency(int ascensionLevel) {
        return 1;
    }
    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        AbstractPlayer abstractPlayer = AbstractDungeon.player;
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT)
            addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)abstractPlayer, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGBurstPower((AbstractCreature)abstractPlayer, this.potency), this.potency));
    }
    



    public AbstractPotion makeCopy() {
        return new BGSkillPotion();
    }
}


