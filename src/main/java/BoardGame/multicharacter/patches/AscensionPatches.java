package BoardGame.multicharacter.patches;

import BoardGame.characters.AbstractBGPlayer;
import BoardGame.multicharacter.BGMultiCharacter;
import BoardGame.relics.BGPeacePipe;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireTokeEffect;

public class AscensionPatches {

    @SpirePatch2(clz = CharStat.class, method = "incrementAscension")
    public static class UnlockNextAscensionPatch {
        @SpirePostfixPatch
        public static void Postfix(){
            if(AbstractDungeon.player instanceof AbstractBGPlayer &&
                !(AbstractDungeon.player instanceof BGMultiCharacter)) {
                AbstractPlayer temp = AbstractDungeon.player;
                AbstractDungeon.player=new BGMultiCharacter("Temp BGMultiCharacter",BGMultiCharacter.Enums.BG_MULTICHARACTER);
                AbstractDungeon.player.getCharStat().incrementAscension();
                AbstractDungeon.player=temp;
            }
        }
    }

}
