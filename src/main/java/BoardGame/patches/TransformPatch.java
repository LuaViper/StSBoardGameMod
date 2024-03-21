package BoardGame.patches;

import BoardGame.BoardGame;
import BoardGame.powers.AbstractBGPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.*;
import javassist.expr.Expr;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.ArrayList;

import static BoardGame.characters.BGCurse.Enums.BG_CURSE;

public class TransformPatch {


    public static CardGroup getTransformableCards(){
        CardGroup purgeable = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck
                .getPurgeableCards());
        CardGroup retVal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        for (AbstractCard c : purgeable.group) {
            if (c.color!=BG_CURSE)
                retVal.group.add(c);
        }
        return retVal;
    }




    @SpirePatch2(clz=CardGroup.class,method="getPurgeableCards")
    public static class BGAscendersBanePurgePatch {
        @SpirePostfixPatch
        public static CardGroup Postfix(CardGroup __result){
            CardGroup retVal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : __result.group) {
                //other nonpurgeable cards have already been filtered
                if (!c.cardID.equals("BGAscendersBane"))
                    retVal.group.add(c);
            }
            return retVal;
        }
    }

}
