package CoopBoardGame.patches;

import CoopBoardGame.characters.BGDefect;
import CoopBoardGame.characters.BGIronclad;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.characters.BGWatcher;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.*;
import java.util.ArrayList;

public class VictoryVFXPatches {

    @SpirePatch2(clz = VictoryScreen.class, method = "updateVfx", paramtypez = {})
    public static class Foo {

        @SpirePostfixPatch
        public static void Bar(
            VictoryScreen __instance,
            @ByRef ArrayList<AbstractGameEffect>[] ___effect,
            @ByRef float[] ___effectTimer,
            @ByRef float[] ___effectTimer2
        ) {
            if (AbstractDungeon.player.chosenClass == BGIronclad.Enums.BG_IRONCLAD) {
                ___effectTimer[0] -= Gdx.graphics.getDeltaTime();
                if (___effectTimer[0] < 0.0F) {
                    ___effect[0].add(new SlowFireParticleEffect());
                    ___effect[0].add(new SlowFireParticleEffect());
                    ___effect[0].add(new IroncladVictoryFlameEffect());
                    ___effect[0].add(new IroncladVictoryFlameEffect());
                    ___effect[0].add(new IroncladVictoryFlameEffect());
                    ___effectTimer[0] = 0.1F;
                }
            } else if (AbstractDungeon.player.chosenClass == BGSilent.Enums.BG_SILENT) {
                ___effectTimer[0] -= Gdx.graphics.getDeltaTime();
                if (___effectTimer[0] < 0.0F) {
                    if (___effect[0].size() < 100) {
                        ___effect[0].add(new SilentVictoryStarEffect());
                        ___effect[0].add(new SilentVictoryStarEffect());
                        ___effect[0].add(new SilentVictoryStarEffect());
                        ___effect[0].add(new SilentVictoryStarEffect());
                    }

                    ___effectTimer[0] = 0.1F;
                }

                ___effectTimer2[0] += Gdx.graphics.getDeltaTime();
                if (___effectTimer2[0] > 1.0F) {
                    ___effectTimer2[0] = 1.0F;
                }
            } else if (AbstractDungeon.player.chosenClass == BGDefect.Enums.BG_DEFECT) {
                boolean foundEyeVfx = false;

                for (AbstractGameEffect effect : ___effect[0]) {
                    if (effect instanceof DefectVictoryEyesEffect) {
                        foundEyeVfx = true;
                        break;
                    }
                }

                if (!foundEyeVfx) {
                    ___effect[0].add(new DefectVictoryEyesEffect());
                }

                if (___effect[0].size() < 15) {
                    ___effect[0].add(new DefectVictoryNumberEffect());
                }
            } else if (AbstractDungeon.player.chosenClass == BGWatcher.Enums.BG_WATCHER) {
                boolean createdEffect = false;

                for (AbstractGameEffect effect : ___effect[0]) {
                    if (effect instanceof WatcherVictoryEffect) {
                        createdEffect = true;
                        break;
                    }
                }

                if (!createdEffect) {
                    ___effect[0].add(new WatcherVictoryEffect());
                }
            }
        }
    }
}
