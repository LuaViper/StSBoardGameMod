package CoopBoardGame.multiplayer.rows;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

/**
 * Interface for creatures that can be assigned to rows in multiplayer combat.
 * Provides a SpireField for tracking row position on any AbstractCreature.
 */
public interface MultiCreature {

    @SpirePatch(clz = AbstractCreature.class, method = SpirePatch.CLASS)
    public static class Field {
        public static SpireField<Integer> currentRow = new SpireField<>(() -> 0);
    }
}
