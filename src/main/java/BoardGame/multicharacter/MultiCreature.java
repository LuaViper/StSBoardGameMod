package BoardGame.multicharacter;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface MultiCreature {
    //public abstract int getCurrentRow();


    @SpirePatch(
            clz= AbstractCreature.class,
            method=SpirePatch.CLASS
    )
    public static class Field {
        public static SpireField<Integer> currentRow = new SpireField<>(() -> 0);
        public static SpireField<Integer> savedCurrentEnergy = new SpireField<>(() -> 0);
    }
}
