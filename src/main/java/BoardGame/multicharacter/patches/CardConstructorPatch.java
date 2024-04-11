package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CardConstructorPatch {
    @SpirePatch2(clz = AbstractCard.class,method = SpirePatch.CONSTRUCTOR,
    paramtypez = {String.class, String.class, String.class, int.class,
            String.class, AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class,
            AbstractCard.CardTarget.class, DamageInfo.DamageType.class})
    public static class Patch1 {
        @SpirePrefixPatch
        //public static void Foo(AbstractCard __instance, String id, String name, String imgUrl, int cost, String rawDescription, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target, DamageInfo.DamageType dType) {
        public static void Foo(AbstractCard __instance) {
            CardTargetingPatches.CardField.lastHoveredTarget.set(__instance,ContextPatches.currentTargetContext);
        }
    }
}
