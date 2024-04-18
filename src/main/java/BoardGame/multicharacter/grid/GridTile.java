package BoardGame.multicharacter.grid;

import BoardGame.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

public class GridTile {

    public static int TILE_WIDTH=216;
    public static int TILE_HEIGHT=163;
    public static Texture tileImg = TextureLoader.getTexture("BoardGameResources/images/ui/gridtile.png");
    public int width=1;
    public int height=1;
    public float offsetX=416;
    public float offsetY=350;
    public GridBackground parent;

    public boolean shouldBeVisible=true;
    public float currentFade=0.0F;
    public float targetFade=0.0F;
    public final float FADE_IN_SPEED = 4.0F;
    public final float FADE_OUT_SPEED = 2.0F;

    public void update(){
        if(currentFade<targetFade){
            currentFade += Gdx.graphics.getDeltaTime()* FADE_IN_SPEED;
            if(currentFade>targetFade)currentFade=targetFade;
        }
        if(currentFade>targetFade){
            currentFade -= Gdx.graphics.getDeltaTime()* FADE_IN_SPEED;
            if(currentFade<targetFade)currentFade=targetFade;
        }
    }
    public void render(SpriteBatch sb, float gridOffsetX, float gridOffsetY){
        float scale=1*currentFade;
        float scaleOffsetX=TILE_WIDTH*(1-scale)/2;
        float scaleOffsetY=TILE_HEIGHT*(1-scale)/2;
        sb.draw(tileImg, gridOffsetX+(offsetX+scaleOffsetX) * Settings.scale, gridOffsetY+(offsetY+scaleOffsetY) * Settings.scale, 0F, 0F, 216F * Settings.scale, 163F * Settings.scale, scale, scale, 0, 0, 0, TILE_WIDTH, TILE_HEIGHT, false, false);
    }

}
