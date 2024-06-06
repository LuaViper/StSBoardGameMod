package BoardGame.powers;

import BoardGame.relics.BGIceCream;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BGAngerPower extends AbstractBGPower implements AfterCompletelyResolveCardPower{
    public static final String POWER_ID = "BGAnger";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:Anger");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGAngerPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGAnger";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("anger");
        //TODO NEXT NEXT: onAfterUseCard works with Distraction, still doesn't work with Feel No Pain
        // FNP triggers onExhaust
        // onAfterCardPlayed is player only, and probably not late enough anyway
        //TODO NEXT NEXT:
        // insert patch, targeting abstractgameaction.tickduration
        // if (this.duration == UseCardAction.DUR)     //ReflectionHacks.getPrivateStatic
        //  for (AbstractPower p : m.powers)
        //    if (!card.dontTriggerOnUseCard)
        //      if(p instanceof AfterCompletelyResolveCardPower)
        //        p.onAfterCompletelyResolveCard(card, this);
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }


    @Override
    public void onAfterCompletelyResolveCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.SKILL) {
            addToBot(new DamageAction( AbstractDungeon.player, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
            flash();
        }
    }


    @SpirePatch2(clz= UseCardAction.class, method= "update",
            paramtypez={})
    public static class AfterCompletelyResolveCardPatch{
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={}
        )
        public static void Insert(UseCardAction __instance) {
            float duration=ReflectionHacks.getPrivate(__instance,AbstractGameAction.class,"duration");
            float dur=ReflectionHacks.getPrivateStatic(UseCardAction.class,"DUR");
            if(duration == dur){
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters)
                  for (AbstractPower p : m.powers) {
                      AbstractCard card = ((AbstractCard)ReflectionHacks.getPrivate(__instance,UseCardAction.class,"targetCard"));
                      if (!card.dontTriggerOnUseCard)
                          if (p instanceof AfterCompletelyResolveCardPower)
                              ((AfterCompletelyResolveCardPower)p).onAfterCompletelyResolveCard(card, __instance);
                  }
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(UseCardAction.class,"tickDuration");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}


