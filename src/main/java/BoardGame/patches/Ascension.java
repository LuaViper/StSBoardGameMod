package BoardGame.patches;

import BoardGame.characters.AbstractBGPlayer;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

public class Ascension {
    static String[] A_TEXT = CardCrawlGame.languagePack.getUIString("BoardGame:AscensionModeDescriptions").TEXT;

    static final int CURRENT_MAX_ASCENSION=12;

    @SpirePatch2(clz= CharacterSelectScreen.class,method="updateAscensionToggle",paramtypez={})
    public static class DisableAscensionPatch{
        @SpirePrefixPatch
        public static void Prefix(CharacterSelectScreen __instance, SeedPanel ___seedPanel, @ByRef boolean[] ___isAscensionModeUnlocked){
            if (!___seedPanel.shown) {
                for (CharacterOption o : __instance.options) {
                    //o.update();
                    if (o.selected) {
                        if (o.c instanceof AbstractBGPlayer) {
                            //___isAscensionModeUnlocked[0] = false;
                            //__instance.isAscensionMode=false;
                            if((int)ReflectionHacks.getPrivate(o,CharacterOption.class,"maxAscensionLevel")>CURRENT_MAX_ASCENSION)
                                ReflectionHacks.setPrivate(o,CharacterOption.class,"maxAscensionLevel",CURRENT_MAX_ASCENSION);
                        }
                    }
                }
            }
        }
    }

    //These two patches are redundant with updateAscensionToggle postfix

//    @SpirePatch2(clz=CharacterOption.class, method="incrementAscensionLevel",paramtypez = {int.class})
//    public static class IncrementTextPatch{
//        @SpirePostfixPatch
//        public static void Postfix(int ___maxAscensionLevel, int ___level){
//            if (___level > ___maxAscensionLevel)
//                return;
//            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = A_TEXT[___level-1];
//        }
//    }
//    @SpirePatch2(clz=CharacterOption.class, method="decrementAscensionLevel",paramtypez = {int.class})
//    public static class DecrementTextPatch{
//        @SpirePostfixPatch
//        public static void Postfix(CharacterOption __instance, int ___level){
//            if(___level <= 0)
//                return;
//            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = A_TEXT[___level-1];
//        }
//    }

    @SpirePatch2(clz=CharacterSelectScreen.class, method="updateAscensionToggle",paramtypez = {})
    public static class AscensionTextPatch{
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance, int ___ascensionLevel){
            boolean isABGCharacter=false;
            for(CharacterOption o : __instance.options){
                if(o.selected){
                    if(o.c instanceof AbstractBGPlayer){
                        isABGCharacter=true;
                    }
                }
            }
            if(!isABGCharacter)return;

            ////reminder: this block isn't necessary -- current ascension setting is saved per-character
//            if(AbstractDungeon.ascensionLevel>CURRENT_MAX_ASCENSION){
//                ___ascensionLevel=AbstractDungeon.ascensionLevel=CURRENT_MAX_ASCENSION;
//            }
            if(___ascensionLevel <= 0 || ___ascensionLevel > 20)
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = "";
            else
                CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = A_TEXT[___ascensionLevel-1];
        }
    }

    @SpirePatch2(clz= TopPanel.class, method="setupAscensionMode",paramtypez = {})
    public static class AscensionTextPatch2{
        @SpirePostfixPatch
        public static void Postfix(@ByRef String[] ___ascensionString){
            if(AbstractDungeon.player instanceof AbstractBGPlayer) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < AbstractDungeon.ascensionLevel; i++) {
                    sb.append(A_TEXT[i]);
                    if (i != AbstractDungeon.ascensionLevel - 1)
                        sb.append(" NL ");
                }
                ___ascensionString[0] = sb.toString();
            }
        }
    }


}
