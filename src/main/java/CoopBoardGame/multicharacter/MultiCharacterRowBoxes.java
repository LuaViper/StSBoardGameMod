package CoopBoardGame.multicharacter;

import CoopBoardGame.characters.AbstractBGPlayer;
import CoopBoardGame.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import java.util.ArrayList;

public class MultiCharacterRowBoxes {

    private final Texture img = TextureLoader.getTexture(
        "CoopBoardGameResources/images/charSelect/MultiCharacterSelectGrid.png"
    );

    private final float START_X = -800.0F * Settings.xScale;

    private final float DEST_X = 160.0F * Settings.scale;

    private float targetX;

    private float posX;

    private float posY;

    public ArrayList<MultiCharacterSwapButton> swapButtons = new ArrayList<>();

    // Row selection and assignment
    private CharacterRowAssignment rowAssignment = new CharacterRowAssignment();
    private ArrayList<Hitbox> rowHitboxes = new ArrayList<>();
    private Color selectedRowGlowColor = new Color(0.2F, 0.8F, 1.0F, 0.0F);

    // Row slot dimensions (matching the grid image)
    private static final float ROW_WIDTH = 64.0F * Settings.scale * 2.0F;
    private static final float ROW_HEIGHT = 80.0F * Settings.scale * 2.0F;

    public MultiCharacterRowBoxes() {
        this.posX = this.targetX = this.START_X;
        this.posY = (Settings.HEIGHT / 2) - 240.0F;

        // Initialize 4 row hitboxes
        for (int i = 0; i < CharacterRowAssignment.MAX_ROWS; i++) {
            Hitbox hb = new Hitbox(ROW_WIDTH, ROW_HEIGHT);
            rowHitboxes.add(hb);
        }
    }

    public void update() {
        this.posX = MathHelper.uiLerpSnap(this.posX, this.targetX);

        // Update row hitbox positions
        for (int i = 0; i < rowHitboxes.size(); i++) {
            Hitbox hb = rowHitboxes.get(i);
            hb.x = this.posX + 8.0F * Settings.scale * 2.0F;
            hb.y = this.posY + 8.0F + (80 * i) * Settings.scale * 2.0F;
            hb.update();
        }

        // Global drag state update
        CharacterSelectDragManager.globalUpdate();

        // Handle row click to set selected row (only when not dragging)
        if (
            !CharacterSelectDragManager.isDragging() &&
            !CharacterSelectDragManager.shouldBlockClicks()
        ) {
            for (int i = 0; i < rowHitboxes.size(); i++) {
                Hitbox hb = rowHitboxes.get(i);
                if (hb.hovered && InputHelper.justClickedLeft) {
                    // Only select row if it's empty or if clicking to select for potential removal
                    rowAssignment.setSelectedRow(i);
                    CardCrawlGame.sound.playA("UI_CLICK_1", -0.4F);
                }
            }
        }

        // Update swap buttons
        for (MultiCharacterSwapButton b : this.swapButtons) {
            b.update();
            b.hb.x = this.posX + 8.0F * Settings.scale * 2.0F;
        }

        // Update drag state
        if (CharacterSelectDragManager.isDragging()) {
            CharacterSelectDragManager.updateDrag(rowHitboxes);

            // Handle mouse release to end drag
            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                CharacterSelectDragManager.endDrag(this);
            }
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
        sb.draw(
            this.img,
            this.posX,
            this.posY,
            0.0F,
            0.0F,
            80.0F,
            320.0F,
            Settings.scale * 2.0F,
            Settings.scale * 2.0F,
            0.0F,
            0,
            0,
            80,
            320,
            false,
            false
        );

        // Render selected row highlight
        renderSelectedRowHighlight(sb);

        // Render hover highlight during drag
        CharacterSelectDragManager.renderHoverHighlight(sb, rowHitboxes);

        // Render swap buttons (skip the dragged one)
        for (MultiCharacterSwapButton b : this.swapButtons) {
            if (
                CharacterSelectDragManager.isDragging() &&
                b == CharacterSelectDragManager.draggedButton
            ) {
                continue; // Skip rendering the dragged button in normal pass
            }
            b.render(sb);
        }

        // Render dragged button on top
        CharacterSelectDragManager.renderDraggedButton(sb);
    }

