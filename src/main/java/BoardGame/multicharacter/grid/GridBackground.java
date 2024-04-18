package BoardGame.multicharacter.grid;

import BoardGame.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;
import java.util.Collections;


public class GridBackground {



    //TODO NEXT: grid can't actually go on overlay menu, it needs to show up behind characters

    public ArrayList<GridSubgrid> subGrids=new ArrayList<>();
    public GridSubgrid playerGrid;
    public GridSubgrid enemyGrid;

    public final float INITIAL_TILE_SPAWN_TIMER=1f;
    public final float TILE_SPAWN_SPEED=1/16f;


    public boolean visible=false;
    public float tileSpawnTimer=INITIAL_TILE_SPAWN_TIMER;
    public float offsetX=0;
    public float offsetY=0;
    public static Texture tileImg = TextureLoader.getTexture("BoardGameResources/images/ui/gridtile.png");

    //TODO: during spireshield fight, there are always 4 rows (instead of # players)
    public GridBackground(){
        resetGridAtStartOfCombat();
    }

    public void resetGridAtStartOfCombat(){
        tileSpawnTimer=INITIAL_TILE_SPAWN_TIMER;
        subGrids.clear();
        playerGrid=new GridSubgrid();
        playerGrid.screenOffsetX = Settings.WIDTH*(1/3f)- GridTile.TILE_WIDTH/2f*Settings.scale;
        playerGrid.offsetY = 350*Settings.scale;
        enemyGrid = new GridSubgrid();
        enemyGrid.screenOffsetX = Settings.WIDTH*(2/3f)- GridTile.TILE_WIDTH/2f*Settings.scale;
        enemyGrid.offsetY = 350*Settings.scale;
        subGrids.add(playerGrid);
        subGrids.add(enemyGrid);
        enemyGrid.createTilesForEnemies();
    }

    public void update(){
        ArrayList<GridTile> tilesSpawningIn=new ArrayList<>();
        ArrayList<GridTile> tilesDespawning=new ArrayList<>();
        for(GridSubgrid sub : subGrids){
            sub.update();
            for(GridTile tile : sub.tiles){
                if(tile.shouldBeVisible && tile.targetFade<=0F){
                    tilesSpawningIn.add(tile);
                }else if(!tile.shouldBeVisible && tile.targetFade>0F){
                    tilesDespawning.add(tile);
                }
            }
        }


        if(tileSpawnTimer>0)
            tileSpawnTimer-= Gdx.graphics.getDeltaTime();
        if (tileSpawnTimer <= 0) {
            tileSpawnTimer=0;
            if(!tilesSpawningIn.isEmpty()) {
                Collections.shuffle(tilesSpawningIn);
                tilesSpawningIn.get(0).targetFade=1.0F;
                tileSpawnTimer=TILE_SPAWN_SPEED;
            }else if(!tilesDespawning.isEmpty()){
                Collections.shuffle(tilesDespawning);
                tilesDespawning.get(0).targetFade=0.0F;
                tileSpawnTimer=TILE_SPAWN_SPEED;
            }
        }
    }
    public void render(SpriteBatch sb){
        for(GridSubgrid sub : subGrids){
            sub.render(sb);
        }
//        if(this.visible){
//
//            sb.setColor(Color.WHITE);
//
//            for(int i=0;i<4;i+=1){
//                sb.draw(tileImg, 416*Settings.scale, (350+163*i)*Settings.scale, 32.0F, 32.0F, 216F*Settings.scale, 163F*Settings.scale, 1F, 1F, 0, 0, 0, 216, 163, false, false);
//
//                for(int j=0;j<5;j+=1){
//                    sb.draw(tileImg, (416-50+216*2+(216*j))*Settings.scale, (350+163*i)*Settings.scale, 32.0F, 32.0F, 216F*Settings.scale, 163F*Settings.scale, 1F, 1F, 0, 0, 0, 216, 163, false, false);
//                }
//            }
//
//        }
    }


}
