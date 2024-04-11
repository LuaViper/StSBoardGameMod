package BoardGame.multicharacter.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ActionPatches {
    @SpirePatch(
            clz= AbstractGameAction.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractPlayer> owner = new SpireField<>(() -> null);
        public static SpireField<AbstractCreature> rowTarget = new SpireField<>(() -> null);
        public static SpireField<Boolean> alreadyCalledTargetSelectScreen = new SpireField<>(() -> false);
    }

    public static AbstractGameAction setOwnerFromConstructor(AbstractGameAction action,AbstractPlayer owner){
        Field.owner.set(action,owner);
        return action;
    }
}
