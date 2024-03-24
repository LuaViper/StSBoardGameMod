package BoardGame.multicharacter;

import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class MultiCharacterSwapButton {
  public boolean selected = false;
  
  public boolean locked = false;
  
  private Color glowColor = new Color(1.0F, 0.8F, 0.2F, 0.0F);
  
  public MultiCharacterSwapButton(String optionName, AbstractPlayer c, String buttonUrl) {
    this.name = optionName;
    this.hb = new Hitbox(HB_W, HB_W);
    this.buttonImg = TextureLoader.getTexture(buttonUrl);
    this.c = c;
    this.charInfo = null;
    this.charInfo = c.getLoadout();
    this.flavorText = this.charInfo.flavorText;
    this.unlocksRemaining = 5 - UnlockTracker.getUnlockLevel(c.chosenClass);
  }
  
  public void update() {
    updateHitbox();
  }
  
  private void updateHitbox() {
    this.hb.update();
    this.hb.cX = this.hb.x + this.hb.width / 2.0F;
    this.hb.cY = this.hb.y + this.hb.height / 2.0F;
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
    sb.draw(ImageMaster.CHAR_OPT_HIGHLIGHT, this.hb.cX - HB_W / 2.0F, this.hb.cY - HB_W / 2.0F, HB_W / 2.0F, HB_W / 2.0F, HB_W, HB_W, 1.0F, 1.0F, 0.0F, 0, 0, 64, 64, false, false);
    if (!this.selected && !this.hb.hovered) {
      sb.setColor(Color.LIGHT_GRAY);
    } else {
      sb.setColor(Color.WHITE);
    } 
    sb.draw(this.buttonImg, this.hb.cX - HB_W / 2.0F, this.hb.cY - HB_W / 2.0F, HB_W / 2.0F, HB_W / 2.0F, HB_W, HB_W, 1.0F, 1.0F, 0.0F, 0, 0, 64, 64, false, false);
  }
  
  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CharacterOption");
  
  public static final String[] TEXT = uiStrings.TEXT;
  
  public static final float HB_W = 128.0F * Settings.scale;
  
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


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\MultiCharacterSwapButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */