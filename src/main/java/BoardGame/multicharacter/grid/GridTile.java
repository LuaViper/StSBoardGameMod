package BoardGame.multicharacter.grid;

import BoardGame.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;

import static com.badlogic.gdx.graphics.Color.WHITE;

public class GridTile {

    public static int SOURCE_TILE_WIDTH=216;
    public static int SOURCE_TILE_HEIGHT=163;
    public static int TILE_WIDTH = SOURCE_TILE_WIDTH;
    public static int TILE_HEIGHT = SOURCE_TILE_HEIGHT;
    public static Texture tileImg = TextureLoader.getTexture("BoardGameResources/images/ui/gridtile.png");
    public int width=1;
    public int height=1;
    public float offsetX=0;
    public float offsetY=0;
    public GridSubgrid parent;
    public AbstractCreature creature;
    public boolean shouldBeVisible=true;
    public float currentFade=0.0F;
    public float targetFade=0.0F;
    public final float FADE_IN_SPEED = 4.0F;
    public final float FADE_OUT_SPEED = 2.0F;

    @SpirePatch(
            clz=AbstractCreature.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<Float> originalDrawX = new SpireField<>(()->0f);
        public static SpireField<Float> originalDrawY = new SpireField<>(()->0f);
        public static SpireField<GridTile> gridTile = new SpireField<>(()->null);
        public static SpireField<Float> tileLerpAmount = new SpireField<>(()->0f);
        public static SpireField<Float> tileLerpTarget = new SpireField<>(()->0f);
    }

    public GridTile(){
        width=TILE_WIDTH;
        height=TILE_HEIGHT;
    }

    public void update(){
        if(currentFade<targetFade){
            currentFade += Gdx.graphics.getDeltaTime()*FADE_IN_SPEED;
            if(currentFade>targetFade)currentFade=targetFade;
        }
        if(currentFade>targetFade){
            currentFade -= Gdx.graphics.getDeltaTime()* FADE_IN_SPEED;
            if(currentFade<targetFade)currentFade=targetFade;
        }
        if(creature!=null){
            Field.gridTile.set(creature,this);
        }
        if(creature!=null){
            Field.tileLerpAmount.set(creature,
                    MathHelper.scaleLerpSnap(
                    Field.tileLerpAmount.get(creature),
                    Field.tileLerpTarget.get(creature)
                    ));
        }


    }

    public float getXPosition(){
        return parent.screenOffsetX+parent.centeringOffsetX+offsetX;
    }
    public float getYPosition(){
        return parent.offsetY+offsetY;
    }

    public float getCenterXPosition(){
        return parent.screenOffsetX+parent.centeringOffsetX+offsetX+TILE_WIDTH*(1/2f);
    }

    public void render(SpriteBatch sb){
        sb.setColor(Color.WHITE);
        float scale=1*currentFade;
        float scaleOffsetX=TILE_WIDTH*(1-scale)/2f;
        float scaleOffsetY=TILE_HEIGHT*(1-scale)/2f;
        sb.draw(tileImg, (getXPosition()+scaleOffsetX)*Settings.scale, (getYPosition()+scaleOffsetY) * Settings.scale, 0F, 0F, TILE_WIDTH, TILE_HEIGHT, scale * Settings.scale, scale* Settings.scale, 0, 0, 0, SOURCE_TILE_WIDTH, SOURCE_TILE_HEIGHT, false, false);
    }

}
