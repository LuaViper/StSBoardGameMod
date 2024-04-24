package BoardGame.patches;

import BoardGame.characters.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.scene.*;

import java.util.ArrayList;
import java.util.Iterator;

public class RedWarningFlashPatches {
    @SpirePatch2(clz = AbstractPlayer.class, method = "damage",
            paramtypez={DamageInfo.class})
    public static class Foo {
        @SpirePostfixPatch
        public static void Bar(AbstractPlayer __instance) {
            if(__instance instanceof AbstractBGPlayer) {
                if (__instance.currentHealth > 1) { //if currenthealth was exactly 1, then we've already flashed
                    if (__instance.currentHealth <= 3) {
                        AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)));
                    }
                }
            }
        }
    }

}
