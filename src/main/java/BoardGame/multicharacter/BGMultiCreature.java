package BoardGame.multicharacter;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface BGMultiCreature {
    //public abstract int getCurrentRow();


    @SpirePatch(
            clz= AbstractMonster.class,
            method=SpirePatch.CLASS
    )
    public static class Field {
        public static SpireField<Integer> currentRow = new SpireField<>(() -> 0);
    }
}
