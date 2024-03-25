package BoardGame.cards.BGPurple;
import BoardGame.actions.BGPlayCardFromDiscardPileAction;
import BoardGame.actions.CheckAfterUseCardAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGCurse.BGParasite;
import BoardGame.characters.BGWatcher;
import BoardGame.screen.TargetSelectScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

//TODO: does this cooperate with Time Eater and other No Play effects + Golden Eye?
public class BGWeave extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWeave");
    public static final String ID = "BGWeave";

    private boolean activated=false;

    public BGWeave() {
        super("BGWeave", cardStrings.NAME, "purple/attack/weave", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.ENEMY);

        this.baseDamage=1;
        this.baseMagicNumber=5;
        this.magicNumber=this.baseMagicNumber;
    }

    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        if(activated) this.baseDamage += this.magicNumber;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        if(activated) this.baseDamage += this.magicNumber;
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        this.activated=false;
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGWeave();
    }


    public void triggerOnScryDiscard(){
        activated=true;
        //TODO: we've fixed the weave not settling correctly, but we might be able to roll back some of this -- maybe all of it, as long as we prevent souls.discard from happening
        AbstractCard c=this;
        if (AbstractDungeon.player.hoveredCard == c) {
            AbstractDungeon.player.releaseCard();
        }
        /////////////////////// this here is CardGroup.moveToDiscardPile, minus the souls.discard
        AbstractDungeon.actionManager.removeFromQueue(c);
        c.unhover();
        c.untip();
        c.stopGlowing();
        AbstractDungeon.player.drawPile.group.remove(c);
        c.shrink();
        c.darken(false);
        addToBot((AbstractGameAction) new BGPlayCardFromDiscardPileAction(this));
    }

    @SpirePatch2(clz= ScryAction.class,method="update",paramtypez={})
    public static class ScryActionPatch {
        @SpireInsertPatch(
                locator = BGWeave.ScryActionPatch.Locator.class,
                localvars = {"c"}
        )
        public static SpireReturn<Void> Insert(@ByRef AbstractCard[] ___c) {
            if(___c[0] instanceof BGWeave){
                ((BGWeave)___c[0]).triggerOnScryDiscard();
                BoardGame.BoardGame.logger.info("Discarded BGWeave during Scry, setting card reference to null");
                ___c[0]=null;   //stop card from going to discard -- it messes with the limbo/autoplay stuff we just set up
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "moveToDiscardPile");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz= CardGroup.class,method="moveToDiscardPile",paramtypez={AbstractCard.class})
    public static class ScryActionPatch2 {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCard c) {
            if(c == null){
                for(StackTraceElement ste : Thread.currentThread().getStackTrace()){
                    //TODO: is there a way to check for BGWeave instead of ScryAction? (It's not in the stacktrace at this point)
                    if(ste.getClassName().equals("com.megacrit.cardcrawl.actions.utility.ScryAction")){
                        BoardGame.BoardGame.logger.info("Just called moveToDiscardPile(null), but ScryAction was in the stack trace as expected, so we're failing silently");
                        return SpireReturn.Return();
                    }
                }
                BoardGame.BoardGame.logger.warn("Just called moveToDiscardPile(null), and ScryAction was NOT in the stack trace. NullPointerException incoming!");
            }
            return SpireReturn.Continue();
        }
    }


}



