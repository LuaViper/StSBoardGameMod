package CoopBoardGame.multicharacter;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//TODO: when a player profile is erased, unselectableplayer pref files are improperly staying behind
/**
 * Marker interface for characters that should NOT appear in the character select screen.
 * Currently used to hide MultiCharacter from selection (the multi-character mode is accessed
 * through individual BG characters in coop multiplayer mode).
 */
public interface UnselectablePlayer {
    @SpirePatch2(clz = BaseMod.class, method = "generateCharacterOptions")
    public static class CharacterOptionsPatch {

        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (
                        m.getClassName().equals(ArrayList.class.getName()) &&
                        m.getMethodName().equals("add")
                    ) {
                        // Hide characters that implement UnselectablePlayer (currently just MultiCharacter)
                        m.replace(
                            "{ if(!(character instanceof " +
                                UnselectablePlayer.class.getName() +
                                ")){ $_ = $proceed($$); } }"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = BaseMod.class, method = "generateCustomCharacterOptions")
    public static class CustomGameOptionsPatch {

        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (
                        m.getClassName().equals(ArrayList.class.getName()) &&
                        m.getMethodName().equals("add")
                    ) {
                        // Also hide UnselectablePlayer characters from custom game modes
                        m.replace(
                            "{ if(!(character instanceof " +
                                UnselectablePlayer.class.getName() +
                                ")){ $_ = $proceed($$); } }"
                        );
                    }
                }
            };
        }
    }
}
