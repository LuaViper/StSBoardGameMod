package BoardGame.powers;

import BoardGame.relics.BGTheDieRelic;
import BoardGame.thedie.TheDie;
import BoardGame.ui.LockInRollButton;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGInvinciblePower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGInvinciblePower");
    public static final String POWER_ID = "BGInvinciblePower";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGInvinciblePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGInvinciblePower";
        this.owner = owner;
        this.amount = amount;

        updateDescription();
        loadRegion("heartDef");
        this.priority = 99;
    }

    public void stackPower(int stackAmount) {
        //do nothing
        this.fontScale = 8.0F;
        this.amount=1;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_INTANGIBLE", 0.05F);
    }

    public void updateDescription() {
        if (this.amount <= 0) {
            this.description = DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }




    @SpirePatch2(clz = AbstractCreature.class, method = "decrementBlock",
            paramtypez={DamageInfo.class, int.class})
    public static class InvincibleDecrementBlockPatch {
        @SpirePostfixPatch
        public static int decrementBlock(int __result,AbstractCreature __instance, DamageInfo info, int damageAmount) {
            if(__instance.hasPower("BGInvinciblePower")){
                AbstractPower p=__instance.getPower("BGInvinciblePower");
                if(__result>p.amount){
                    __result=p.amount;
                }
                p.amount-=__result;
                p.updateDescription();
            }
            return __result;
        }
    }

    //Weak immunity is handled in BGWeakPower

}


