package BoardGame.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;

public interface MixedAttacks {
    public int intentDmg1();
    public int intentDmg2();
    public boolean isMixedAttacking();

    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntentTip",
            paramtypez={})
    public static class updateIntentTipPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> updateIntentTip(AbstractMonster __instance, PowerTip ___intentTip){
            if(__instance instanceof MixedAttacks
            && __instance.intent == AbstractMonster.Intent.ATTACK
            &&((MixedAttacks)__instance).isMixedAttacking()) {
                ___intentTip.header = "Aggressive"; //TODO: localization
                ___intentTip.body = "This enemy intends to NL #yAttack once for #b"
                        + ((MixedAttacks) __instance).intentDmg1() + " damage"
                        +" NL and again for #b"
                        + ((MixedAttacks) __instance).intentDmg2() + " damage.";

                ___intentTip.img = ReflectionHacks.privateMethod(AbstractMonster.class, "getAttackIntentTip").invoke((AbstractMonster)__instance);

                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz = AbstractMonster.class, method = "renderDamageRange",
            paramtypez={SpriteBatch.class})
    public static class renderDamageRangePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> renderDamageRange(AbstractMonster __instance, SpriteBatch sb, BobEffect ___bobEffect, Color ___intentColor){
            if(__instance instanceof MixedAttacks
                    && __instance.intent == AbstractMonster.Intent.ATTACK
                    &&((MixedAttacks)__instance).isMixedAttacking()) {

                if (__instance.intent.name().contains("ATTACK")) {
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont,
                            Integer.toString( ((MixedAttacks)__instance).intentDmg1() )
                            + "+"
                            + Integer.toString( ((MixedAttacks)__instance).intentDmg2() )                            ,
                            __instance.intentHb.cX - 30.0F * Settings.scale,
                            __instance.intentHb.cY + ___bobEffect.y - 12.0F * Settings.scale, ___intentColor);

                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


}
