package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.ui.OverlayMenuPatches;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Defect;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.Iterator;

public class MultiCharacterSelectButton {
  public boolean selected = false;
  public boolean locked = false;

  private Color glowColor = new Color(1.0F, 0.8F, 0.2F, 0.0F);

  public MultiCharacterSelectButton(String optionName, AbstractPlayer c, String buttonUrl) {
    this.name = optionName;
    this.hb = new Hitbox(HB_W, HB_W);
    this.buttonImg = ImageMaster.loadImage(buttonUrl);
    this.c = c;
    this.charInfo = null;
    this.charInfo = c.getLoadout();
    this.flavorText = this.charInfo.flavorText;
    this.unlocksRemaining = 5 - UnlockTracker.getUnlockLevel(c.chosenClass);
  }

  public void saveChosenAscensionLevel(int level) {
    Prefs pref = this.c.getPrefs();
    pref.putInteger("LAST_ASCENSION_LEVEL", level);
    pref.flush();
  }

  public void update() {
    updateHitbox();
  }

  private void updateHitbox() {
    this.hb.update();
    this.hb.cX = this.hb.x + this.hb.width / 2.0F;
    this.hb.cY = this.hb.y + this.hb.height / 2.0F;
    if (this.hb.justHovered)
      CardCrawlGame.sound.playA("UI_HOVER", -0.3F);
    if (!this.hb.hovered || this.locked);
    if (InputHelper.justClickedLeft && !this.locked && this.hb.hovered) {
      CardCrawlGame.sound.playA("UI_CLICK_1", -0.4F);
      this.hb.clickStarted = true;
    }
    if (this.hb.clicked) {
      this.hb.clicked = false;
      if (!this.selected) {
        boolean SOLO_MODE_ONLY=!BoardGame.ENABLE_TEST_FEATURES;
        if(SOLO_MODE_ONLY) {
          MultiCharacterSelectScreen screen = (MultiCharacterSelectScreen) BaseMod.getCustomScreen(MultiCharacterSelectScreen.Enum.MULTI_CHARACTER_SELECT);
          for (MultiCharacterSelectButton b : screen.buttons) {
            b.selected = false;
          }
        }

        this.selected = true;

        MultiCharacter p = (MultiCharacter)AbstractDungeon.player;
        if(SOLO_MODE_ONLY) {
          p.subcharacters.clear();
        }

        this.c.doCharSelectScreenSelectEffect();

        AbstractPlayer newChar = (AbstractPlayer)this.c.newInstance();
        if(newChar instanceof Defect){
          newChar.maxOrbs=newChar.masterMaxOrbs=3;
        }
        p.subcharacters.add(newChar);
        newChar.initializeStarterDeck();
      } else {
        this.selected = false;
        MultiCharacter p = (MultiCharacter)AbstractDungeon.player;
        for (Iterator<AbstractPlayer> i = p.subcharacters.iterator(); i.hasNext(); ) {
          AbstractPlayer s = i.next();
          if (s.getClass() == this.c.getClass())
            i.remove();
        }
      }
      ((MultiCharacterRowBoxes) OverlayMenuPatches.OverlayMenuExtraInterface.multiCharacterRowBoxes.get(AbstractDungeon.overlayMenu)).remakeSwapButtonsAndPositionCharacters();
    }
  }

  public void render(SpriteBatch sb) {
    renderOptionButton(sb);
    this.hb.render(sb);
  }

  private void renderOptionButton(SpriteBatch sb) {
    if (this.selected) {
      this.glowColor.a = 0.25F + (MathUtils.cosDeg((float)(System.currentTimeMillis() / 4L % 360L)) + 1.25F) / 3.5F;
      sb.setColor(this.glowColor);
    } else {
      sb.setColor(BLACK_OUTLINE_COLOR);
    }
    sb.draw(ImageMaster.CHAR_OPT_HIGHLIGHT, this.hb.cX - 110.0F, this.hb.cY - 110.0F, 110.0F, 110.0F, 220.0F, 220.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 220, 220, false, false);
    if (!this.selected && !this.hb.hovered) {
      sb.setColor(Color.LIGHT_GRAY);
    } else {
      sb.setColor(Color.WHITE);
    }
    sb.draw(this.buttonImg, this.hb.cX - 110.0F, this.hb.cY - 110.0F, 110.0F, 110.0F, 220.0F, 220.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 220, 220, false, false);
  }

  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CharacterOption");

  public static final String[] TEXT = uiStrings.TEXT;

  public static final float HB_W = 150.0F * Settings.scale;

  private static final Color BLACK_OUTLINE_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.5F);

  private static final float NAME_OFFSET_Y = 200.0F * Settings.scale;

  private Texture buttonImg;

  public AbstractPlayer c;

  public Hitbox hb;

  private static final int BUTTON_W = 220;

  public static final String ASSETS_DIR = "images/ui/charSelect/";

  private static final int ICON_W = 64;

  private float infoX;

  private float infoY;

  public String name;

  private String hp;

  private int gold;

  private String flavorText;

  private CharSelectInfo charInfo;

  private int unlocksRemaining;
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\MultiCharacterSelectButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */