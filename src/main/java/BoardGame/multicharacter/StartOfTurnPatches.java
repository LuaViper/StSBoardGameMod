package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGCharacter;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class StartOfTurnPatches {
  @SpirePatch2(clz = AbstractRoom.class, method = "update", paramtypez = {})
  public static class DrawPatch {
    @SpireInsertPatch(locator = Locator.class, localvars = {})
    public static void Insert() {
      if (!(AbstractDungeon.player instanceof BGMultiCharacter))
        return; 
      for (int i = ((BGMultiCharacter)AbstractDungeon.player).subcharacters.size() - 1; i >= 0; i--) {
        AbstractBGCharacter c = ((BGMultiCharacter)AbstractDungeon.player).subcharacters.get(i);
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DrawCardAction((AbstractCreature)c, c.gameHandSize));
      } 
    }
    
    private static class Locator extends SpireInsertLocator {
      public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
        Matcher.NewExprMatcher newExprMatcher = new Matcher.NewExprMatcher(EnableEndTurnButtonAction.class);
        return LineFinder.findInOrder(ctMethodToPatch, new ArrayList(), (Matcher)newExprMatcher);
      }
    }
  }
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\StartOfTurnPatches.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */