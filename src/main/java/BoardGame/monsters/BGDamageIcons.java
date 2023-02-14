package BoardGame.monsters;

import BoardGame.patches.DefaultInsertPatch;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public interface BGDamageIcons {

    @SpirePatch2(clz = AbstractMonster.class, method = "getAttackIntentTip",
            paramtypez={})
    public static class getAttackIntentTipPatch {
        @SpirePostfixPatch
        public static Texture Postfix(AbstractMonster __instance, Texture __result, boolean ___isMultiDmg, int ___intentMultiAmt) {
            final Logger logger = LogManager.getLogger(DefaultInsertPatch.class.getName());
            //logger.info("getAttackIntentTip postfix");
            if (!(__instance instanceof BGDamageIcons)) {
                return __result;
            }

            ArrayList<String> emptyList;
            int tmp;
            if(___isMultiDmg){
                tmp = __instance.getIntentDmg() * ___intentMultiAmt;
            } else {
                tmp = __instance.getIntentDmg();
            }
            //logger.info("tmp: "+tmp);
            if (tmp < 1){
                return ImageMaster.INTENT_ATK_TIP_1;
            }
            if (tmp < 2){
                return ImageMaster.INTENT_ATK_TIP_2;
            }
            if (tmp < 3){
                return ImageMaster.INTENT_ATK_TIP_3;
            }
            if (tmp < 4){
                //logger.info("RETURN TIP_4 (3dmg): "+tmp);
                //__result=ImageMaster.INTENT_ATK_TIP_4;
                return ImageMaster.INTENT_ATK_TIP_4;
            }
            if (tmp < 5){
                return ImageMaster.INTENT_ATK_TIP_5;
            }
            if (tmp < 6) {
                return ImageMaster.INTENT_ATK_TIP_6;
            }

            return ImageMaster.INTENT_ATK_TIP_7;

        }
    }

    //these were written before we discovered SpireReturn.  can be changed to SpirePrefix when we have the time
    @SpirePatch2(clz = AbstractMonster.class, method = "getAttackIntent",
            paramtypez = {int.class})
    public static class getAttackIntentPatch {
        public static Texture Postfix(AbstractMonster __instance, Texture __result, int dmg) {
            final Logger logger = LogManager.getLogger(DefaultInsertPatch.class.getName());
            if (!(__instance instanceof BGDamageIcons)) {
                return __result;
            }
            //logger.info("getAttackIntent(dmg) postfix");
            if (dmg < 1)
                return ImageMaster.INTENT_ATK_1;
            if (dmg < 2)
                return ImageMaster.INTENT_ATK_2;
            if (dmg < 3)
                return ImageMaster.INTENT_ATK_3;
            if (dmg < 4)
                return ImageMaster.INTENT_ATK_4;
            if (dmg < 5)
                return ImageMaster.INTENT_ATK_5;
            if (dmg < 6) {
                return ImageMaster.INTENT_ATK_6;
            }
            return ImageMaster.INTENT_ATK_7;
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "getAttackIntent",
            paramtypez = {})
    public static class getAttackIntentPatch2 {
        public static Texture Postfix(AbstractMonster __instance, Texture __result, boolean ___isMultiDmg, int ___intentMultiAmt) {
            final Logger logger = LogManager.getLogger(DefaultInsertPatch.class.getName());

            if (!(__instance instanceof BGDamageIcons)) {
                return __result;
            }

            int tmp;
            if (___isMultiDmg) {
                tmp = __instance.getIntentDmg() * ___intentMultiAmt;
            } else {
                tmp = __instance.getIntentDmg();
            }

            // logger.info("getAttackIntent() postfix "+tmp);

            if (tmp < 1)
                return ImageMaster.INTENT_ATK_1;
            if (tmp < 2)
                return ImageMaster.INTENT_ATK_2;
            if (tmp < 3)
                return ImageMaster.INTENT_ATK_3;
            if (tmp < 4){
                //logger.info("...return INTENT_ATK_4");
                //__result=ImageMaster.INTENT_ATK_4;
                return ImageMaster.INTENT_ATK_4;
            }
            if (tmp < 5)
                return ImageMaster.INTENT_ATK_5;
            if (tmp < 6) {
                return ImageMaster.INTENT_ATK_6;
            }
            return ImageMaster.INTENT_ATK_7;
        }
    }

}
