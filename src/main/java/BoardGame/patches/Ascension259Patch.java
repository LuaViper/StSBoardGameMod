package BoardGame.patches;

import BoardGame.cards.BGCurse.BGAscendersBane;
import BoardGame.dungeons.BGExordium;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class Ascension259Patch {


    @SpirePatch2(clz=AbstractDungeon.class, method="dungeonTransitionSetup")
    public static class MaxHPPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            if(CardCrawlGame.dungeon instanceof BGExordium) {
                if (AbstractDungeon.ascensionLevel >= 2)
                    player.decreaseMaxHealth(player.getAscensionMaxHPLoss());
                if (AbstractDungeon.ascensionLevel >= 5) {
                    player.masterDeck.addToTop((AbstractCard)new BGAscendersBane());
                    UnlockTracker.markCardAsSeen("BGAscendersBane");
                }
                if (AbstractDungeon.ascensionLevel >= 9)
                    player.currentHealth = MathUtils.round(player.maxHealth - 1);


            }

        }
    }
}
