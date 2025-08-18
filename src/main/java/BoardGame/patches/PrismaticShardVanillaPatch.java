package BoardGame.patches;

import BoardGame.characters.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class PrismaticShardVanillaPatch {
    public static ArrayList<AbstractCard.CardColor> excludedColors;

    // instrumentpatch is not needed here.
    // use insertpatch targeting CardGroup.shuffle
    // and remove excluded cards from local CardGroup anyCard
    // ...note that getAnyColorCard has 2 overloads, patch both of them
    static{
        excludedColors=new ArrayList<>();
        excludedColors.add(BGIronclad.Enums.BG_RED);
        excludedColors.add(BGSilent.Enums.BG_GREEN);
        excludedColors.add(BGDefect.Enums.BG_BLUE);
        excludedColors.add(BGWatcher.Enums.BG_PURPLE);
        excludedColors.add(BGColorless.Enums.CARD_COLOR);
        excludedColors.add(BGCurse.Enums.BG_CURSE);
    }

    @SpirePatch2(clz= CardLibrary.class,method="getAnyColorCard",paramtypez={AbstractCard.CardType.class, AbstractCard.CardRarity.class})
    public static class CardLibraryPatches {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"anyCard"}
        )
        public static void Insert(CardGroup anyCard) {
            for(int i = anyCard.size()-1; i>=0; i-=1){
                if(excludedColors.contains(anyCard.group.get(i).color)){
                    anyCard.group.remove(i);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "shuffle");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
    @SpirePatch2(clz= CardLibrary.class,method="getAnyColorCard",paramtypez={AbstractCard.CardRarity.class})
    public static class CardLibraryPatches2 {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"anyCard"}
        )
        public static void Insert(CardGroup anyCard) {
            for(int i = anyCard.size()-1; i>=0; i-=1){
                if(excludedColors.contains(anyCard.group.get(i).color)){
                    anyCard.group.remove(i);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "shuffle");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }




}
