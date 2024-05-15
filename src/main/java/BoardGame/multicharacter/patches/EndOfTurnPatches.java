package BoardGame.multicharacter.patches;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.dungeons.BGTheBeyond;
import BoardGame.multicharacter.MultiCharacter;
import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderFixSwitches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.defect.TriggerEndOfTurnOrbsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import java.util.ArrayList;

public class EndOfTurnPatches {
    @SpirePatch2(clz= MonsterGroup.class, method="applyEndOfTurnPowers")
    public static class StatusCardColorPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {}
        )
        public static void Foo(MonsterGroup __instance) {
            if (AbstractDungeon.player instanceof MultiCharacter) {
                for(AbstractPlayer c : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(c);
                    for (AbstractPower p : AbstractDungeon.player.powers)
                        p.atEndOfRound();
                    ContextPatches.popPlayerContext();
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(MonsterGroup.class, "monsters");
                return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[1]};
            }
        }
    }




    @SpirePatch2(clz= GameActionManager.class, method="callEndOfTurnActions")
    public static class ApplyEndOfTurnActionsPatch {
        @SpirePostfixPatch
        public static void Foo() {
            if(AbstractDungeon.player instanceof MultiCharacter){
                for(AbstractPlayer ch : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(ch);
                    AbstractDungeon.getCurrRoom().applyEndOfTurnRelics();
                    ContextPatches.popPlayerContext();
                }
                for(AbstractPlayer ch : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(ch);
                    AbstractDungeon.getCurrRoom().applyEndOfTurnPreCardPowers();
                    ContextPatches.popPlayerContext();
                }
                for(AbstractPlayer ch : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(ch);
                    AbstractDungeon.actionManager.addToBottom(new TriggerEndOfTurnOrbsAction());
                    ContextPatches.popPlayerContext();
                }
                for(AbstractPlayer ch : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(ch);
                    for (AbstractCard c : AbstractDungeon.player.hand.group)
                        c.triggerOnEndOfTurnForPlayingCard();
                    ContextPatches.popPlayerContext();
                }
                for(AbstractPlayer ch : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(ch);
                    AbstractDungeon.player.stance.onEndOfTurn();
                    ContextPatches.popPlayerContext();
                }
                ////old behavior -- all actions for one player at a time -- no longer used
//                for(AbstractPlayer ch : MultiCharacter.getSubcharacters()) {
//                    ContextPatches.pushPlayerContext(ch);
//                    AbstractDungeon.getCurrRoom().applyEndOfTurnRelics();
//                    AbstractDungeon.getCurrRoom().applyEndOfTurnPreCardPowers();
//                    AbstractDungeon.actionManager.addToBottom(new TriggerEndOfTurnOrbsAction());
//                    for (AbstractCard c : AbstractDungeon.player.hand.group)
//                        c.triggerOnEndOfTurnForPlayingCard();
//                    AbstractDungeon.player.stance.onEndOfTurn();
//                    ContextPatches.popPlayerContext();
//                }
            }
        }
    }


}
