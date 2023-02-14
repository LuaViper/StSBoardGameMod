package BoardGame.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class BGEctoplasm extends AbstractBGRelic  {
    public static final String ID = "BGEctoplasm";

    public BGEctoplasm() {
        super("BGEctoplasm", "ectoplasm.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }


    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }


    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[1] + this.DESCRIPTIONS[0];
    }


    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }


    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster++;
    }


    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster--;
    }


    public boolean canSpawn() {
        if (AbstractDungeon.actNum > 1) {
            return false;
        }
        return true;
    }


    public AbstractRelic makeCopy() {
        return new BGEctoplasm();
    }


    @SpirePatch2(clz = AbstractPlayer.class, method = "gainGold",
            paramtypez = {int.class})
    public static class EctoplasmGainGoldPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> gainGold(AbstractPlayer __instance, int amount) {
            if (__instance.hasRelic("BGEctoplasm")) {
                __instance.getRelic("BGEctoplasm").flash();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}


