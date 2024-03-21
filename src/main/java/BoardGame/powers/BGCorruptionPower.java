package BoardGame.powers;

import BoardGame.relics.BGShivs;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGCorruptionPower extends AbstractBGPower {
    public static final String POWER_ID = "BGCorruptionPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Corruption");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGCorruptionPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "BGCorruptionPower";
        this.owner = owner;
        this.amount = -1;
        this.description = DESCRIPTIONS[0];
        loadRegion("corruption");
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[1];
    }


    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.SKILL) {
            flash();
            action.exhaustCard = true;
        }
    }

    public static boolean isActive(){
        if (AbstractDungeon.player != null && AbstractDungeon.currMapNode != null &&
                (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                AbstractDungeon.player.hasPower("BGCorruptionPower")) {
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
                    AbstractDungeon.player.hasPower("BGCorruptionPower")
                    && __instance.type == AbstractCard.CardType.SKILL) {
                __result = true;
            }
            return __result;
        }
    }

}

