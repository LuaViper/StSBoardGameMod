
//Regal Pillow is hardcoded in RestOption constructor and CampfireSleepEffect constructor

package BoardGame.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BGRegalPillow
        extends AbstractBGRelic  {
    public static final String ID = "BGRegal Pillow";
    public static final int HEAL_AMT = 3;

    public BGRegalPillow() {
        super("BGRegal Pillow", "regal_pillow.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.MAGICAL);
    }
    public int getPrice() {return 6;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public AbstractRelic makeCopy() {
        return new BGRegalPillow();
    }



    @SpirePatch2(clz= RestOption.class, method=SpirePatch.CONSTRUCTOR,
            paramtypez={boolean.class})
    public static class RestOptionPillowPatch{
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static void Insert(@ByRef String[] ___description) {
            if(AbstractDungeon.player.hasRelic("BGRegal Pillow")) {
                UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("Rest Option");
                String[] TEXT = uiStrings.TEXT;
                ___description[0] += "\n+3" + TEXT[2] + (AbstractDungeon.player.getRelic("BGRegal Pillow")).name + LocalizedStrings.PERIOD;
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(RestOption.class,"updateUsability");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    //actual +3 healing patch has been moved to RestOptionPatch.java

}


