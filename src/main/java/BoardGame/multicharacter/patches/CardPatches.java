package BoardGame.multicharacter.patches;

import BoardGame.cards.BGGoldenTicket;
import BoardGame.characters.AbstractBGCharacter;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import javax.naming.Context;
import java.util.ArrayList;

public class CardPatches {
    @SpirePatch(
            clz=AbstractCard.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractBGCharacter> owner = new SpireField<>(() -> null);
    }



    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class MarkCardOwnerPatch {
        @SpireInsertPatch(locator = CardPatches.MarkCardOwnerPatch.Locator.class,
                localvars = {"foundClasses"}
        )
        private static void Insert(AbstractPlayer __instance) {
            //called immediately after drawPile.initializeDeck
            if (__instance instanceof AbstractBGCharacter) {
                for(AbstractCard c : __instance.drawPile.group) {
                    CardPatches.Field.owner.set(c,(AbstractBGCharacter)__instance);
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
                    ContextPatches.pushContext(CardPatches.Field.owner.get(__instance));
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
                    ContextPatches.popContext();
                }
            }
        }
    }
}
