package BoardGame.patches;

import BoardGame.cards.BGCurse.BGAscendersBane;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.dungeons.BGExordium;
import BoardGame.multicharacter.patches.UpdateActionPatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class Ascension259Patch {


    @SpirePatch2(clz=AbstractDungeon.class, method="dungeonTransitionSetup")
    public static class MaxHPPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            //we DO have to override heal-after-boss (HealAfterBossPatch below)
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                if (AbstractDungeon.ascensionLevel >= 6) {
                    player.heal(4, false);
                } else {
                    player.heal(player.maxHealth, false);
                }
            }

            //we do NOT have to override Exordium-specific checks (technically we're reimplementing them instead)
            if(CardCrawlGame.dungeon instanceof BGExordium) {
                if (AbstractDungeon.ascensionLevel >= 2)
                    player.decreaseMaxHealth(player.getAscensionMaxHPLoss());
                if (AbstractDungeon.ascensionLevel >= 5) {
                    player.masterDeck.addToTop(new BGAscendersBane());
                    UnlockTracker.markCardAsSeen("BGAscendersBane");
                }
                if (AbstractDungeon.ascensionLevel >= 9)
                    player.currentHealth = MathUtils.round(player.maxHealth - 1);
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "dungeonTransitionSetup")
    public static class HealAfterBossPatch {
        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()) && m.getMethodName().equals("heal")) {
                        m.replace("{ if(!("+CardCrawlGame.class.getName()+".dungeon instanceof "+ AbstractBGDungeon.class.getName()+")){ $_ = $proceed($$); } }");
                    }
                }
            };
        }
    }

}
