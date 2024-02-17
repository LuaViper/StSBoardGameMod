
package BoardGame.potions;

import BoardGame.actions.BGUseShivAction;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGAncientPotion
        extends AbstractPotion {
    public static final String POTION_ID = "BGAncientPotion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGAncientPotion");

    public BGAncientPotion() {
        super(potionStrings.NAME, "BGAncientPotion", AbstractPotion.PotionRarity.UNCOMMON, AbstractPotion.PotionSize.FAIRY, AbstractPotion.PotionColor.ANCIENT);
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
        AbstractPlayer p = AbstractDungeon.player;
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGWeakened"));
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGVulnerable"));
        }
    }

    



    public AbstractPotion makeCopy() {
        return new BGAncientPotion();
    }
}


