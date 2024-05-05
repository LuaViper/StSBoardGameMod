package BoardGame.multicharacter;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

//TODO: when a player profile is erased, unselectableplayer pref files are improperly staying behind
public interface UnselectablePlayer {
    @SpirePatch2(clz = BaseMod.class, method = "generateCharacterOptions")
    public static class CharacterOptionsPatch {
        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
                        m.replace("{ if(!(character instanceof "+UnselectablePlayer.class.getName()+")){ $_ = $proceed($$); } }");
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
//                    if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
//                        m.replace("{ if(!(character instanceof "+UnselectablePlayer.class.getName()+")){ $_ = $proceed($$); } }");
//                    }
                    if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
                        m.replace("{ if(!(character instanceof "+MultiCharacter.class.getName()+")){ $_ = $proceed($$); } }");
                    }
                }
            };
        }
    }
}

