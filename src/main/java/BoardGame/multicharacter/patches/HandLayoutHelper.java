package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.MultiCharacter;
import BoardGame.multicharacter.MultiCreature;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

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

}
