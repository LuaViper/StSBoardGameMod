package BoardGame.multicharacter.patches;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.stats.CharStat;

public class AscensionPatches {

    @SpirePatch2(clz = CharStat.class, method = "incrementAscension")
    public static class UnlockNextAscensionPatch {
        @SpirePostfixPatch
        public static void Postfix(){
            if(AbstractDungeon.player instanceof AbstractBGPlayer &&
                !(AbstractDungeon.player instanceof MultiCharacter)) {
                AbstractPlayer temp = AbstractDungeon.player;
                AbstractDungeon.player=new MultiCharacter("Temp BGMultiCharacter", MultiCharacter.Enums.BG_MULTICHARACTER);
                AbstractDungeon.player.getCharStat().incrementAscension();
                AbstractDungeon.player=temp;
            }
        }
    }

}
