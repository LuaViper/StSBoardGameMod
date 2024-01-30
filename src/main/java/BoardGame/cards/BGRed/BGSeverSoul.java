package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGCurse.BGParasite;
import BoardGame.characters.BGIronclad;
import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BGSeverSoul extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSever Soul");
    public static final String ID = "BGSever Soul";

    public BGSeverSoul() {
        super("BGSever Soul", cardStrings.NAME, "red/attack/sever_soul", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);

        this.baseDamage = 3;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        if (!this.upgraded) {
            addToBot((AbstractGameAction)new ExhaustAction(1, false, true, false));
        }else {
            addToBot((AbstractGameAction)new ExhaustAction(2, false, true, false));
        }
    }


    public AbstractCard makeCopy() {
        return new BGSeverSoul();
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }




    @SpirePatch2(clz= HandCardSelectScreen.class,method="refreshSelectedCards",paramtypez={})
    public static class BGHandCardSelectScreenPatch {
        @SpireInsertPatch(
            locator = BGSeverSoul.BGHandCardSelectScreenPatch.Locator.class,
            localvars = {}
        )
        public static void Insert(HandCardSelectScreen __instance, @ByRef boolean[] ___anyNumber) {
            //we're not 100% sure this patch doesn't break anything, so only do it if we're playing the board game
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)){
                SpireReturn.Continue();
                return;
            }
            if (__instance.selectedCards.size() >= 1 && ___anyNumber[0] && !__instance.canPickZero) {
                __instance.button.enable();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(HandCardSelectScreen.class, "upTo");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }



}


