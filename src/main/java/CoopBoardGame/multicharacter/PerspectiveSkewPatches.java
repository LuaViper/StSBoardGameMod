package CoopBoardGame.multicharacter;

import CoopBoardGame.characters.BGWatcher;
import CoopBoardGame.multicharacter.grid.GridBackground;
import CoopBoardGame.multicharacter.grid.GridTile;
import CoopBoardGame.multicharacter.CombatRowManager;
import CoopBoardGame.util.TogetherInSpireHelper;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.BoneData;
import com.esotericsoftware.spine.Skeleton;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

//TODO: original hitbox dimensions appear to be hardcoded to render size; might be able to auto-align monsters to grid floor with them
// (must store original hb_x,hb_y,hb_w,hb_h somewhere during constructor)

public class PerspectiveSkewPatches {

    @SpirePatch2(
        clz = AbstractPlayer.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { String.class, AbstractPlayer.PlayerClass.class }
    )
    public static class PlayerConstructorPostfix {

        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance) {
            GridTile.Field.originalDrawX.set(__instance, __instance.drawX);
            GridTile.Field.originalDrawY.set(__instance, __instance.drawY);
        }
    }

    @SpirePatch2(
        clz = AbstractPlayer.class,
        method = "movePosition",
        paramtypez = { float.class, float.class }
    )
    public static class PlayerMovePostfix {

        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance, float x, float y) {
            GridTile.Field.originalDrawX.set(__instance, x);
            GridTile.Field.originalDrawY.set(__instance, y);
            //TODO: set dialogX/Y
        }
    }

    @SpirePatch2(
        clz = AbstractMonster.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {
            String.class,
            String.class,
            int.class,
            float.class,
            float.class,
            float.class,
            float.class,
            String.class,
            float.class,
            float.class,
            boolean.class,
        }
    )
    public static class MonsterConstructorPostfix {

        @SpirePostfixPatch
        public static void Foo(AbstractMonster __instance, float offsetX, float offsetY) {
            float drawX = (float) Settings.WIDTH * 0.75F + offsetX * Settings.xScale;
            float drawY = AbstractDungeon.floorY + offsetY * Settings.yScale;
            GridTile.Field.originalDrawX.set(__instance, drawX);
            GridTile.Field.originalDrawY.set(__instance, drawY);
        }
    }

    //    @SpirePatch2(clz=AbstractCreature.class,method=SpirePatch.CONSTRUCTOR,
    //            paramtypez={String.class, AbstractPlayer.PlayerClass.class})
    //    public static class CreatureConstructorPostfix {
    //        @SpirePostfixPatch
    //        public static void Foo(AbstractPlayer __instance) {
    //            GridTile.Field.originalDrawX.set(__instance,__instance.drawX);
    //            GridTile.Field.originalDrawY.set(__instance,__instance.drawY);
    //        }
    //    }

    //TODO NEXT: Watcher's enter-stance VFX are positioned incorrectly -- move player pre-render earlier too, maybe
    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class PlayerRenderBefore {

        @SpirePrefixPatch
        public static void Foo(AbstractPlayer __instance) {
            beforeRenderingCreature(__instance);
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "render", paramtypez = { SpriteBatch.class })
    public static class PlayerRenderPostfix {

        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance, SpriteBatch sb) {
            afterRenderingCreature(__instance);
        }
    }

    //Pre-render patches have been moved from before render to after updateAnimations
    // because some update functions (updateIntentVFX) check hitbox positions
    // updateAnimations sets hitbox position
    //Note that only monsters use updateAnimations!  Players do not.
    @SpirePatch2(clz = AbstractCreature.class, method = "updateAnimations")
    public static class MonsterRenderBefore {

        @SpirePostfixPatch
        public static void Foo(AbstractCreature __instance) {
            beforeRenderingCreature(__instance);
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "render", paramtypez = { SpriteBatch.class })
    public static class MonsterRenderPostfix {

        @SpirePostfixPatch
        public static void Foo(AbstractMonster __instance, SpriteBatch sb) {
            afterRenderingCreature(__instance);
        }
    }

    public static void beforeRenderingCreature(AbstractCreature c) {
        float roomDrawX = GridTile.Field.originalDrawX.get(c);
        float roomDrawY = GridTile.Field.originalDrawY.get(c);
        if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null) return;

        // Determine the number of rows based on mode
        int maxRows;
        if (CardCrawlGame.chosenCharacter == MultiCharacter.Enums.BG_MULTICHARACTER) {
            // Single player MultiCharacter mode
            if (MultiCharacter.getSubcharacters() == null) return;
            maxRows = MultiCharacter.getSubcharacters().size();
        } else if (TogetherInSpireHelper.isMultiplayerBoardGameMode()) {
            // TogetherInSpire multiplayer with individual BG characters
            maxRows = TogetherInSpireHelper.getBoardGamePlayerCount();
        } else {
            // Standard single player mode - no row positioning needed
            return;
        }

        if (maxRows <= 1) return;
        int whichRow = MultiCreature.Field.currentRow.get(c);

        // New row-based positioning system
        float rowHeight = CombatRowManager.getRowHeight(maxRows);

        // Creatures are drawn from their feet (bottom), not center.
        // Offset downward from row center to visually center the sprite in the row.
        // Player offset: position at bottom 35% of row (feet near row bottom, sprite fills upward)
        // Enemy offset: position at bottom 30% of row (slightly higher than players since enemies vary in size)
        float playerYOffset = rowHeight * 0.35f;  // Players positioned at 35% from bottom of row
        float enemyYOffset = rowHeight * 0.30f;   // Enemies positioned at 30% from bottom of row

        // Position based on whether this is a player or monster
        if (c instanceof AbstractPlayer) {
            // Players on the left side of the screen
            roomDrawX = Settings.WIDTH * CombatRowManager.PLAYER_X_FRACTION;
            // Position Y at row bottom + offset (not row center)
            float rowBottomY = CombatRowManager.getRowBottomY(whichRow, maxRows);
            roomDrawY = (rowBottomY + playerYOffset) * Settings.scale;
        } else if (c instanceof AbstractMonster) {
            // Enemies on the right side - distribute them horizontally
            // Get the monster's index within its row to spread them out
            int monsterIndexInRow = getMonsterIndexInRow((AbstractMonster) c, whichRow);
            float xFraction = CombatRowManager.ENEMY_START_X_FRACTION +
                (monsterIndexInRow * CombatRowManager.ENEMY_SPACING_FRACTION);
            roomDrawX = Settings.WIDTH * xFraction;
            // Position Y at row bottom + offset (not row center)
            float rowBottomY = CombatRowManager.getRowBottomY(whichRow, maxRows);
            roomDrawY = (rowBottomY + enemyYOffset) * Settings.scale;
        }

        // Apply row-based position immediately (no lerp needed for row positioning)
        c.drawX = roomDrawX;
        c.drawY = roomDrawY;

        // Grid tile lerp is separate from row positioning - only affects tile snap behavior
        GridTile tile = GridTile.Field.gridTile.get(c);
        float tilelerptarget = GridBackground.isGridViewActive() ? 1.0f : 0.0f;
        GridTile.Field.tileLerpTarget.set(c, tilelerptarget);

        // If there's a tile and grid view is active, lerp towards tile position
        if (tile != null && GridBackground.isGridViewActive()) {
            float gridDrawX = (tile.getXPosition() + GridTile.TILE_WIDTH / 2f) * Settings.scale;
            float gridDrawY = (tile.getYPosition()) * Settings.scale;
            float lerpAmount = GridTile.Field.tileLerpAmount.get(c);
            c.drawX = roomDrawX + (gridDrawX - roomDrawX) * lerpAmount;
            c.drawY = roomDrawY + (gridDrawY - roomDrawY) * lerpAmount;
        }

        // Apply scaling based on number of rows
        if (tile != null) {
            BoneData rootdata = ((Skeleton) ReflectionHacks.getPrivate(
                    c,
                    AbstractCreature.class,
                    "skeleton"
                )).getData().findBone("root");
            Bone root = ((Skeleton) ReflectionHacks.getPrivate(
                    c,
                    AbstractCreature.class,
                    "skeleton"
                )).findBone("root");

            // Use dynamic scaling based on number of rows
            float originalscale = 1.0f;
            float gridscale = CombatRowManager.getScaleForRows(maxRows);
            // Watcher needs slightly smaller scale due to staff
            if (c instanceof Watcher || c instanceof BGWatcher) {
                gridscale *= 0.85f;
            }
            float lerpscale =
                originalscale + (gridscale - originalscale) * GridTile.Field.tileLerpAmount.get(c);
            rootdata.setScaleX(lerpscale);
            rootdata.setScaleY(lerpscale);
            root.setScaleX(lerpscale);
            root.setScaleY(lerpscale);
        }

        ReflectionHacks.privateMethod(AbstractCreature.class, "refreshHitboxLocation").invoke(c);
        if (tile != null) {
            //TODO: store original hb dimensions
            //TODO: do we need to change hb_w and hb_h too, or just hb.width+height?

            //TODO NEXT: player hitboxes are disappearing instead of conforming to grid tiles
            //TODO NEXT: only change hitbox dimensions when grid view is active
            //TODO NEXT: only change healthbar size/position when grid view is active
            //TODO NEXT: allow clicking on watcher's miracles
            //TODO NEXT: watcher's vigilance is Block To Any Player

            //TODO NEXT: move debuffs up above HP bar so they fit in grid tile

            c.hb_w = tile.width * Settings.scale;
            c.hb_h = tile.height * Settings.scale;

            c.hb.width = tile.width * Settings.scale;
            c.hb.height = tile.height * Settings.scale;
            c.hb.move(
                (tile.getXPosition() + tile.width / 2f) * Settings.scale,
                (tile.getYPosition() + tile.height / 2f) * Settings.scale
            );
            //TODO: store original hb dimensions?
            //note that changing healthHb here also affects where the enemy's name is drawn
            c.healthHb.width = 0;
            c.healthHb.height = 0;
            c.healthHb.cY = c.hb.cY - tile.width * .2f * Settings.scale;
        }

        if (c instanceof AbstractPlayer) {
            //TODO: consider moving orbs much closer to player
            int i = 0;
            for (AbstractOrb o : ((AbstractPlayer) c).orbs) {
                o.setSlot(i, ((AbstractPlayer) c).orbs.size());
                i += 1;
            }
        }

        if (c instanceof AbstractMonster && tile != null) {
            float temp = c.hb_h;
            c.hb_h = tile.height * Settings.scale;
            ((AbstractMonster) c).refreshIntentHbLocation();
            Hitbox ihb = ((AbstractMonster) c).intentHb;
            ihb.move(ihb.cX, ihb.cY - 96 * Settings.scale);
            c.hb_h = temp;
            //TODO: store original hb dimensions?
            ((AbstractMonster) c).intentHb.width = 0;
            ((AbstractMonster) c).intentHb.height = 0;
        }
    }

    public static void afterRenderingCreature(AbstractCreature c) {
        //c.drawX=GridTile.Field.originalDrawX.get(c);
        //c.drawY=GridTile.Field.originalDrawY.get(c);
    }

    /**
     * Gets the index of a monster within its row (for horizontal distribution).
     * @param monster The monster to find
     * @param row The row the monster is in
     * @return Index of the monster within its row (0-based)
     */
    private static int getMonsterIndexInRow(AbstractMonster monster, int row) {
        if (AbstractDungeon.getMonsters() == null) return 0;
        int index = 0;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (MultiCreature.Field.currentRow.get(m) == row) {
                if (m == monster) {
                    return index;
                }
                index++;
            }
        }
        return 0;
    }
}
