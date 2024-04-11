package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCreature;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.util.Objects;

//TODO: make sure VANILLA Spot Weakness still works after patch, it's the only vanilla card using SELF_AND_ENEMY
//note: SELF_AND_ENEMY is only used by vanilla Spot Weakness
// ALL is only used by vanilla Vault

public class CardTargetingPatches {
    @SpirePatch(
            clz=AbstractPlayer.class,
            method=SpirePatch.CLASS
    )
    public static class PlayerField
    {
        public static SpireField<AbstractCreature> lastHoveredTarget = new SpireField<>(() -> null);
    }
    @SpirePatch(
            clz=AbstractCard.class,
            method=SpirePatch.CLASS
    )
    public static class CardField
    {
        public static SpireField<AbstractCreature> lastHoveredTarget = new SpireField<>(() -> null);
    }

    public static boolean isUsingTargeting(AbstractCard card){
        if(card.target==AbstractCard.CardTarget.ENEMY)return true;
        else if(card.target== AbstractCard.CardTarget.ALL_ENEMY) {
            if(moreThanOneRowExists()){
                return true;
            }
        }
        return false;
    }
    public static boolean moreThanOneRowExists(){
        int row = -1;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if(m instanceof BGMultiCreature) {
                if (!m.isDeadOrEscaped()) {
                    int newRow = BGMultiCreature.Field.currentRow.get(m);
                    if (newRow != row) {
                        if (row != -1) {
                            return true;
                        }
                        row = newRow;
                    }
                }
            }
        }
        return false;
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "clickAndDragCards")
    public static class RowTargetingPatch {
        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.CardTarget.class.getName()) && m.getFieldName().equals("ENEMY")) {
                        m.replace("$_ = "+CardTargetingPatches.class.getName()+".isUsingTargeting(hoveredCard) ? hoveredCard.target : null;");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "renderHoverReticle",
            paramtypez = {SpriteBatch.class})
    public static class ReticlePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Foo(AbstractPlayer __instance, SpriteBatch sb) {
            if(__instance.hoveredCard.target == AbstractCard.CardTarget.ALL_ENEMY){
                if(true || isUsingTargeting(__instance.hoveredCard)){       //TODO: verify whether this check is redundant
                    AbstractMonster hoveredMonster = ReflectionHacks.getPrivate(__instance,AbstractPlayer.class,"hoveredMonster");
                    if (__instance.inSingleTargetMode && hoveredMonster != null) {
                        for(AbstractMonster m : AbstractDungeon.getMonsters().monsters){
                            if(Objects.equals(BGMultiCreature.Field.currentRow.get(m), BGMultiCreature.Field.currentRow.get(hoveredMonster))) {
                                m.renderReticle(sb);
                            }
                        }
                    }
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
    public static class HoveredMonsterPatch {
        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance) {
            CardTargetingPatches.PlayerField.lastHoveredTarget.set(__instance, ReflectionHacks.getPrivate(__instance,AbstractPlayer.class,"hoveredMonster"));
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "playCard")
    public static class PlayCardPatch {
        @SpirePrefixPatch
        public static void Foo(AbstractPlayer __instance) {
            CardTargetingPatches.CardField.lastHoveredTarget.set(__instance.hoveredCard, PlayerField.lastHoveredTarget.get(__instance));
        }
    }
}
