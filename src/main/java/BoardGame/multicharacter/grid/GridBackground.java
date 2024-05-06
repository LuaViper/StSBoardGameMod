package BoardGame.multicharacter.grid;

import BoardGame.BoardGame;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;
import java.util.Collections;


public class GridBackground {

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
        if(!BoardGame.ENABLE_TEST_FEATURES)return;
        tileSpawnTimer=INITIAL_TILE_SPAWN_TIMER;
        subGrids.clear();
        playerGrid=new GridSubgrid();
        playerGrid.screenOffsetX = Settings.WIDTH*(1/3f)/Settings.scale- GridTile.TILE_WIDTH/2f;
        playerGrid.offsetY = 350;
        enemyGrid = new GridSubgrid();
        enemyGrid.screenOffsetX = Settings.WIDTH*(2/3f)/Settings.scale- GridTile.TILE_WIDTH/2f;
        enemyGrid.offsetY = 350;
        subGrids.add(playerGrid);
        subGrids.add(enemyGrid);
        enemyGrid.createTilesForEnemies();
    }

    public void update(){
        if(!BoardGame.ENABLE_TEST_FEATURES)return;
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
        if(!BoardGame.ENABLE_TEST_FEATURES)return;
        if(!visible)return;
        for(GridSubgrid sub : subGrids){
            sub.render(sb);
        }
    }


}
