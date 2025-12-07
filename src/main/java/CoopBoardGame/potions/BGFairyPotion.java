package CoopBoardGame.potions;

import CoopBoardGame.dungeons.AbstractBGDungeon;
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
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class BGFairyPotion extends AbstractPotion {

    public static final String POTION_ID = "BGFairyPotion";
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(
        "CoopBoardGame:BGFairyPotion"
    );

    public BGFairyPotion() {
        super(
            potionStrings.NAME,
            POTION_ID,
            AbstractPotion.PotionRarity.RARE,
            AbstractPotion.PotionSize.FAIRY,
            AbstractPotion.PotionColor.FAIRY
        );
        this.isThrown = false;
    }

    public int getPrice() {
        return 3;
    }

    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    public void use(AbstractCreature target) {
        int healAmt = 2;

        if (healAmt < 1) {
            healAmt = 1;
        }

        AbstractDungeon.player.heal(healAmt, true);
        //not 100% confident player's HP was exactly 0 to begin with, so let's just make sure...
        AbstractDungeon.player.currentHealth = 2;

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

    @SpirePatch2(clz = AbstractPlayer.class, method = "damage", paramtypez = { DamageInfo.class })
    public static class BGFairyPotionPlayerDamagePatch {

        @SpireInsertPatch(
            locator = BGFairyPotion.BGFairyPotionPlayerDamagePatch.Locator.class,
            localvars = {}
        )
        public static SpireReturn<Void> Insert(AbstractPlayer __instance) {
            //we're not 100% sure this patch doesn't break anything, so only do it if we're playing the board game
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                ////
                // Behavior for Fairy Potion NOT protecting from multihits
                ////
                for (AbstractPotion potion : __instance.potions) {
                    if (!potion.ID.equals(POTION_ID)) {
                        continue;
                    }

                    potion.flash();
                    __instance.currentHealth = 0;
                    potion.use(__instance);
                    AbstractDungeon.topPanel.destroyPotion(potion.slot);
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch)
                throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(
                    AbstractPlayer.class,
                    "hasPotion"
                );
                return LineFinder.findInOrder(
                    ctMethodToPatch,
                    new ArrayList<Matcher>(),
                    finalMatcher
                );
            }
        }
    }
}
