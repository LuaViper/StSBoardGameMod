package BoardGame.patches;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.dungeons.BGTheCity;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.DungeonMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

public class ThornsPatches {

    //Spiker death check is handled in SpikerReflectAction
    @SpirePatch2(clz = GameActionManager.class, method = "clearPostCombatActions",
            paramtypez={})
    public static class Foo {
        @SpirePostfixPatch
        public static void Bar(GameActionManager __instance) {
            //we've already cleared most actions.  if this is boardgame, clear damage actions too
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                for (Iterator<AbstractGameAction> i = __instance.actions.iterator(); i.hasNext(); ) {
                    AbstractGameAction e = i.next();
                    if (e.actionType == AbstractGameAction.ActionType.DAMAGE)
                        i.remove();
                }
            }
        }
    }

}
