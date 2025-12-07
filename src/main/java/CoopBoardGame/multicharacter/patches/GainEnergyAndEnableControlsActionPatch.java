package CoopBoardGame.multicharacter.patches;

import CoopBoardGame.multicharacter.GainEnergyAndEnableControlsMultiAction;
import CoopBoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainEnergyAndEnableControlsAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//TODO: this is only called at the start of combat, not at the start of each turn!
public class GainEnergyAndEnableControlsActionPatch {

    @SpirePatch2(clz = GainEnergyAndEnableControlsAction.class, method = "update")
    public static class EnergyPatch1 {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(GainEnergyAndEnableControlsAction __instance) {
            if (AbstractDungeon.player instanceof MultiCharacter) {
                if (ActionPatches.Field.owner.get(__instance) == null) {
                    CoopBoardGame.CoopBoardGame.logger.info(
                        "called GainEnergyAndEnableControlsAction, subbing in GainEnergyAndEnableControlsMultiAction"
                    );
                    AbstractDungeon.actionManager.addToTop(
                        new GainEnergyAndEnableControlsMultiAction()
                    );
                    __instance.isDone = true;
                    return SpireReturn.Return();
                } else {
                    CoopBoardGame.CoopBoardGame.logger.warn(
                        "called GainEnergyAndEnableControlsAction with owner " +
                            ActionPatches.Field.owner.get(__instance) +
                            " when null owner was expected!"
                    );
                }
            }
            return SpireReturn.Continue();
        }
    }
}
