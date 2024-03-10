
package BoardGame.potions;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGCurse.BGParasite;
import BoardGame.powers.BGDoubleAttackPower;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

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



    @SpirePatch2(clz=BetterDiscardPileToHandAction.class,method="update",paramtypez={})
    public static class BetterDiscardPileToHandActionPatch {
        @SpireInsertPatch(
                locator = BGLiquidMemories.BetterDiscardPileToHandActionPatch.Locator.class,
                localvars = {"c"}
        )
        public static void Insert(@ByRef AbstractCard[] ___c, boolean ___setCost, int ___newCost) {
            if (___c[0] instanceof AbstractBGCard) {
                if(___setCost && ___newCost==0){
                    BoardGame.BoardGame.logger.info("Assigned Liquid Memories effect to "+___c[0]);
                    ((AbstractBGCard)___c[0]).hasLiquidMemoriesEffect=true;
                }else{
                    //TODO: complain very loudly
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "lighten");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    //hasLiquidMemoriesEffect
}