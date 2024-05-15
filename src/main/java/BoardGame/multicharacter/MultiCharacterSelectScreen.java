package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.characters.AbstractBGPlayer;
import BoardGame.characters.BGDefect;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGSilent;
import BoardGame.characters.BGWatcher;
import BoardGame.multicharacter.patches.UpdateActionPatch;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
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
  
  public String description;
  
  public ArrayList<MultiCharacterSelectButton> buttons = new ArrayList<>();
  
  public MultiCharacterSelectScreen() {
    if(true) {
      //Board Game
      this.buttons.add(new MultiCharacterSelectButton("The Ironclad", new BGIronclad("The Ironclad", BGIronclad.Enums.BG_IRONCLAD), "images/ui/charSelect/ironcladButton.png"));
      this.buttons.add(new MultiCharacterSelectButton("The Silent", new BGSilent("The Silent", BGSilent.Enums.BG_SILENT), "images/ui/charSelect/silentButton.png"));
      this.buttons.add(new MultiCharacterSelectButton("The Defect", new BGDefect("The Defect", BGDefect.Enums.BG_DEFECT), "images/ui/charSelect/defectButton.png"));
      this.buttons.add(new MultiCharacterSelectButton("The Watcher", new BGWatcher("The Watcher", BGWatcher.Enums.BG_WATCHER), "images/ui/charSelect/watcherButton.png"));
    }else {
      //Vanilla
      this.buttons.add(new MultiCharacterSelectButton("The Ironclad", BaseMod.findCharacter(AbstractPlayer.PlayerClass.IRONCLAD), "images/ui/charSelect/ironcladButton.png"));
      this.buttons.add(new MultiCharacterSelectButton("The Silent", BaseMod.findCharacter(AbstractPlayer.PlayerClass.THE_SILENT), "images/ui/charSelect/silentButton.png"));
      this.buttons.add(new MultiCharacterSelectButton("The Defect", BaseMod.findCharacter(AbstractPlayer.PlayerClass.DEFECT), "images/ui/charSelect/defectButton.png"));
      this.buttons.add(new MultiCharacterSelectButton("The Watcher", BaseMod.findCharacter(AbstractPlayer.PlayerClass.WATCHER), "images/ui/charSelect/watcherButton.png"));
    }

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
      for (AbstractPlayer c : ((MultiCharacter)AbstractDungeon.player).subcharacters) {
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
    MultiCharacter c = (MultiCharacter)AbstractDungeon.player;
    if (c.subcharacters.isEmpty()) {
      AbstractDungeon.overlayMenu.proceedButton.hide();
    } else {
      AbstractDungeon.overlayMenu.proceedButton.show();
    } 
    for (MultiCharacterSelectButton b : this.buttons)
      b.update(); 
  }
  
  public void render(SpriteBatch sb) {
    description = BoardGame.ENABLE_TEST_FEATURES ? "Choose up to 4 characters." : "Choose 1 character." ;
    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.description, (Settings.WIDTH / 2), Settings.HEIGHT/2f - 120.0F * Settings.scale, Settings.CREAM_COLOR);
    for (MultiCharacterSelectButton b : this.buttons)
      b.render(sb); 
  }
  
  @SpirePatch2(clz = CharacterOption.class, method = "updateInfoPosition", paramtypez = {})
  public static class CharacterSelectTextPatch {
    @SpirePrefixPatch
    private static SpireReturn<Void> Prefix(CharacterOption __instance, @ByRef float[] ___infoX, @ByRef float[] ___infoY) {
      if (!(__instance.c instanceof MultiCharacter))
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

  //TODO LATER: this patch will be unnecessary once multichar mode becomes baseline
  @SpirePatch2(clz = CharacterOption.class, method = "renderInfo")
  public static class Foo {
    @SpireInstrumentPatch
    public static ExprEditor Bar() {
      return new ExprEditor() {
        public void edit(FieldAccess m) throws CannotCompileException {
          if (m.getClassName().equals(CharacterOption.class.getName()) && m.getFieldName().equals("flavorText")) {
            m.replace("$_ = "+MultiCharacterSelectScreen.class.getName()+".flavorTextPatch(this);");
          }
        }
      };
    }
  }
  public static String flavorTextPatch(CharacterOption __instance){
    CharSelectInfo charInfo=ReflectionHacks.getPrivate(__instance, CharacterOption.class, "charInfo");
    if(charInfo.player instanceof MultiCharacter){
      if(BoardGame.ENABLE_TEST_FEATURES){
        return charInfo.flavorText+" NL Play up to four characters at once!";
      }
    }
    return charInfo.flavorText;
  }
}


