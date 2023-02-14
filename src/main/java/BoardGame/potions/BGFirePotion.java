package BoardGame.potions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class BGFirePotion
        extends AbstractPotion {
    public static final String POTION_ID = "BGFire Potion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("Fire Potion");




    public BGFirePotion() {
        super(potionStrings.NAME, "BGFire Potion", AbstractPotion.PotionRarity.COMMON, AbstractPotion.PotionSize.SPHERE, AbstractPotion.PotionColor.FIRE);
        this.isThrown = true;
        this.targetRequired = true;
    }

    public int getPrice() {return 2;}

    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        DamageInfo info = new DamageInfo((AbstractCreature)AbstractDungeon.player, this.potency, DamageInfo.DamageType.THORNS);
        info.applyEnemyPowersOnly(target);
        addToBot((AbstractGameAction)new DamageAction(target, info, AbstractGameAction.AttackEffect.FIRE));
    }


    public int getPotency(int ascensionLevel) {
        return 4;
    }


    public AbstractPotion makeCopy() {
        return new BGFirePotion();
    }
}


