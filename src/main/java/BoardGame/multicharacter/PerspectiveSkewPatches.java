package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGPlayer;
import basemod.abstracts.CustomPlayer;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PerspectiveSkewPatches {
  @SpirePatch2(clz = CustomPlayer.class, method = "movePosition", paramtypez = {float.class, float.class})
  public static class PlayerConstructorPatch {
    @SpirePrefixPatch
    public static void Prefix(AbstractPlayer __instance, @ByRef float[] ___x, @ByRef float[] ___y) {
      if (!(AbstractDungeon.player instanceof BGMultiCharacter))
        return; 
      int maxRows = ((BGMultiCharacter)AbstractDungeon.player).subcharacters.size();
      if (maxRows <= 1)
        return; 
      int whichRow = ((AbstractBGPlayer)__instance).currentRow;
      float multiplier = whichRow - (maxRows - 1) / 2.0F;
      float maxmultiplier = (maxRows - 1) / 2.0F;
      multiplier /= maxmultiplier;
      if (maxRows == 2)
        multiplier *= 0.75F; 
      float xmultiplier = -1.0F * multiplier * 0.25F + 1.0F;
      ___x[0] = ___x[0] - (Settings.WIDTH / 2);
      ___x[0] = ___x[0] * xmultiplier;
      ___x[0] = ___x[0] + (Settings.WIDTH / 2);
      float multiplier2 = (multiplier + 1.0F) / 2.0F;
      multiplier2 = (float)Math.pow(multiplier2, 0.5D);
      float max = 1.125F;
      float min = 0.25F;
      float range = max - min;
      float ymultiplier = multiplier2 * range + min;
      ___y[0] = ___y[0] * ymultiplier;
    }
  }
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\PerspectiveSkewPatches.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */