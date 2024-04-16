package BoardGame.multicharacter.patches;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CardPatches {
    @SpirePatch(
            clz=AbstractCard.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractBGPlayer> owner = new SpireField<>(() -> null);
    }



    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class MarkCardOwnerPatch {
        @SpireInsertPatch(locator = CardPatches.MarkCardOwnerPatch.Locator.class,
                localvars = {"foundClasses"}
        )
        private static void Insert(AbstractPlayer __instance) {
            //called immediately after drawPile.initializeDeck
            if (__instance instanceof AbstractBGPlayer) {
                for(AbstractCard c : __instance.drawPile.group) {
                    CardPatches.Field.owner.set(c,(AbstractBGPlayer)__instance);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(EndTurnButton.class, "enabled");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    //note that hasEnoughEnergy is additionally patched in BGConfusionPower
    @SpirePatch2(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class HasEnoughEnergyPatch1 {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard __instance) {
            if (CardCrawlGame.chosenCharacter == BGMultiCharacter.Enums.BG_MULTICHARACTER) {
                if (CardPatches.Field.owner.get(__instance) != null) {
                    ContextPatches.pushPlayerContext(CardPatches.Field.owner.get(__instance));
                }
            }
        }
    }
    @SpirePatch2(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class HasEnoughEnergyPatch2 {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            if (CardCrawlGame.chosenCharacter == BGMultiCharacter.Enums.BG_MULTICHARACTER) {
                if (CardPatches.Field.owner.get(__instance) != null) {
                    ContextPatches.popPlayerContext();
                }
            }
        }
    }
}