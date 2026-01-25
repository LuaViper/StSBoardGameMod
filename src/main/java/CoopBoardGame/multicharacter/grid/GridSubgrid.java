package CoopBoardGame.multicharacter.grid;

import CoopBoardGame.multicharacter.CombatRowManager;
import CoopBoardGame.multicharacter.MultiCharacter;
import CoopBoardGame.multicharacter.MultiCreature;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.ArrayList;

public class GridSubgrid {

    public ArrayList<GridTile> tiles = new ArrayList<>();
    //offsetX and offsetY are lower left corner of the player subgrid.
    //for the enemy subgrid, it's the lower left corner of the bottom center tile.
    public float screenOffsetX;
    public float centeringOffsetX = 0;
    public float offsetY;

    public int numRows = 0;

    public GridSubgrid() {
        int numRows = MultiCharacter.getSubcharacters().size();
        float rowHeight = CombatRowManager.getRowHeight(numRows);

        for (int i = 0; i < numRows; i += 1) {
            GridTile tile = new GridTile();
            tile.offsetX = 0;
            // Use dynamic row height instead of fixed TILE_HEIGHT
            tile.offsetY = i * rowHeight;
            tile.parent = this;
            tiles.add(tile);

            if (i < MultiCharacter.getSubcharacters().size()) {
                tile.creature = MultiCharacter.getSubcharacters().get(i);
                GridTile.Field.gridTile.set(tile.creature, tile);
            }
        }
    }

    public void createTilesForEnemies() {
        tiles.clear();
        if (
            AbstractDungeon.getCurrMapNode() == null || AbstractDungeon.getCurrRoom() == null
        ) return;

        int numRows = MultiCharacter.getSubcharacters().size();
        float rowHeight = CombatRowManager.getRowHeight(numRows);

        ArrayList<ArrayList<AbstractMonster>> rows = new ArrayList<>();
        for (int i = 0; i < 4; i += 1) {
            rows.add(new ArrayList<>());
        }
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            int whichRow = MultiCreature.Field.currentRow.get(m);
            if (whichRow < rows.size()) {
                rows.get(whichRow).add(m);
            }
        }

        // Calculate tile width based on scale (use scaled tile size)
        float scaledTileWidth = GridTile.TILE_WIDTH * CombatRowManager.getScaleForRows(numRows);

        int maxcolumn = 0;
        // Create tiles for each row (bottom to top, row 0 at bottom)
        for (int i = 0; i < numRows; i++) {
            float tileOffsetY = i * rowHeight;
            for (int j = 0; j < rows.get(i).size(); j++) {
                GridTile tile = new GridTile();
                tile.offsetX = scaledTileWidth * j;
                tile.offsetY = tileOffsetY;
                tile.parent = this;
                tiles.add(tile);
                tile.creature = rows.get(i).get(j);
                if (j > maxcolumn) maxcolumn = j;
            }
        }
        this.centeringOffsetX = (-(maxcolumn) / 2f) * scaledTileWidth;
    }

    public void update() {
        //TODO: during shieldspear fight, increase numRows to 4 even with fewer players
        if (MultiCharacter.getSubcharacters() != null) {
            numRows = MultiCharacter.getSubcharacters().size();
        }
        for (GridTile tile : tiles) {
            tile.update();
        }
    }

    public void render(SpriteBatch sb) {
        for (GridTile tile : tiles) {
            tile.render(sb);
        }
    }
}
