
//Ice Cream is hardcoded into EnergyManager.recharge()
//also need to cap energy during EnergyPanel.addEnergy()



package BoardGame.relics;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;


public class BGIceCream extends AbstractBGRelic  {
    public static final String ID = "BGIce Cream";

    public BGIceCream() {
        super("BGIce Cream", "iceCream.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.FLAT);
    }

    public int getPrice() {return 7;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public AbstractRelic makeCopy() {
        return new BGIceCream();
    }


    @SpirePatch2(clz= EnergyManager.class, method= "recharge",
            paramtypez={})
    public static class EnergyManagerIceCreamPatch{
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(EnergyManager __instance) {
            if (AbstractDungeon.player.hasRelic("BGIce Cream")) {
                if (EnergyPanel.totalCount > 0) {
                    AbstractDungeon.player.getRelic("BGIce Cream").flash();
                    AbstractDungeon.actionManager.addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, AbstractDungeon.player
                            .getRelic("BGIce Cream")));
                }
                EnergyPanel.addEnergy(__instance.energy);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(EnergyPanel.class,"setEnergy");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    @SpirePatch2(clz= EnergyPanel.class, method= "addEnergy",
            paramtypez={int.class})
    public static class EnergyPanelMaxEnergyPatch{
        @SpirePostfixPatch public static void Postfix(){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                if(EnergyPanel.totalCount>6) EnergyPanel.totalCount=6;
            }
        }
    }
}


