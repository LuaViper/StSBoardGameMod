package BoardGame.potions;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Iterator;

public class BGFairyPotion extends AbstractPotion {
    public static final String POTION_ID = "BGFairyPotion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGFairyPotion");




    public BGFairyPotion() {
        super(potionStrings.NAME, "BGFairyPotion", AbstractPotion.PotionRarity.RARE, AbstractPotion.PotionSize.FAIRY, AbstractPotion.PotionColor.FAIRY);
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
        float percent = this.potency / 100.0F;
        int healAmt = 2;

        if (healAmt < 1) {
            healAmt = 1;
        }

        AbstractDungeon.player.heal(healAmt, true);
        //not 100% confident player's HP was exactly 0 to begin with, so let's just make sure...
        AbstractDungeon.player.currentHealth=2;

        AbstractDungeon.topPanel.destroyPotion(this.slot);
    }


    public boolean canUse() {
        return false;
    }


    public int getPotency(int ascensionLevel) {
        return 2;
    }


    public AbstractPotion makeCopy() {
        return new BGFairyPotion();
    }


    @SpirePatch2(clz= AbstractPlayer.class,method="damage",paramtypez={DamageInfo.class})
    public static class BGFairyPotionPlayerDamagePatch {
        @SpireInsertPatch(
                locator = BGFairyPotion.BGFairyPotionPlayerDamagePatch.Locator.class,
                localvars = {}
        )
        public static SpireReturn<Void> Insert(AbstractPlayer __instance) {
            //we're not 100% sure this patch doesn't break anything, so only do it if we're playing the board game
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                ////
                // Behavior for Fairy Potion protecting from multihits
                ////
//                if (__instance.hasPotion("BGFairyPotion")) {
//                    //TODO: if it's ruled that Fairy Potion doesn't protect from multihits, just copy the vanilla fairy potion code block over to here
//                    AbstractDungeon.actionManager.addToBottom(new BGDestroyFairyPotionAction());
//                    BoardGame.BoardGame.logger.info("hasPotion(BGFairyPotion)==true");
//                    return SpireReturn.Return();
//                }

                ////
                // Behavior for Fairy Potion NOT protecting from multihits
                ////
                Iterator var4;
                if (__instance.hasPotion("BGFairyPotion")) {
                    var4 = __instance.potions.iterator();

                    while(var4.hasNext()) {
                        AbstractPotion p = (AbstractPotion)var4.next();
                        if (p.ID.equals("BGFairyPotion")) {
                            p.flash();
                            __instance.currentHealth = 0;
                            p.use(__instance);
                            AbstractDungeon.topPanel.destroyPotion(p.slot);
                            return SpireReturn.Return();
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPotion");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}


