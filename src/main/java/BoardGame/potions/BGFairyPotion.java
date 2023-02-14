//package BoardGame.potions;
//
//import com.megacrit.cardcrawl.core.AbstractCreature;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.helpers.PowerTip;
//import com.megacrit.cardcrawl.localization.PotionStrings;
//import com.megacrit.cardcrawl.potions.AbstractPotion;
//
//public class BGFairyPotion extends AbstractPotion {
//    public static final String POTION_ID = "BGFairyPotion";
//    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGFairyPotion");
//
//
//
//
//    public BGFairyPotion() {
//        super(potionStrings.NAME, "BGFairyPotion", AbstractPotion.PotionRarity.RARE, AbstractPotion.PotionSize.FAIRY, AbstractPotion.PotionColor.FAIRY);
//        this.isThrown = false;
//    }
//
//    public int getPrice() {return 3;}
//
//    public void initializeData() {
//        this.potency = getPotency();
//        this.description = potionStrings.DESCRIPTIONS[0];
//        this.tips.clear();
//        this.tips.add(new PowerTip(this.name, this.description));
//    }
//
//
//    public void use(AbstractCreature target) {
//        float percent = this.potency / 100.0F;
//        int healAmt = 2;
//
//        if (healAmt < 1) {
//            healAmt = 1;
//        }
//
//        AbstractDungeon.player.heal(healAmt, true);
//        //not 100% confident player's HP was exactly 0 to begin with, so let's just make sure...
//        AbstractDungeon.player.currentHealth=2;
//
//        AbstractDungeon.topPanel.destroyPotion(this.slot);
//    }
//
//
//    public boolean canUse() {
//        return false;
//    }
//
//
//    public int getPotency(int ascensionLevel) {
//        return 2;
//    }
//
//
//    public AbstractPotion makeCopy() {
//        return new BGFairyPotion();
//    }
//}
//
//
