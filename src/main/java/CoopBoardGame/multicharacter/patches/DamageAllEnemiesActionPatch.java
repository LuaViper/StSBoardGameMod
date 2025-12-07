package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.NullMonster;
import CoopBoardGame.screen.TargetSelectScreen;
import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;

public class DamageAllEnemiesActionPatch {

    @SpirePatch2(clz = DamageAllEnemiesAction.class, method = "update")
    public static class AbstractDungeonGetColorlessRewardCardsPatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Foo(DamageAllEnemiesAction __instance) {
            CoopBoardGame.CoopBoardGame.logger.info("MAX_HAND_SIZE: " + BaseMod.MAX_HAND_SIZE);
            if (CardTargetingPatches.moreThanOneRowExists()) {
                if (
                    ActionPatches.Field.rowTarget.get(__instance) == null ||
                    ActionPatches.Field.rowTarget.get(__instance) instanceof NullMonster
                ) {
                    if (!ActionPatches.Field.alreadyCalledTargetSelectScreen.get(__instance)) {
                        //TODO: check "damage ALL enemies" field, return Continue if set
                        //                if(!alreadyCalledTargetSelect) {
                        //TODO: this works for shieldspear, but in proper multiplayer we want the target select screen to autocomplete if there is only 1 row remaining (even if that row has multiple enemies)
                        TargetSelectScreen.TargetSelectAction tssAction = target -> {
                            if (target != null) {
                                ActionPatches.Field.rowTarget.set(__instance, target);
                            }
                        };
                        //addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, )); //doesn't work -- softlock forever (current damage action is still active)
                        BaseMod.openCustomScreen(
                            TargetSelectScreen.Enum.TARGET_SELECT,
                            tssAction,
                            "Choose a target row for area-of-effect attack.",
                            false
                        );
                        ActionPatches.Field.alreadyCalledTargetSelectScreen.set(__instance, true);
                    }
                    //even if we've already called target select screen, stop action here
                    //(this is obvious, but we have to put this comment here to remind ourselves)
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
}
