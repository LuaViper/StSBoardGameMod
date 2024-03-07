package BoardGame.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGFreeAttackPower extends AbstractBGPower {
    public static final String POWER_ID = "BGFreeAttackPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGFreeAttackPower");
    public BGFreeAttackPower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = "BGFreeAttackPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("swivel");
    }

    //TODO: are we or are we not allowed to duplicate Madness (or Swivel) in the BG?
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if(this.amount>1)this.amount=1;
    }
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = powerStrings.DESCRIPTIONS[0];
        } else {
            this.description = powerStrings.DESCRIPTIONS[1] + this.amount + powerStrings.DESCRIPTIONS[2];
        }
    }


    public void onUseCard(AbstractCard card, UseCardAction action) {
        //if (card.type == AbstractCard.CardType.ATTACK && !card.purgeOnUse && this.amount > 0) {
        if (card.type == AbstractCard.CardType.ATTACK && this.amount > 0) {
            //TODO: PRETTY sure we count copied cards here, but maybe doublecheck.
            flash();
            this.amount--;
            if (this.amount == 0)
                addToTop((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGFreeAttackPower"));
        }
    }

    public void atEndOfTurn(boolean isPlayer) {
        //TODO: is this supposed to flash here?  some powers flash here
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGFreeAttackPower"));
    }

    public static boolean isActive(){
        if (AbstractDungeon.player != null && AbstractDungeon.currMapNode != null &&
                (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                AbstractDungeon.player.hasPower("BGFreeAttackPower")) {
            return true;
        }
        return false;
    }

    @SpirePatch2(clz=AbstractCard.class, method="freeToPlay", paramtypez={})
    public static class freeToPlayPatch{
        @SpirePostfixPatch
        public static boolean Postfix(AbstractCard __instance, boolean __result){
            if (AbstractDungeon.player != null && AbstractDungeon.currMapNode != null &&
                    (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                    AbstractDungeon.player.hasPower("BGFreeAttackPower")
                && __instance.type == AbstractCard.CardType.ATTACK) {
                __result = true;
            }
            return __result;
        }
    }
}


