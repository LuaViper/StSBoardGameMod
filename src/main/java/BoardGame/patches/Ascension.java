package BoardGame.patches;

import BoardGame.characters.*;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.multicharacter.BGMultiCharacter;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.Collections;

//TODO: disable "ascension 14 unlocked!" message, if it exists

public class Ascension {
    static String[] A_TEXT = CardCrawlGame.languagePack.getUIString("BoardGame:AscensionModeDescriptions").TEXT;

    public static final int CURRENT_MAX_ASCENSION=13;

    public static void combineUnlockedAscensions(){
        //BGMultiCharacter's prefs are recalculated from solo characters every time the game is reset.


        //TODO: combine death counts too

        int totalVictories=0;
        int totalDeaths=0;
        ArrayList<Integer> maxLevels = new ArrayList<>();
        AbstractPlayer p,i,s,d,w;
        p=new BGMultiCharacter("Prefs Lookup", BGMultiCharacter.Enums.BG_MULTICHARACTER);
        Prefs multipref = p.getPrefs();
        i=new BGIronclad("Prefs Lookup", BGIronclad.Enums.BG_IRONCLAD);
        totalVictories+= i.getCharStat().getVictoryCount();
        totalDeaths+= i.getCharStat().getDeathCount();
        Prefs pref = i.getPrefs();
        maxLevels.add(pref.getInteger("ASCENSION_LEVEL", 0));
        s=new BGSilent("Prefs Lookup", BGSilent.Enums.BG_SILENT);
        totalVictories+= s.getCharStat().getVictoryCount();
        totalDeaths+= s.getCharStat().getDeathCount();
        pref = s.getPrefs();
        maxLevels.add(pref.getInteger("ASCENSION_LEVEL", 0));
        d = new BGDefect("Prefs Lookup", BGDefect.Enums.BG_DEFECT);
        totalVictories+= d.getCharStat().getVictoryCount();
        totalDeaths+= d.getCharStat().getDeathCount();
        pref = d.getPrefs();
        maxLevels.add(pref.getInteger("ASCENSION_LEVEL", 0));
        w = new BGWatcher("Prefs Lookup", BGWatcher.Enums.BG_WATCHER);
        totalVictories+= w.getCharStat().getVictoryCount();
        totalDeaths+= w.getCharStat().getDeathCount();
        pref = w.getPrefs();
        maxLevels.add(pref.getInteger("ASCENSION_LEVEL", 0));

        int maxLevel = Collections.max(maxLevels);
        multipref.putInteger("ASCENSION_LEVEL", maxLevel);
        i.getPrefs().putInteger("ASCENSION_LEVEL",maxLevel);
        s.getPrefs().putInteger("ASCENSION_LEVEL",maxLevel);
        d.getPrefs().putInteger("ASCENSION_LEVEL",maxLevel);
        w.getPrefs().putInteger("ASCENSION_LEVEL",maxLevel);
        multipref.putInteger("WIN_COUNT", totalVictories);
        multipref.putInteger("LOSE_COUNT", totalDeaths);
        multipref.flush();
        i.getPrefs().flush();
        s.getPrefs().flush();
        d.getPrefs().flush();
        w.getPrefs().flush();
    }

    @SpirePatch2(clz= CharacterSelectScreen.class,method="updateAscensionToggle",paramtypez={})
    public static class DisableAscensionPatch{
        @SpirePrefixPatch
        public static void Prefix(CharacterSelectScreen __instance, SeedPanel ___seedPanel, @ByRef boolean[] ___isAscensionModeUnlocked){
            if (!___seedPanel.shown) {
                for (CharacterOption o : __instance.options) {
                    //o.update();
                    if (o.selected) {
                        //if (o.c instanceof AbstractBGPlayer) {
                        if (o.c instanceof BGMultiCharacter) {
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


    @SpirePatch2(clz= TopPanel.class, method="renderDungeonInfo",
            paramtypez={SpriteBatch.class})
    public static class AscensionColorPatch{
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(TopPanel __instance,SpriteBatch sb) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon && AbstractDungeon.ascensionLevel==13) {
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont,
                        Integer.toString(AbstractDungeon.ascensionLevel),
                        (float)ReflectionHacks.getPrivate(__instance,TopPanel.class,"floorX") + 166.0F * Settings.scale,
                        ReflectionHacks.getPrivateStatic(TopPanel.class,"INFO_TEXT_Y"), Settings.GOLD_COLOR);
                if (__instance.ascensionHb != null)
                    __instance.ascensionHb.render(sb);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class,"RED_TEXT_COLOR");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    @SpirePatch2(clz = ProceedButton.class, method = "update")
    public static class DoubleBossPatch {
        @SpireInstrumentPatch
        public static ExprEditor Bar() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(ProceedButton.class.getName()) && m.getMethodName().equals("goToVictoryRoomOrTheDoor")) {
                        m.replace("{ if("+CardCrawlGame.class.getName()+".dungeon instanceof "+AbstractBGDungeon.class.getName()+
                                " && "+AbstractBGDungeon.class.getName()+".ascensionLevel>=13 "+
                                " && "+AbstractBGDungeon.class.getName()+".bossList.size() == 2) goToDoubleBoss(); else $_ = $proceed($$); }");
                    }
                }
            };
        }
    }

}
