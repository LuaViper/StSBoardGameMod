package BoardGame.multicharacter.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class ActionPatches {
    @SpirePatch(
            clz= AbstractGameAction.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractPlayer> owner = new SpireField<>(() -> null);
    }

    public static AbstractGameAction setOwnerFromConstructor(AbstractGameAction action,AbstractPlayer owner){
        Field.owner.set(action,owner);
        return action;
    }
}
