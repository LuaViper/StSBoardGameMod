package BoardGame.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect;

public class BGSozu extends AbstractBGRelic  {
    public static final String ID = "BGSozu";

    public BGSozu() {
        super("BGSozu", "sozu.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
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


    public AbstractRelic makeCopy() {
        return new BGSozu();
    }


    @SpirePatch2(clz= ObtainPotionAction.class,method="update",paramtypez = {})
    public static class ObtainPotionActionPatch{
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ObtainPotionAction __instance, float ___duration){
            if (___duration == Settings.ACTION_DUR_XFAST) {
                if (AbstractDungeon.player.hasRelic("BGSozu")) {
                    AbstractDungeon.player.getRelic("BGSozu").flash();
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz= RewardItem.class,method="claimReward",paramtypez = {})
    public static class RewardItemPatch{
        @SpirePrefixPatch public static SpireReturn<Boolean> Prefix(RewardItem __instance){
            if (__instance.type == RewardItem.RewardType.POTION) {
                if (AbstractDungeon.player.hasRelic("BGSozu")) {
                    AbstractDungeon.player.getRelic("BGSozu").flash();
                    return SpireReturn.Return(true);
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz= StorePotion.class,method="purchasePotion",paramtypez = {})
    public static class StorePotionPatch{
        @SpirePrefixPatch public static SpireReturn<Void> Prefix(){
            if (AbstractDungeon.player.hasRelic("BGSozu")) {
                AbstractDungeon.player.getRelic("BGSozu").flash();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz= ObtainPotionEffect.class,method="update",paramtypez = {})
    public static class ObtainPotionEffectPatch{
        @SpirePrefixPatch public static SpireReturn<Void> Prefix(ObtainPotionEffect __instance){
            if (AbstractDungeon.player.hasRelic("BGSozu")) {
                __instance.isDone=true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


}


