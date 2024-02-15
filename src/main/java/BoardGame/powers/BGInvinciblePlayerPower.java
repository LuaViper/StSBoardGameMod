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

public class BGInvinciblePlayerPower extends AbstractBGPower {
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGIntangible");
    public static final String POWER_ID = "BGIntangible";
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGInvinciblePlayerPower(AbstractCreature owner, int turns) {
        this.name = NAME;
        this.ID = "BGIntangible";
        this.owner = owner;
        this.amount = 1;
        if(owner instanceof AbstractPlayer){
            AbstractRelic r=((AbstractPlayer)owner).getRelic("BoardGame:BGTheDieRelic");
            if(r!=null){
                if(((BGTheDieRelic)r).tookDamageThisTurn) { //TODO: move flag to AbstractBGPlayer, if possible (but requires turn start event)
                    this.amount = 0;
                }
            }
        }
        updateDescription();
        //loadRegion("intangible");
        //this.priority = 75;
        loadRegion("heartDef");
        this.priority = 99;
    }

    public void stackPower(int stackAmount) {
        //do nothing
        //TODO: make sure amount stays at 0 if it's already 0
        //this.fontScale = 8.0F;
        //this.amount=1;
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


    public void atStartOfTurn() {
        //flash();

        addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGIntangible"));
    }


    @SpirePatch2(clz = AbstractCreature.class, method = "decrementBlock",
            paramtypez={DamageInfo.class, int.class})
    public static class InvincibleDecrementBlockPatch {
        @SpirePostfixPatch
        public static int decrementBlock(int __result,AbstractCreature __instance, DamageInfo info, int damageAmount) {
            if(__instance.hasPower("BGIntangible")){
                AbstractPower p=__instance.getPower("BGIntangible");
                if(__result>p.amount){
                    __result=p.amount;
                }
                p.amount-=__result;
                p.updateDescription();
            }
            if(__result>0 && __instance==AbstractDungeon.player){
                AbstractRelic r=((AbstractPlayer)__instance).getRelic("BoardGame:BGTheDieRelic");
                if(r!=null){
                    ((BGTheDieRelic)r).tookDamageThisTurn=true;
                }
            }
            return __result;
        }
    }



}


