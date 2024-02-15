package BoardGame.cards;

import BoardGame.relics.BGCalipers;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.HandCheckAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public interface CardDoesNotDiscardWhenPlayed {

    //TODO LATER: what happens if we play Corpse Explosion while Corruption is active?
    //TODO: this is totally wrong -- only avoid discarding when card is played, not at any other time card is discarded!
    @SpirePatch(clz = UseCardAction.class, method = "update",
            paramtypez = {})
    public static class UseCardActionUpdatePatch {
        @SpireInsertPatch(
                locator= CardDoesNotDiscardWhenPlayed.UseCardActionUpdatePatch.Locator.class,
                localvars = {}
        )
        public static SpireReturn<Void> Insert(UseCardAction __instance, @ByRef AbstractCard[] ___targetCard){
            if(!(___targetCard[0] instanceof CardDoesNotDiscardWhenPlayed))
                return SpireReturn.Continue();

            //SKIP moveToDrawPile, but do everything else
            ___targetCard[0].exhaustOnUseOnce=false;
            ___targetCard[0].dontTriggerOnUseCard=false;
            AbstractDungeon.actionManager.addToBottom(new HandCheckAction());
            ReflectionHacks.RMethod tickDuration=ReflectionHacks.privateMethod(AbstractGameAction.class,"tickDuration");
            tickDuration.invoke(__instance);

            return SpireReturn.Return();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class,"moveToDiscardPile");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

    }


}
