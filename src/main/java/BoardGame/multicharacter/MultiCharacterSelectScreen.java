package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGCharacter;
import BoardGame.characters.BGDefect;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGSilent;
import BoardGame.characters.BGWatcher;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import BoardGame.ui.OverlayMenuPatches;

public class MultiCharacterSelectScreen extends CustomScreen {
  public boolean isDone = false;
  
  final Logger logger = LogManager.getLogger(MultiCharacterSelectScreen.class.getName());
  
  public static class Enum {
    @SpireEnum
    public static AbstractDungeon.CurrentScreen MULTI_CHARACTER_SELECT;
  }
  
  public AbstractDungeon.CurrentScreen curScreen() {
    return Enum.MULTI_CHARACTER_SELECT;
  }
  
  public String description = "Choose up to 4 characters.";
  
  public ArrayList<MultiCharacterSelectButton> buttons = new ArrayList<>();
  
  public MultiCharacterSelectScreen() {
    this.buttons.add(new MultiCharacterSelectButton("The Ironclad", BaseMod.findCharacter(BGIronclad.Enums.BG_IRONCLAD), "images/ui/charSelect/ironcladButton.png"));
    this.buttons.add(new MultiCharacterSelectButton("The Silent", BaseMod.findCharacter(BGSilent.Enums.BG_SILENT), "images/ui/charSelect/silentButton.png"));
    this.buttons.add(new MultiCharacterSelectButton("The Defect", BaseMod.findCharacter(BGDefect.Enums.BG_DEFECT), "images/ui/charSelect/defectButton.png"));
    this.buttons.add(new MultiCharacterSelectButton("The Watcher", BaseMod.findCharacter(BGWatcher.Enums.BG_WATCHER), "images/ui/charSelect/watcherButton.png"));
    for (int i = 0; i < this.buttons.size(); i++) {
      Hitbox hb = ((MultiCharacterSelectButton)this.buttons.get(i)).hb;
      hb.x = Settings.WIDTH / 2.0F + (i - 1.5F) * 232.0F * Settings.scale;
      hb.y = Settings.HEIGHT / 2.0F;
      hb.x -= MultiCharacterSelectButton.HB_W / 2.0F;
      hb.y -= MultiCharacterSelectButton.HB_W / 2.0F;
    } 
  }
  
  private void open() {
    this.isDone = false;
    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE)
      AbstractDungeon.previousScreen = AbstractDungeon.screen; 
    reopen();
  }
  
  public void reopen() {
    AbstractDungeon.screen = curScreen();
    AbstractDungeon.isScreenUp = true;
    AbstractDungeon.overlayMenu.hideBlackScreen();
    ((MultiCharacterRowBoxes)OverlayMenuPatches.OverlayMenuExtraInterface.multiCharacterRowBoxes.get(AbstractDungeon.overlayMenu)).show();
    for (MultiCharacterSelectButton b : this.buttons) {
      b.selected = false;
      for (AbstractBGCharacter c : ((BGMultiCharacter)AbstractDungeon.player).subcharacters) {
        if (c.name.equals(b.c.name))
          b.selected = true; 
      } 
    } 
  }
  
  public void openingSettings() {
    AbstractDungeon.previousScreen = curScreen();
  }
  
  public void close() {
    genericScreenOverlayReset();
    ((MultiCharacterRowBoxes)OverlayMenuPatches.OverlayMenuExtraInterface.multiCharacterRowBoxes.get(AbstractDungeon.overlayMenu)).hide();
  }
  
  public void update() {
    if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
      this.isDone = true;
      AbstractDungeon.closeCurrentScreen();
      return;
    } 
    BGMultiCharacter c = (BGMultiCharacter)AbstractDungeon.player;
    if (c.subcharacters.isEmpty()) {
      AbstractDungeon.overlayMenu.proceedButton.hide();
    } else {
      AbstractDungeon.overlayMenu.proceedButton.show();
    } 
    for (MultiCharacterSelectButton b : this.buttons)
      b.update(); 
  }
  
  public void render(SpriteBatch sb) {
    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.description, (Settings.WIDTH / 2), Settings.HEIGHT - 180.0F * Settings.scale, Settings.CREAM_COLOR);
    for (MultiCharacterSelectButton b : this.buttons)
      b.render(sb); 
  }
  
  @SpirePatch2(clz = CharacterOption.class, method = "updateInfoPosition", paramtypez = {})
  public static class CharacterSelectTextPatch {
    @SpirePrefixPatch
    private static SpireReturn<Void> Prefix(CharacterOption __instance, @ByRef float[] ___infoX, @ByRef float[] ___infoY) {
      if (!(__instance.c instanceof BGMultiCharacter))
        return SpireReturn.Continue(); 
      if (__instance.selected) {
        ___infoX[0] = MathHelper.uiLerpSnap(___infoX[0], Settings.WIDTH / 2.0F - 500.0F * Settings.scale + ((Float)ReflectionHacks.getPrivateStatic(CharacterOption.class, "DEST_INFO_X")).floatValue());
      } else {
        ___infoX[0] = MathHelper.uiLerpSnap(___infoX[0], ((Float)ReflectionHacks.getPrivateStatic(CharacterOption.class, "START_INFO_X")).floatValue());
      } 
      ___infoY[0] = Settings.HEIGHT / 2.0F + 250.0F * Settings.scale;
      return SpireReturn.Return();
    }
  }
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\MultiCharacterSelectScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */