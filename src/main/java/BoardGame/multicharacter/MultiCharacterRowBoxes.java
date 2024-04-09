package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import java.util.ArrayList;

public class MultiCharacterRowBoxes {
  private final Texture img = TextureLoader.getTexture("BoardGameResources/images/charSelect/MultiCharacterSelectGrid.png");
  
  private final float START_X = -800.0F * Settings.xScale;
  
  private final float DEST_X = 160.0F * Settings.scale;
  
  private float targetX;
  
  private float posX;
  
  private float posY;
  
  private final float SCALE = 2.0F;
  
  public ArrayList<MultiCharacterSwapButton> swapButtons = new ArrayList<>();
  
  public MultiCharacterRowBoxes() {
    this.posX = this.targetX = this.START_X;
    this.posY = (Settings.HEIGHT / 2) - 240.0F;
  }
  
  public void update() {
    this.posX = MathHelper.uiLerpSnap(this.posX, this.targetX);
    for (MultiCharacterSwapButton b : this.swapButtons) {
      b.update();
      b.hb.x = this.posX + 8.0F * Settings.scale * 2.0F;
    } 
  }
  
  public void show() {
    this.targetX = this.DEST_X;
  }
  
  public void hide() {
    this.targetX = this.START_X;
  }
  
  public void render(SpriteBatch sb) {
    sb.setColor(Color.WHITE);
    sb.draw(this.img, this.posX, this.posY, 0.0F, 0.0F, 80.0F, 320.0F, Settings.scale * 2.0F, Settings.scale * 2.0F, 0.0F, 0, 0, 80, 320, false, false);
    for (MultiCharacterSwapButton b : this.swapButtons)
      b.render(sb); 
  }
  
  public void remakeSwapButtonsAndPositionCharacters() {
    this.swapButtons = new ArrayList<>();
    BGMultiCharacter c = (BGMultiCharacter)AbstractDungeon.player;
    int i;
    for (i = 0; i < c.subcharacters.size(); i++) {
      AbstractBGPlayer s = c.subcharacters.get(i);
      s.currentRow = i;
      MultiCharacterSwapButton b = new MultiCharacterSwapButton(s.name, (AbstractPlayer)s, s.getMultiSwapButtonUrl());
      Hitbox hb = b.hb;
      hb.y = this.posY + 8.0F + (80 * i) * Settings.scale * 2.0F;
      this.swapButtons.add(b);
    } 
    for (i = 0; i < c.subcharacters.size(); i++) {
      AbstractBGPlayer s = c.subcharacters.get(i);
      s.movePosition(Settings.WIDTH * 0.25F, AbstractDungeon.floorY);
    } 
  }
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\MultiCharacterRowBoxes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */