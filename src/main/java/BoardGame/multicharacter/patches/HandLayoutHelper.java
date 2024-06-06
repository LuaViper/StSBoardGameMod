package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.MultiCharacterSelectScreen;
import BoardGame.multicharacter.MultiCreature;
import BoardGame.multicharacter.grid.GridBackground;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import javax.naming.Context;
import java.util.ArrayList;

import static com.badlogic.gdx.graphics.GL20.*;

public class HandLayoutHelper {

    //TODO: if a character's hand is empty, scroll past it to the next hand (unless EVERYONE'S hand is empty)

    //public final float BASE_SPACING = 35.0f;  //this works, provided no cards are playable
    public final float BASE_SPACING = 40.0f;    //this allows extra space for card glow
    public float SPACING;
    public int currentHand=-1;
    public float[] hand_offset_y = {0f,0f,0f,0f};
    public float[] hand_target_y = {0f,0f,0f,0f};
    public HandLayoutHelper(){

    }
    public void update(){
        SPACING = BASE_SPACING * Settings.scale;
        if(currentHand>=0) {
            if (!AbstractDungeon.isScreenUp) {
                if (!MultiCharacter.getSubcharacters().isEmpty()) {
                    for (int i = 0; i < MultiCharacter.getSubcharacters().size(); i += 1) {
                        hand_offset_y[i] = MathHelper.uiLerpSnap(hand_offset_y[i], hand_target_y[i]);
                    }
                    //can't scroll hands while dragging a card (inc. InputHelper.getCardSelectedByHotkey?)
                    AbstractPlayer currentCharacter = MultiCharacter.getSubcharacters().get(currentHand);
                    if (true || !currentCharacter.isDraggingCard){
                    //&& InputHelper.getCardSelectedByHotkey(currentCharacter.hand)==null) {
                        if (InputHelper.scrolledDown) {
                            int i = currentHand;
                            i += 1;
                            if (i >= MultiCharacter.getSubcharacters().size()) i = 0;
                            changeHand(i, 1);
                        }
                        if (InputHelper.scrolledUp) {
                            int i = currentHand;
                            i -= 1;
                            if (i < 0) i = MultiCharacter.getSubcharacters().size() - 1;
                            changeHand(i, -1);
                        }
                    }
                }
            }
        }
    }

    public void changeHand(int index,int change){
        if(change==1){
            hand_offset_y[currentHand]=0+SPACING* MultiCharacter.getSubcharacters().size();
        }
        changeHand(index);
        if(change==-1){
            hand_offset_y[currentHand]=0-SPACING*1;
        }
    }

