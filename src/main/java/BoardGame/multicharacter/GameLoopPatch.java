package BoardGame.multicharacter;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class GameLoopPatch {
  @SpirePatch2(clz = AbstractDungeon.class, method = "update", paramtypez = {})
  public static class RoomUpdatePatch {
    @SpireInsertPatch(locator = Locator.class, localvars = {})
    public static void Insert() {
      if (AbstractDungeon.player instanceof BGMultiCharacter) {
        BGMultiCharacter mc = (BGMultiCharacter)AbstractDungeon.player;
        for (int i = mc.subcharacters.size() - 1; i >= 0; i--) {
          AbstractDungeon.player = (AbstractPlayer)mc.subcharacters.get(i);
          AbstractDungeon.currMapNode.room.update();
        } 
        AbstractDungeon.player = (AbstractPlayer)mc;
      } 
    }
    
    private static class Locator extends SpireInsertLocator {
      public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
        Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(AbstractScene.class, "update");
        return LineFinder.findInOrder(ctMethodToPatch, new ArrayList(), (Matcher)methodCallMatcher);
      }
    }
  }
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\GameLoopPatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */