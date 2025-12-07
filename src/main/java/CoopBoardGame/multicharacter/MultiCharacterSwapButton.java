package CoopBoardGame.multicharacter;

import CoopBoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

public class MultiCharacterSwapButton {

    public boolean selected = false;

    public boolean locked = false;

    private Color glowColor = new Color(1.0F, 0.8F, 0.2F, 0.0F);

    // Row assignment
    public int row = -1;

    // Remove button
    private Hitbox removeButtonHb;
    private static final float REMOVE_BTN_SIZE = 24f * Settings.scale;
    private Color removeButtonColor = new Color(1.0F, 0.3F, 0.3F, 0.8F);

    public MultiCharacterSwapButton(String optionName, AbstractPlayer c, Texture texture) {
        this.name = optionName;
        this.hb = new Hitbox(HB_W, HB_W);
        this.buttonImg = texture;
        this.c = c;

        // Initialize remove button hitbox (positioned at top-right of portrait)
        this.removeButtonHb = new Hitbox(REMOVE_BTN_SIZE, REMOVE_BTN_SIZE);
    }

    public MultiCharacterSwapButton(String optionName, AbstractPlayer c, String buttonUrl) {
        this(optionName, c, TextureLoader.getTexture(buttonUrl));
    }

    public void update() {
        updateHitbox();
    }

    private void updateHitbox() {
        this.hb.update();
        this.hb.cX = this.hb.x + this.hb.width / 2.0F;
        this.hb.cY = this.hb.y + this.hb.height / 2.0F;

        // Update remove button position (top-right corner of portrait)
        this.removeButtonHb.x = this.hb.x + this.hb.width - REMOVE_BTN_SIZE - 4f * Settings.scale;
        this.removeButtonHb.y = this.hb.y + this.hb.height - REMOVE_BTN_SIZE - 4f * Settings.scale;
        this.removeButtonHb.update();

        // Check for remove button click (only if not dragging)
        if (
            !CharacterSelectDragManager.isDragging() &&
            !CharacterSelectDragManager.shouldBlockClicks()
        ) {
            if (this.removeButtonHb.hovered && InputHelper.justClickedLeft) {
                // Remove this character - handled by MultiCharacterRowBoxes
                // We'll signal this via a callback approach
                if (this.row != -1) {
                    // Find the row boxes and remove
                    com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu.proceedButton.hide();
                    MultiCharacterRowBoxes rowBoxes =
                        (MultiCharacterRowBoxes) CoopBoardGame.ui.OverlayMenuPatches.OverlayMenuExtraInterface.multiCharacterRowBoxes.get(
                            com.megacrit.cardcrawl.dungeons.AbstractDungeon.overlayMenu
                        );
                    rowBoxes.removeCharacterFromRow(this.row);
                    return;
                }
            }
        }

        // Check for drag start on portrait (only if not clicking remove button)
        if (!this.removeButtonHb.hovered && this.row != -1) {
            CharacterSelectDragManager.checkDragStart(this, this.row);
        }
    }

    public void render(SpriteBatch sb) {
        renderOptionButton(sb);
        renderRemoveButton(sb);
        this.hb.render(sb);
    }

    private void renderOptionButton(SpriteBatch sb) {
        if (this.selected) {
            this.glowColor.a =
                0.25F +
                (MathUtils.cosDeg((float) ((System.currentTimeMillis() / 4L) % 360L)) + 1.25F) /
                3.5F;
            sb.setColor(this.glowColor);
        } else {
            sb.setColor(BLACK_OUTLINE_COLOR);
        }
        sb.draw(
            ImageMaster.CHAR_OPT_HIGHLIGHT,
            this.hb.cX - HB_W / 2.0F,
            this.hb.cY - HB_W / 2.0F,
            HB_W / 2.0F,
            HB_W / 2.0F,
            HB_W,
            HB_W,
            1.0F,
            1.0F,
            0.0F,
            0,
            0,
            64,
            64,
            false,
            false
        );
        if (!this.selected && !this.hb.hovered) {
            sb.setColor(Color.LIGHT_GRAY);
        } else {
            sb.setColor(Color.WHITE);
        }
        sb.draw(
            this.buttonImg,
            this.hb.cX - HB_W / 2.0F,
            this.hb.cY - HB_W / 2.0F,
            HB_W / 2.0F,
            HB_W / 2.0F,
            HB_W,
            HB_W,
            1.0F,
            1.0F,
            0.0F,
            0,
            0,
            64,
            64,
            false,
            false
        );
    }

    /**
     * Renders the small X button at the top-right corner of the portrait.
     */
    private void renderRemoveButton(SpriteBatch sb) {
        // Background circle
        if (this.removeButtonHb.hovered) {
            sb.setColor(1.0F, 0.2F, 0.2F, 1.0F);
        } else {
            sb.setColor(removeButtonColor);
        }

        float size = REMOVE_BTN_SIZE;
        float cx = this.removeButtonHb.x + size / 2f;
        float cy = this.removeButtonHb.y + size / 2f;

        // Draw a simple circle background
        sb.draw(
            ImageMaster.WHITE_SQUARE_IMG,
            this.removeButtonHb.x,
            this.removeButtonHb.y,
            size,
            size
        );

        // Draw X using two lines (simplified as text for now)
        sb.setColor(Color.WHITE);
        com.megacrit.cardcrawl.helpers.FontHelper.renderFontCentered(
            sb,
            com.megacrit.cardcrawl.helpers.FontHelper.buttonLabelFont,
            "X",
            cx,
            cy,
            Color.WHITE
        );
    }

    /**
     * Gets the button image texture for drag rendering.
     */
    public Texture getButtonImg() {
        return this.buttonImg;
    }

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(
        "CharacterOption"
    );

    public static final String[] TEXT = uiStrings.TEXT;

    public static final float HB_W = 128.0F * Settings.scale;

    private static final Color BLACK_OUTLINE_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.5F);

    private Texture buttonImg;

    public AbstractPlayer c;

    public Hitbox hb;

    public static final String ASSETS_DIR = "images/ui/charSelect/";

    public String name;
}
