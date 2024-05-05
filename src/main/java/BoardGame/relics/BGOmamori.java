//Omamori is hardcoded ShowCardAndObtainEffect (24-26) + FastCardObtainEffect (25-27)



package BoardGame.relics;

import BoardGame.characters.BGCurse;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGOmamori extends AbstractBGRelic  {
    public static final String ID = "BGOmamori";

    public BGOmamori() {
        super("BGOmamori", "omamori.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.FLAT);
        //this.counter = 2;
    }

    public int getPrice() {return 5;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


//    public void setCounter(int setCounter) {
//        this.counter = setCounter;
//        if (setCounter == 0) {
//            usedUp();
//        } else if (setCounter == 1) {
//            this.description = this.DESCRIPTIONS[1];
//        }
//    }

    public void use() {
        flash();
//        this.counter--;
//        if (this.counter == 0) {
//            setCounter(0);
//        } else {
//            this.description = this.DESCRIPTIONS[1];
//        }
    }


//    public boolean canSpawn() {
//        return (Settings.isEndless || AbstractDungeon.floorNum <= 48);
//    }

    public static final Logger logger = LogManager.getLogger(BGOmamori.class.getName());

    public AbstractRelic makeCopy() {
        return new BGOmamori();
    }


    @SpirePatch(clz = ShowCardAndObtainEffect.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    public static class ShowCardAndObtainEffectPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ShowCardAndObtainEffect effect,
                                               AbstractCard card, float x, float y, boolean convergeCards, @ByRef boolean[] ___converge) {

            //logger.info("BGOMAMORI SHOWCARDANDOBTAINEFFECT PATCH");
            if (card.color == BGCurse.Enums.BG_CURSE && AbstractDungeon.player.hasRelic("BGOmamori")) {
                ((BGOmamori)AbstractDungeon.player.getRelic("BGOmamori")).use();
                logger.info(card.cardID+": BGOmamori goes DING");
                effect.duration = 0.0F;
                effect.isDone = true;
                ___converge[0] = convergeCards;

            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(clz = ShowCardAndObtainEffect.class, method = "update",
            paramtypez = {})
    public static class ShowCardAndObtainEffectPatch2 {
        @SpirePrefixPatch public static SpireReturn<Void> update(ShowCardAndObtainEffect __instance){
            //TODO: if this really is a bug with vanilla, make it optional to fix
            if (__instance.isDone) {
                return SpireReturn.Return();
            }
            //logger.info("ShowCardAndObtainEffect duration "+__instance.duration);
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(clz = FastCardObtainEffect.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class})
    public static class FastCardObtainEffectPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(FastCardObtainEffect effect,
                                               AbstractCard card, float x, float y) {
            //logger.info("BGOMAMORI FASTCARDOBTAINEFFECT PATCH");
            if (card.color == BGCurse.Enums.BG_CURSE && AbstractDungeon.player.hasRelic("BGOmamori")) {
                ((BGOmamori)AbstractDungeon.player.getRelic("BGOmamori")).use();
                logger.info(card.cardID+": BGOmamori goes DING");
                effect.duration = 0.0F;
                effect.isDone = true;
            }
            return SpireReturn.Continue();
        }
    }


}


