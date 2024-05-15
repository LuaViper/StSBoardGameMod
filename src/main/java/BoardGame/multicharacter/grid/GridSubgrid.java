package BoardGame.multicharacter.grid;

import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.MultiCreature;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class GridSubgrid {

    public ArrayList<GridTile> tiles=new ArrayList<>();
    //offsetX and offsetY are lower left corner of the player subgrid.
    //for the enemy subgrid, it's the lower left corner of the bottom center tile.
    public float screenOffsetX;
    public float centeringOffsetX=0;
    public float offsetY;

    public int numRows=0;
    public GridSubgrid(){
        for(int i=0;i<MultiCharacter.getSubcharacters().size();i+=1){
            GridTile tile=new GridTile();
            tile.offsetX=0;
            tile.offsetY=i*GridTile.TILE_HEIGHT;
            tile.parent=this;
            tiles.add(tile);

            if(i<MultiCharacter.getSubcharacters().size()){
                tile.creature=MultiCharacter.getSubcharacters().get(i);
                GridTile.Field.gridTile.set(tile.creature,tile);
            }
        }
    }

    public void createTilesForEnemies(){
        tiles.clear();
        if(AbstractDungeon.getCurrMapNode()==null || AbstractDungeon.getCurrRoom()==null)return;

        ArrayList<ArrayList<AbstractMonster>> rows = new ArrayList<>();
        for(int i=0;i<4;i+=1){
            rows.add(new ArrayList<>());
        }
        for(AbstractMonster m : AbstractDungeon.getMonsters().monsters){
            int whichRow=0;
            whichRow= MultiCreature.Field.currentRow.get(m);
            rows.get(whichRow).add(m);
        }
        float offsetY=GridTile.TILE_HEIGHT*3;
        int maxcolumn=0;
        for(int i=3;i>=0;i-=1){
            for(int j=0;j<rows.get(i).size();j+=1){
                GridTile tile=new GridTile();
                tile.offsetX=GridTile.TILE_WIDTH*j;
                tile.offsetY=offsetY;
                tile.parent=this;
                tiles.add(tile);
                tile.creature=rows.get(i).get(j);
                if(j>maxcolumn)maxcolumn=j;
            }
            offsetY-=GridTile.TILE_HEIGHT;
        }
        this.centeringOffsetX=-(maxcolumn)/2f*GridTile.TILE_WIDTH;
    }
    public void update(){
        //TODO: during shieldspear fight, increase numRows to 4 even with fewer players
        if(MultiCharacter.getSubcharacters()!=null){
            numRows= MultiCharacter.getSubcharacters().size();
        }
        for(GridTile tile : tiles){
            tile.update();
        }

    }
    public void render(SpriteBatch sb){
        for(GridTile tile : tiles){
            tile.render(sb);
        }
    }

}
