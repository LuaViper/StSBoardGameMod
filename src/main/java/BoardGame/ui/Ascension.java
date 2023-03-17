package BoardGame.ui;

import BoardGame.characters.BGIronclad;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

public class Ascension {
    @SpirePatch2(clz= CharacterSelectScreen.class,method="updateAscensionToggle",paramtypez={})
    public static class DisableAscensionPatch{
        @SpirePrefixPatch
        public static void Prefix(CharacterSelectScreen __instance, SeedPanel ___seedPanel, @ByRef boolean[] ___isAscensionModeUnlocked){
            if (!___seedPanel.shown) {
                for (CharacterOption o : __instance.options) {
                    //o.update();
                    if (o.selected) {
                        if (o.c instanceof BGIronclad) {
                            ___isAscensionModeUnlocked[0] = false;
                            __instance.isAscensionMode=false;
                        }
                    }
                }
            }
        }
    }
}
