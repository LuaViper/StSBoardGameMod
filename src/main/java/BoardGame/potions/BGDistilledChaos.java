
package BoardGame.potions;

import BoardGame.actions.BGPlayDrawnCardAction;
import BoardGame.actions.BGPlayThreeDrawnCardsAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGCurse.BGParasite;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.ui.EntropicBrewPotionButton;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

//TODO: if Distilled Chaos tries to play an Unplayable card, the entire chain breaks! need to patch whatever is in charge of discarding unplayable cards
//TODO LATER: strictly speaking we should ask for the order of cards one at a time, in case the first card draws additional cards and that informs the decision of the remaining 2 cards

public class BGDistilledChaos extends AbstractPotion {
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("BoardGame:BGDistilledChaos");

    public static final String POTION_ID = "BGDistilledChaos";

    public BGDistilledChaos() {
        super(potionStrings.NAME, "BGDistilledChaos", AbstractPotion.PotionRarity.UNCOMMON, AbstractPotion.PotionSize.JAR, AbstractPotion.PotionEffect.RAINBOW, Color.WHITE, null, Color.GREEN);
//        this.liquidColor= CardHelper.getColor(236f, 56f, 55f);
//        this.hybridColor= CardHelper.getColor(206f, 0f, 137f);
//        //this.spotsColor = CardHelper.getColor(236f, 163f, 128f);
//        this.spotsColor = new Color(0,0,0,0);

        this.isThrown = false;
    }

    public int getPrice() {return 3;}


    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }


    public void use(AbstractCreature target) {
        addToBot((AbstractGameAction)new DrawCardAction(3, (AbstractGameAction)new BGPlayThreeDrawnCardsAction()));
    }


    public int getPotency(int ascensionLevel) {
        return 3;
    }
    public AbstractPotion makeCopy() {
        return new BGDistilledChaos();
    }



    @SpirePatch2(clz= GameActionManager.class,method="getNextAction")
    public static class DiscardUnplayableAutoplayCardPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"c"}
        )
        public static void Foo(AbstractCard ___c) {
            if(___c instanceof AbstractBGCard){
                AbstractBGCard c=(AbstractBGCard)___c;
                if(c.isInAutoplay){
                    c.moveToDiscardPile();
                    if(c.followUpCardChain!=null && !c.followUpCardChain.isEmpty()){
                        AbstractCard card=c.followUpCardChain.get(0);
                        if(card instanceof AbstractBGCard){
                            ((AbstractBGCard)card).followUpCardChain=c.followUpCardChain;
                            ((AbstractBGCard)card).followUpCardChain.remove(0);
                            if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
                                TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                                    if (target != null) {
                                        card.calculateCardDamage(target);
                                    }
                                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new NewQueueCardAction(card, target, true, true));
                                };
                                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for " + card.name + ".")); //TODO: localization
                            } else {
                                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new NewQueueCardAction(card, null, true, true));
                            }
                        }
                    }
                }

            }
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.NewExprMatcher(ThoughtBubble.class);
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }



}


