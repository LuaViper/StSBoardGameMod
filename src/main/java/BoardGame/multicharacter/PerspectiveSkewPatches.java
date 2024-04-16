package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.monsters.AbstractBGMonster;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PerspectiveSkewPatches {

  //TODO: combine both into BGMultiCreature......somehow
  @SpirePatch2(clz = CustomPlayer.class, method = "movePosition", paramtypez = {float.class, float.class})
  public static class PlayerMovePatch {
    @SpirePrefixPatch
    public static void Foo(AbstractPlayer __instance, @ByRef float[] ___x, @ByRef float[] ___y) {
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

  @SpirePatch2(clz = AbstractMonster.class, method = "init")
  public static class MonsterInitPatch {
    @SpirePostfixPatch
    public static void Foo(AbstractMonster __instance, @ByRef float[] ___drawX, @ByRef float[] ___drawY) {
      if (CardCrawlGame.chosenCharacter!=BGMultiCharacter.Enums.BG_MULTICHARACTER)
        return;
      if (!(__instance instanceof BGMultiCreature))
        return;
      if(AbstractDungeon.getCurrRoom()==null)
        return;
      if(BGMultiCharacter.getSubcharacters()==null)
        return;
      int maxRows = BGMultiCharacter.getSubcharacters().size();
      if (maxRows <= 1)
        return;
      int whichRow = BGMultiCreature.Field.currentRow.get(__instance);
      float multiplier = whichRow - (maxRows - 1) / 2.0F;
      float maxmultiplier = (maxRows - 1) / 2.0F;
      multiplier /= maxmultiplier;
      if (maxRows == 2)
        multiplier *= 0.75F;
      float xmultiplier = -1.0F * multiplier * 0.25F + 1.0F;
      ___drawX[0] = ___drawX[0] - (Settings.WIDTH / 2);
      ___drawX[0] = ___drawX[0] * xmultiplier;
      ___drawX[0] = ___drawX[0] + (Settings.WIDTH / 2);
      float multiplier2 = (multiplier + 1.0F) / 2.0F;
      multiplier2 = (float)Math.pow(multiplier2, 0.5D);
      float max = 1.125F;
      float min = 0.25F;
      float range = max - min;
      float ymultiplier = multiplier2 * range + min;
      ___drawY[0] = ___drawY[0] * ymultiplier;
      ReflectionHacks.privateMethod(AbstractCreature.class,"refreshHitboxLocation").invoke(__instance);
      __instance.refreshIntentHbLocation();
    }
  }



}