    public void changeHand(int index){
        SPACING = BASE_SPACING * Settings.scale;
        if(currentHand>=0) MultiCharacter.getSubcharacters().get(currentHand).releaseCard();
        currentHand=index;
        int i = index;
        for(int j = 0; j< MultiCharacter.getSubcharacters().size(); j+=1) {
            hand_target_y[i]=0+SPACING*j;
            i+=1;if(i>= MultiCharacter.getSubcharacters().size())i=0;
        }
        AbstractPlayer newCurrentPlayer=MultiCharacter.getSubcharacters().get(currentHand);
        ContextPatches.pushPlayerContext(newCurrentPlayer);
        for(AbstractCard c : newCurrentPlayer.hand.group){
            c.applyPowers();
        }
        ContextPatches.popPlayerContext();
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderCard",
            paramtypez = {SpriteBatch.class,boolean.class,boolean.class})
    public static class RenderCardPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard __instance, SpriteBatch sb, boolean hovered, boolean selected) {
            if(CardCrawlGame.chosenCharacter!= MultiCharacter.Enums.BG_MULTICHARACTER)return;
            if(CardPatches.Field.owner.get(__instance)==null)return;
            //if (AbstractDungeon.player != null && AbstractDungeon.player.isDraggingCard && __instance == AbstractDungeon.player.hoveredCard) {}
            int whichRow= MultiCreature.Field.currentRow.get(CardPatches.Field.owner.get(__instance));
            __instance.current_y+= MultiCharacter.handLayoutHelper.hand_offset_y[whichRow];
        }
    }
    @SpirePatch2(clz = AbstractCard.class, method = "renderCard",
            paramtypez = {SpriteBatch.class,boolean.class, boolean.class})
    public static class RenderCardPatch2 {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, SpriteBatch sb, boolean hovered, boolean selected) {
            if(CardCrawlGame.chosenCharacter != MultiCharacter.Enums.BG_MULTICHARACTER) return;
            if(CardPatches.Field.owner.get(__instance)==null)return;
            //if (AbstractDungeon.player != null && AbstractDungeon.player.isDraggingCard && __instance == AbstractDungeon.player.hoveredCard) {}
            int whichRow= MultiCreature.Field.currentRow.get(CardPatches.Field.owner.get(__instance));
            __instance.current_y-= MultiCharacter.handLayoutHelper.hand_offset_y[whichRow];
        }
    }

    @SpirePatch2(clz = CardGroup.class, method = "refreshHandLayout")
    public static class RefreshHandLayoutPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance) {
            if(CardCrawlGame.chosenCharacter != MultiCharacter.Enums.BG_MULTICHARACTER) return;
            if(!(AbstractDungeon.player instanceof MultiCharacter)) return;
            for(AbstractPlayer p : MultiCharacter.getSubcharacters()){
                if(p instanceof MultiCharacter){
                    //TODO: complain very loudly
                    continue;
                }
                ContextPatches.pushPlayerContext(p);
                p.hand.refreshHandLayout();
                ContextPatches.popPlayerContext();
            }

        }
    }

    public static boolean cardIsAttachedToSoul(AbstractCard __instance){
        if(AbstractDungeon.getCurrRoom()!=null && AbstractDungeon.getCurrRoom().souls!=null){
            ArrayList<Soul> souls = ReflectionHacks.getPrivate(AbstractDungeon.getCurrRoom().souls, SoulGroup.class,"souls");
            for(Soul s : souls){
                if(s.card!=null && !s.isDone) {
                    boolean breakpoint=true;
                    if (s.card == __instance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean shouldCardBeGrayedOut(AbstractCard __instance){
        if(CardCrawlGame.chosenCharacter!=MultiCharacter.Enums.BG_MULTICHARACTER)
            return false;
        if(cardIsAttachedToSoul(__instance))
            return false;
        //TODO NEXT: this is currently returning true for vanilla/singleplayer modes.
        if (GridBackground.isGridViewActive()) {
            AbstractPlayer owner = CardPatches.Field.owner.get(__instance);
            if (owner != null) {
                int whichRow = MultiCreature.Field.currentRow.get(CardPatches.Field.owner.get(__instance));
                if (whichRow != MultiCharacter.handLayoutHelper.currentHand) {
                    return true;

                }
            }
        }
        return false;
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderGlow")
    public static class OnlyGlowCurrentHand {
        @SpirePrefixPatch
        public static SpireReturn<Void> Foo(AbstractCard __instance, SpriteBatch sb) {
            if(shouldCardBeGrayedOut(__instance)) {
                //TODO: but DO render glow if card is Glowing Gold, maybe
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    public static void SetGrayedOutColor(Color[] color){
        color[0] = color[0].cpy();
        color[0].r *= 0.75f;
        color[0].g *= 0.75f;
        color[0].b *= 0.75f;
        //color[0].a *= 0.75f;
    }

    @SpirePatch2(clz=AbstractCard.class,method="renderHelper",
            paramtypez={SpriteBatch.class,Color.class, TextureAtlas.AtlasRegion.class,float.class,float.class})
    public static class RenderHelperPatch1{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance, @ByRef Color[] color){
            if(shouldCardBeGrayedOut(__instance)) {
                SetGrayedOutColor(color);
            }
        }
    }
    @SpirePatch2(clz=AbstractCard.class,method="renderHelper",
            paramtypez={SpriteBatch.class,Color.class, TextureAtlas.AtlasRegion.class,float.class,float.class,float.class})
    public static class RenderHelperPatch2{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance, @ByRef Color[] color){
            if(shouldCardBeGrayedOut(__instance)) {
                SetGrayedOutColor(color);
            }
        }
    }

    @SpirePatch2(clz=AbstractCard.class,method="renderHelper",
            paramtypez={SpriteBatch.class,Color.class, Texture.class,float.class,float.class})
    public static class RenderHelperPatch3{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance, @ByRef Color[] color){
            if(shouldCardBeGrayedOut(__instance)) {
                SetGrayedOutColor(color);
            }
        }
    }

    @SpirePatch2(clz=AbstractCard.class,method="renderHelper",
            paramtypez={SpriteBatch.class,Color.class, Texture.class,float.class,float.class,float.class})
    public static class RenderHelperPatch4{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance, @ByRef Color[] color){
            if(shouldCardBeGrayedOut(__instance)) {
                SetGrayedOutColor(color);
            }
        }
    }

    public static void setPortraitColor(AbstractCard __instance){
        Color originalColor=ReflectionHacks.getPrivate(__instance,AbstractCard.class,"renderColor");
        CardPatches.Field.originalRenderColor.set(__instance,originalColor);
        if(shouldCardBeGrayedOut(__instance)) {
            Color[] c={originalColor};
            SetGrayedOutColor(c);
            ReflectionHacks.setPrivate(__instance,AbstractCard.class,"renderColor",c[0]);
        }
    }

    public static void unsetPortraitColor(AbstractCard __instance){
        Color originalColor=CardPatches.Field.originalRenderColor.get(__instance);
        if(originalColor!=null) {
            ReflectionHacks.setPrivate(__instance, AbstractCard.class, "renderColor", originalColor);
        }
    }

    @SpirePatch2(clz=AbstractCard.class,method="renderPortrait")
    public static class PortraitPatch1{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance){
            setPortraitColor(__instance);
        }
        @SpirePostfixPatch
        public static void Foo2(AbstractCard __instance){
            unsetPortraitColor(__instance);
        }
    }

    @SpirePatch2(clz=AbstractCard.class,method="renderJokePortrait")
    public static class PortraitPatch2{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance){
            setPortraitColor(__instance);
        }
        @SpirePostfixPatch
        public static void Foo2(AbstractCard __instance){
            unsetPortraitColor(__instance);
        }
    }

    @SpirePatch2(clz=AbstractCard.class,method="renderTitle")
    public static class TitlePatch{
        @SpirePrefixPatch
        public static void Foo(AbstractCard __instance){
            setPortraitColor(__instance);
        }
        @SpirePostfixPatch
        public static void Foo2(AbstractCard __instance){
            unsetPortraitColor(__instance);
        }
    }

    //TODO: renderDescription patch will take a bit more effort -- need to set/unset, at minimum, __instance.textColor and __instance.goldColor

}
