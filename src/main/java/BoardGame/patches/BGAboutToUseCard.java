package BoardGame.patches;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.powers.AbstractBGPower;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGRegalPillow;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

//TODO NEXT: timing is STILL wrong -- if player has a pending start-of-turn relic, shieldspear facing needs to wait until after it triggers

public class BGAboutToUseCard {
    private AbstractCard targetCard;

    public AbstractCreature target = null;
    //note that vanilla UseCardAction calls onUseCard during the CONSTRUCTOR, so
    public static void process(AbstractCard card, AbstractCreature target) {
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (!card.dontTriggerOnUseCard && p instanceof AbstractBGPower)
                ((AbstractBGPower)p).onAboutToUseCard(card);
        }
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (!card.dontTriggerOnUseCard && r instanceof AbstractBGRelic)
                ((AbstractBGRelic)r).onAboutToUseCard(card);
        }
    }

    //TODO: there are multiple CardQueueItem constructors, make sure we don't need any more of these.
    // could also try AbstractPlayer.playCard instead.
    @SpirePatch2(clz= CardQueueItem.class, method= SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCard.class, AbstractMonster.class})
    public static class Foo{
        @SpirePrefixPatch
        public static void Foo(AbstractCard ___card, AbstractMonster ___monster) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                BGAboutToUseCard.process(___card,___monster);
            }
        }
    }

//    @SpirePatch2(clz= AbstractPlayer.class, method= "useCard",
//            paramtypez={AbstractCard.class, AbstractMonster.class,int.class})
//    public static class Foo{
//        @SpireInsertPatch(
//                locator= Locator.class,
//                localvars={}
//        )
//        public static void Foo(AbstractCard ___c,AbstractMonster ___monster) {
//            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
//                BGAboutToUseCard.process(___c,___monster);
//            }
//        }
//        private static class Locator extends SpireInsertLocator {
//            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
//                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class,"use");
//                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
//            }
//        }
//    }

}
