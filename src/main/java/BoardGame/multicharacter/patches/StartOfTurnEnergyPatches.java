package BoardGame.multicharacter.patches;

import BoardGame.dungeons.BGExordium;
import BoardGame.multicharacter.MultiCharacter;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.PlayerTurnEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Block reset is handled in MultiCharacter.loseBlock
public class StartOfTurnEnergyPatches {
    @SpirePatch2(clz = PlayerTurnEffect.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez={})
    public static class Foo {
        @SpirePostfixPatch
        public static void Bar() {
            if(AbstractDungeon.player instanceof MultiCharacter) {
                for (AbstractPlayer c : MultiCharacter.getSubcharacters()) {
                    ContextPatches.pushPlayerContext(c);
                    AbstractDungeon.player.energy.recharge();
                    for (AbstractRelic r : AbstractDungeon.player.relics)
                        r.onEnergyRecharge();
                    for (AbstractPower p : AbstractDungeon.player.powers)
                        p.onEnergyRecharge();
                    ContextPatches.popPlayerContext();
                }
            }
        }
    }

}
