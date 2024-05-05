package BoardGame.multicharacter.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import javax.naming.Context;

public class MonsterGroupPatches {
    //TODO: eventually need to patch other monsters-are-gone checks, such as Escaped

    @SpirePatch(clz = MonsterGroup.class, method = "areMonstersBasicallyDead")
    public static class MonstersGonePatch1 {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Foo() {
            MonsterGroup original=AbstractDungeonMonsterPatches.Field.originalMonsters.get(AbstractDungeon.getCurrRoom());
            if(original!=null && !original.monsters.isEmpty()) {
                for (AbstractMonster m : original.monsters) {
                    if (!m.isDying && !m.isEscaping)
                        return SpireReturn.Return(false);
                }
            }
            return SpireReturn.Continue();
        }
    }
}
