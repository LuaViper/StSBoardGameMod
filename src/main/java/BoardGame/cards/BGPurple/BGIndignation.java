package BoardGame.cards.BGPurple;
import BoardGame.actions.BGIndignationAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.AbstractBGCharacter;
import BoardGame.characters.BGWatcher;
import BoardGame.relics.BGRegalPillow;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

//this appears to be the only card that needs an onChangeStance event.
public class BGIndignation extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGIndignation");
    public static final String ID = "BGIndignation";

    public BGIndignation() {
        super("BGIndignation", cardStrings.NAME, "purple/skill/indignation", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);

    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGIndignationAction(this.upgraded,m));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            this.onChangeStance(AbstractDungeon.player.stance);
        }
    }


    public AbstractCard makeCopy() {
        return new BGIndignation();
    }


    public void onChangeStance(AbstractStance newStance){
        if(newStance.ID.equals("BGWrath")){
            if(!upgraded)
                target=CardTarget.ENEMY;
            else
                target=CardTarget.ALL_ENEMY;
        }else{
            target=CardTarget.SELF;
        }

    }


    @SpirePatch2(clz= ChangeStanceAction.class, method="update",
            paramtypez={})
    public static class ChangeStanceActionPatch{
        @SpireInsertPatch(
                locator= BGIndignation.ChangeStanceActionPatch.Locator.class,
                localvars={}
        )
        public static void Insert(AbstractStance ___newStance){
            if(!___newStance.ID.equals(AbstractDungeon.player.stance.ID)){
                ((AbstractBGCharacter)AbstractDungeon.player).stanceChangedThisTurn=true;
            }
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c instanceof BGIndignation)
                    ((BGIndignation)c).onChangeStance(___newStance);
            }
            for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                if (c instanceof BGIndignation)
                    ((BGIndignation)c).onChangeStance(___newStance);
            }
            for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
                if (c instanceof BGIndignation)
                    ((BGIndignation)c).onChangeStance(___newStance);
            }
            for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
                if (c instanceof BGIndignation)
                    ((BGIndignation)c).onChangeStance(___newStance);
            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class,"powers");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

}