    /**
     * Renders a pulsing glow highlight on the selected row.
     */
    private void renderSelectedRowHighlight(SpriteBatch sb) {
        int selectedRow = rowAssignment.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= rowHitboxes.size()) return;

        Hitbox hb = rowHitboxes.get(selectedRow);

        // Pulsing alpha
        selectedRowGlowColor.a =
            0.15F +
            (MathUtils.cosDeg((float) ((System.currentTimeMillis() / 4L) % 360L)) + 1.25F) / 6.0F;

        sb.setColor(selectedRowGlowColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, hb.x, hb.y, hb.width, hb.height);
        sb.setColor(Color.WHITE);
    }

    /**
     * Assigns a character to the currently selected row.
     * If the row is already occupied, replaces the existing character.
     */
    public void assignCharacterToSelectedRow(AbstractPlayer character) {
        int selectedRow = rowAssignment.getSelectedRow();

        // If this character is already assigned somewhere, remove it first
        int existingRow = rowAssignment.getRowForCharacter(character);
        if (existingRow != -1) {
            rowAssignment.removeCharacterFromRow(existingRow);
        }

        // Assign to selected row
        rowAssignment.assignCharacterToRow(character, selectedRow);

        // Auto-advance to next empty row
        rowAssignment.autoAdvanceSelectedRow();

        // Rebuild buttons and sync subcharacters
        rebuildSwapButtons();
        syncSubcharactersFromRowAssignment();
    }

    /**
     * Removes the character from a specific row.
     */
    public void removeCharacterFromRow(int row) {
        AbstractPlayer removed = rowAssignment.removeCharacterFromRow(row);
        if (removed != null) {
            CardCrawlGame.sound.play("CARD_BURN");
        }

        // Rebuild buttons and sync subcharacters
        rebuildSwapButtons();
        syncSubcharactersFromRowAssignment();
    }

    /**
     * Rebuilds the swap buttons based on current row assignments.
     */
    public void rebuildSwapButtons() {
        this.swapButtons.clear();

        for (int row = 0; row < CharacterRowAssignment.MAX_ROWS; row++) {
            AbstractPlayer character = rowAssignment.getCharacterAtRow(row);
            if (character == null) continue;

            MultiCharacterSwapButton b = new MultiCharacterSwapButton(
                character.name,
                character,
                ((AbstractBGPlayer) character).getMultiSwapButtonUrl()
            );
            b.row = row;

            // Position the button in the row slot
            Hitbox hb = b.hb;
            hb.x = this.posX + 8.0F * Settings.scale * 2.0F;
            hb.y = this.posY + 8.0F + (80 * row) * Settings.scale * 2.0F;

            this.swapButtons.add(b);
        }

        // Position characters in the world
        for (int row = 0; row < CharacterRowAssignment.MAX_ROWS; row++) {
            AbstractPlayer character = rowAssignment.getCharacterAtRow(row);
            if (character != null) {
                MultiCreature.Field.currentRow.set(character, row);
                character.movePosition(Settings.WIDTH * 0.25F, AbstractDungeon.floorY);
            }
        }
    }

    /**
     * Syncs the MultiCharacter's subcharacters list from the row assignment.
     * Characters are ordered bottom to top (row 0 first).
     */
    private void syncSubcharactersFromRowAssignment() {
        MultiCharacter mc = (MultiCharacter) AbstractDungeon.player;
        mc.subcharacters.clear();
        mc.subcharacters.addAll(rowAssignment.getAssignedCharactersBottomToTop());
    }

    /**
     * Gets the row assignment data structure.
     */
    public CharacterRowAssignment getRowAssignment() {
        return rowAssignment;
    }

    /**
     * Gets the row hitboxes for drag detection.
     */
    public ArrayList<Hitbox> getRowHitboxes() {
        return rowHitboxes;
    }

    /**
     * Legacy method - redirects to rebuildSwapButtons.
     * Kept for compatibility with existing code.
     */
    public void remakeSwapButtonsAndPositionCharacters() {
        // Rebuild assignment from current subcharacters (for initial state)
        MultiCharacter mc = (MultiCharacter) AbstractDungeon.player;
        rowAssignment.clear();
        for (int i = 0; i < mc.subcharacters.size() && i < CharacterRowAssignment.MAX_ROWS; i++) {
            AbstractPlayer s = mc.subcharacters.get(i);
            rowAssignment.assignCharacterToRow(s, i);
        }
        rebuildSwapButtons();
    }
}
