package BoardGame.multicharacter;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.multicharacter.grid.GridTile;
import BoardGame.ui.BGGameTips;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
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
import com.megacrit.cardcrawl.helpers.GameTips;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.Collections;

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
            GridTile.Field.originalDrawX.set(__instance,offsetX);
            GridTile.Field.originalDrawY.set(__instance,offsetY);
        }
    }




  @SpirePatch2(clz= AbstractPlayer.class,method="render",paramtypez={SpriteBatch.class})
  public static class PlayerRenderPrefix {
    @SpirePrefixPatch
    public static void Foo(AbstractPlayer __instance, SpriteBatch sb) {
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
    @SpirePatch2(clz= AbstractMonster.class,method="render",paramtypez={SpriteBatch.class})
    public static class MonsterRenderPrefix {
        @SpirePrefixPatch
        public static void Foo(AbstractMonster __instance, SpriteBatch sb) {
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
        if(tile!=null){
            gridDrawX=(tile.getXPosition()+GridTile.TILE_WIDTH/2f)*Settings.scale;
            gridDrawY=(tile.getYPosition())*Settings.scale;
            GridTile.Field.tileLerpTarget.set(c, 1.0f);  //!!!
            c.drawX=roomDrawX+(gridDrawX-roomDrawX) * GridTile.Field.tileLerpAmount.get(c);
            c.drawY=roomDrawY+(gridDrawY-roomDrawY) * GridTile.Field.tileLerpAmount.get(c);
        }


        BoneData rootdata = ((Skeleton)ReflectionHacks.getPrivate(c,AbstractCreature.class,"skeleton")).getData().findBone("root");
        Bone root = ((Skeleton)ReflectionHacks.getPrivate(c,AbstractCreature.class,"skeleton")).findBone("root");
        float sx=root.getScaleX();
        float sy=root.getScaleY();
        //TODO: store original scale
        float scale=0.75f;
        if(c instanceof Watcher)scale=0.65f;
        rootdata.setScaleX(scale);
        rootdata.setScaleY(scale);
        root.setScaleX(scale);
        root.setScaleY(scale);

        ReflectionHacks.privateMethod(AbstractCreature.class,"refreshHitboxLocation").invoke(c);
        if(tile!=null) {
            //TODO: store original hb dimensions? (or is that what hb_w is for??)
            //TODO: do we need to change hb_w and hb_h too, or just hb.width+height?
            c.hb_w = tile.width*Settings.scale;
            c.hb_h = tile.height*Settings.scale;    //!!!

            c.hb.width = tile.width*Settings.scale;
            c.hb.height = tile.height*Settings.scale;
            c.hb.move((tile.getXPosition()+tile.width/2f)*Settings.scale,(tile.getYPosition()+tile.height/2f)*Settings.scale);
            //TODO: store original hb dimensions?
            c.healthHb.width=0;
            c.healthHb.height=0;
        }

        if(c instanceof AbstractPlayer){
            //TODO: consider moving orbs much closer to player
            int i=0;
            for(AbstractOrb o : ((AbstractPlayer)c).orbs){
                o.setSlot(i,((AbstractPlayer)c).orbs.size());
                i+=1;
            }
        }

        if(c instanceof AbstractMonster){
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


