package BoardGame.multicharacter;

import BoardGame.characters.BGWatcher;
import BoardGame.multicharacter.grid.GridBackground;
import BoardGame.multicharacter.grid.GridTile;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.BoneData;
import com.esotericsoftware.spine.Skeleton;
import com.evacipated.cardcrawl.modthespire.lib.*;
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

    @SpirePatch2(clz=AbstractPlayer.class,method=SpirePatch.CONSTRUCTOR,
            paramtypez={String.class, AbstractPlayer.PlayerClass.class})
    public static class PlayerConstructorPostfix {
        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance) {
            GridTile.Field.originalDrawX.set(__instance,__instance.drawX);
            GridTile.Field.originalDrawY.set(__instance,__instance.drawY);
        }
    }

    @SpirePatch2(clz= AbstractPlayer.class,method="movePosition",paramtypez={float.class,float.class})
    public static class PlayerMovePostfix {
        @SpirePostfixPatch
        public static void Foo(AbstractPlayer __instance, float x,float y) {
            GridTile.Field.originalDrawX.set(__instance,x);
            GridTile.Field.originalDrawY.set(__instance,y);
            //TODO: set dialogX/Y
        }
    }

    @SpirePatch2(clz=AbstractMonster.class,method=SpirePatch.CONSTRUCTOR,
    paramtypez={String.class, String.class, int.class, float.class, float.class, float.class, float.class, String.class, float.class, float.class, boolean.class})
    public static class MonsterConstructorPostfix {
        @SpirePostfixPatch
        public static void Foo(AbstractMonster __instance, float offsetX, float offsetY) {
            float drawX = (float)Settings.WIDTH * 0.75F + offsetX * Settings.xScale;
            float drawY = AbstractDungeon.floorY + offsetY * Settings.yScale;
            GridTile.Field.originalDrawX.set(__instance,drawX);
            GridTile.Field.originalDrawY.set(__instance,drawY);
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
    @SpirePatch2(clz= AbstractPlayer.class,method="render")
  public static class PlayerRenderBefore {
    @SpirePrefixPatch
    public static void Foo(AbstractPlayer __instance) {
        beforeRenderingCreature(__instance);
    }
  }
    @SpirePatch2(clz= AbstractPlayer.class,method="render",paramtypez={SpriteBatch.class})
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
    @SpirePatch2(clz= AbstractCreature.class,method="updateAnimations")
    public static class MonsterRenderBefore {
        @SpirePostfixPatch
        public static void Foo(AbstractCreature __instance) {
            beforeRenderingCreature(__instance);
        }
    }
    @SpirePatch2(clz= AbstractMonster.class,method="render",paramtypez={SpriteBatch.class})
    public static class MonsterRenderPostfix {
        @SpirePostfixPatch
        public static void Foo(AbstractMonster __instance, SpriteBatch sb) {
            afterRenderingCreature(__instance);
        }
    }


    public static void beforeRenderingCreature(AbstractCreature c){
        float roomDrawX=GridTile.Field.originalDrawX.get(c);
        float roomDrawY=GridTile.Field.originalDrawY.get(c);
        if (CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER) return;
        if(AbstractDungeon.getCurrRoom()==null) return;
        if(MultiCharacter.getSubcharacters()==null) return;
        int maxRows = MultiCharacter.getSubcharacters().size();
        if (maxRows <= 1) return;
        int whichRow = MultiCreature.Field.currentRow.get(c);
        float multiplier = whichRow - (maxRows - 1) / 2.0F;
        float maxmultiplier = (maxRows - 1) / 2.0F;
        multiplier /= maxmultiplier;
        if (maxRows == 2) multiplier *= 0.75F;
        float xmultiplier = -1.0F * multiplier * 0.25F + 1.0F;
        roomDrawX = roomDrawX - (Settings.WIDTH / 2);
        roomDrawX = roomDrawX * xmultiplier;
        roomDrawX = roomDrawX + (Settings.WIDTH / 2);
        float multiplier2 = (multiplier + 1.0F) / 2.0F;
        multiplier2 = (float)Math.pow(multiplier2, 0.5D);
        float max = 1.125F;
        float min = 0.25F;
        float range = max - min;
        float ymultiplier = multiplier2 * range + min;
        roomDrawY = roomDrawY * ymultiplier;

        c.drawX=roomDrawX;c.drawY=roomDrawY;

        float gridDrawX=roomDrawX,gridDrawY=roomDrawY;
        GridTile tile=GridTile.Field.gridTile.get(c);

        float tilelerptarget = GridBackground.isGridViewActive() ? 1.0f : 0.0f;
        GridTile.Field.tileLerpTarget.set(c, tilelerptarget);

        if(tile!=null){
            gridDrawX=(tile.getXPosition()+GridTile.TILE_WIDTH/2f)*Settings.scale;
            gridDrawY=(tile.getYPosition())*Settings.scale;
            if(c instanceof AbstractMonster){
                //TODO: only move monster up if monster is drawn too low (but how do we detect that??)
                //gridDrawY+=25*Settings.scale;
            }
            c.drawX=roomDrawX+(gridDrawX-roomDrawX) * GridTile.Field.tileLerpAmount.get(c);
            c.drawY=roomDrawY+(gridDrawY-roomDrawY) * GridTile.Field.tileLerpAmount.get(c);
        }


        if(tile!=null) {
            BoneData rootdata = ((Skeleton) ReflectionHacks.getPrivate(c, AbstractCreature.class, "skeleton")).getData().findBone("root");
            Bone root = ((Skeleton) ReflectionHacks.getPrivate(c, AbstractCreature.class, "skeleton")).findBone("root");
            float sx = root.getScaleX();
            float sy = root.getScaleY();
            //TODO: store original scale during constructor(?) or during animation setup
            float originalscale = 1.0f;
            float gridscale = 0.75f;
            if (c instanceof Watcher || c instanceof BGWatcher) gridscale = 0.65f;
            float lerpscale = originalscale+(gridscale-originalscale)*GridTile.Field.tileLerpAmount.get(c);
            //TODO: also scale down Watcher's staff
            rootdata.setScaleX(lerpscale);
            rootdata.setScaleY(lerpscale);
            root.setScaleX(lerpscale);
            root.setScaleY(lerpscale);
        }

        ReflectionHacks.privateMethod(AbstractCreature.class,"refreshHitboxLocation").invoke(c);
        if(tile!=null) {
            //TODO: store original hb dimensions
            //TODO: do we need to change hb_w and hb_h too, or just hb.width+height?

            //TODO NEXT: player hitboxes are disappearing instead of conforming to grid tiles
            //TODO NEXT: only change hitbox dimensions when grid view is active
            //TODO NEXT: only change healthbar size/position when grid view is active
            //TODO NEXT: allow clicking on watcher's miracles
            //TODO NEXT: watcher's vigilance is Block To Any Player

            //TODO NEXT: move debuffs up above HP bar so they fit in grid tile

            c.hb_w = tile.width*Settings.scale;
            c.hb_h = tile.height*Settings.scale;

            c.hb.width = tile.width*Settings.scale;
            c.hb.height = tile.height*Settings.scale;
            c.hb.move((tile.getXPosition()+tile.width/2f)*Settings.scale,(tile.getYPosition()+tile.height/2f)*Settings.scale);
            //TODO: store original hb dimensions?
            //note that changing healthHb here also affects where the enemy's name is drawn
            c.healthHb.width=0;
            c.healthHb.height=0;
            c.healthHb.cY=c.hb.cY-tile.width*.2f*Settings.scale;
        }

        if(c instanceof AbstractPlayer){
            //TODO: consider moving orbs much closer to player
            int i=0;
            for(AbstractOrb o : ((AbstractPlayer)c).orbs){
                o.setSlot(i,((AbstractPlayer)c).orbs.size());
                i+=1;
            }
        }

        if(c instanceof AbstractMonster && tile!=null){
            float temp=c.hb_h;
            c.hb_h=tile.height*Settings.scale;
            ((AbstractMonster)c).refreshIntentHbLocation();
            Hitbox ihb=((AbstractMonster)c).intentHb;
            ihb.move(ihb.cX,ihb.cY-96*Settings.scale);
            c.hb_h=temp;
            //TODO: store original hb dimensions?
            ((AbstractMonster) c).intentHb.width=0;
            ((AbstractMonster) c).intentHb.height=0;
        }
    }

    public static void afterRenderingCreature(AbstractCreature c){
        //c.drawX=GridTile.Field.originalDrawX.get(c);
        //c.drawY=GridTile.Field.originalDrawY.get(c);
    }



}


