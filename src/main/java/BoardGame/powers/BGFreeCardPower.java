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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

//TODO: are you allowed to duplicate Madness in the BG?

public class BGFreeCardPower extends AbstractBGPower {
    public static final String POWER_ID = "BGFreeCardPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGFreeCardPower");

    public BGFreeCardPower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = "BGFreeCardPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("swivel");
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
        //TODO: why !card.purgeOnUse check?
        if(true && !card.purgeOnUse && this.amount>0){
            flash();
            this.amount--;
            if (this.amount == 0)
                addToTop((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGFreeCardPower"));
        }
    }

    public void atEndOfTurn(boolean isPlayer) {
        //TODO: is this supposed to flash here?  some powers flash here
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner, this.owner, "BGFreeCardPower"));
    }

    @SpirePatch2(clz=AbstractCard.class, method="freeToPlay", paramtypez={})
    public static class freeToPlayPatch{
        @SpirePostfixPatch
        public static boolean Postfix(AbstractCard __instance, boolean __result){
            if (AbstractDungeon.player != null && AbstractDungeon.currMapNode != null &&
                    (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                    AbstractDungeon.player.hasPower("BGFreeCardPower")) {
                __result = true;
            }
            return __result;
        }
    }
}


