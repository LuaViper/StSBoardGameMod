package BoardGame.multicharacter.patches;

import BoardGame.multicharacter.BGMultiCharacter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class OverlayMenuPatches {
    @SpirePatch2(clz = OverlayMenu.class, method = "update")
    public static class OverlayMenuUpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(){
            if(AbstractDungeon.player instanceof BGMultiCharacter){
                for(AbstractPlayer p : BGMultiCharacter.getSubcharacters()){
                    p.hand.update();
                }
            }
        }
    }

}
