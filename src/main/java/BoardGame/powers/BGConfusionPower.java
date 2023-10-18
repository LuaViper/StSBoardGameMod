
//AbstractCard.hasEnoughEnergy
//AbstractCard.getCost
//AbstractPlayer.useCard
//AbstractCard.costForTurn
//AbstractCard.energyOnUse

//WARNING: X-cost cards, and/or the actions they use, are hardcoded to remove all energy.  must adjust that in each file

package BoardGame.powers;

import BoardGame.monsters.DieControlledMoves;
import BoardGame.relics.BGRegalPillow;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BGConfusionPower extends AbstractBGPower {
    public static final String POWER_ID = "BGConfusion";

    private static final Logger logger = LogManager.getLogger(BGConfusionPower.class.getName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGConfusion");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGConfusionPower(AbstractCreature owner,int amount) {
        this.name = NAME;
        this.ID = "BGConfusion";
        this.owner = owner;
        loadRegion("confusion");
        this.type = AbstractPower.PowerType.DEBUFF;
        this.amount=amount;
        this.priority = 0;
        updateDescription();
    }

    public void stackPower(int stackAmount) {
        this.amount=stackAmount;
        this.fontScale = 8.0F;
        updateDescription();
    }


    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_CONFUSION", 0.05F);
    }

    public void onAfterCardPlayed(AbstractCard usedCard){
        this.amount=-1;
        //logger.info("Confusion: onAfterCardPlayed");
        updateDescription();
    }

    public boolean canPlay(AbstractCard card){
        if(this.amount>-1){
            if(EnergyPanel.totalCount<this.amount){
                return false;
            }
        }
        return true;
    }

//    public void onCardDraw(AbstractCard card) {
//        if (card.cost >= 0) {
//            int newCost = AbstractDungeon.cardRandomRng.random(3);
//            if (card.cost != newCost) {
//                card.cost = newCost;
//                card.costForTurn = card.cost;
//                card.isCostModified = true;
//            }
//            card.freeToPlayOnce = false;
//        }
//    }


    public void updateDescription() {
        //logger.info("updateDescrpition: "+this.description+" "+amount+" ? "+this.amount);
        switch(amount) {
            case 1:
                this.description = DESCRIPTIONS[1]+"[E]"+DESCRIPTIONS[2];
                break;
            case 2:
                this.description = DESCRIPTIONS[1]+"[E] [E]"+DESCRIPTIONS[2];
                break;
            case 3:
                this.description = DESCRIPTIONS[1]+"[E] [E] [E]"+DESCRIPTIONS[2];
                break;
            default:
                this.description = DESCRIPTIONS[0];
                break;
        }
    }

    //hasEnoughEnergy checks if we're allowed to play the card in the first place
    @SpirePatch2(clz=AbstractCard.class,method="hasEnoughEnergy",paramtypez={})
    public static class hasEnoughEnergyPatch{
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Boolean> hasEnoughEnergy(){
            AbstractPower p=AbstractDungeon.player.getPower("BGConfusion");
            if(p!=null) {
                if(p.amount>0) {
                    //TODO: is there ever a time when energy<3 at start of turn?  does Fasting exist in BG?
                    //logger.info("CONFUSION totalCount: " + EnergyPanel.totalCount + " power.amount: " + p.amount);
                    if (EnergyPanel.totalCount >= p.amount) {
                        return SpireReturn.Return(true);
                    }
                }
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(EnergyPanel.class,"totalCount");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


    //getCost affects card display, but not the actual energy paid for it
    @SpirePatch2(clz=AbstractCard.class, method="getCost", paramtypez={})
    public static class getCostPatch{
        @SpirePrefixPatch
        public static SpireReturn<String> getCost(){
            if(AbstractDungeon.player!=null) {
                if (AbstractDungeon.player.hasPower("BGConfusion")) {
                    AbstractPower p = AbstractDungeon.player.getPower("BGConfusion");
                    if (p.amount > -1) {
                        return SpireReturn.Return(Integer.toString(p.amount));
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz= AbstractPlayer.class,method="useCard",
            paramtypez={AbstractCard.class, AbstractMonster.class, int.class})
    public static class ConfusionUseCardPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> useCard(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            if(AbstractDungeon.player.hasPower("BGConfusion")) {
                AbstractPower p = AbstractDungeon.player.getPower("BGConfusion");
                if (p.amount > -1) {
                    int finalAmount=p.amount;


                    if (c.type == AbstractCard.CardType.ATTACK) {
                        __instance.useFastAttackAnimation();
                    }

                    c.calculateCardDamage(monster);


                    //if (c.cost == -1 && EnergyPanel.totalCount < energyOnUse && !c.ignoreEnergyOnUse) {
                    if (!c.ignoreEnergyOnUse) {
                        //c.energyOnUse = EnergyPanel.totalCount;
                        c.energyOnUse = finalAmount;
                    }
                    //logger.info("set card energyOnUse to "+c.energyOnUse);


                    if (c.cost == -1 && c.isInAutoplay) {
                        c.freeToPlayOnce = true;
                    }

                    c.use(__instance, monster);

                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new UseCardAction(c, (AbstractCreature) monster));
                    if (!c.dontTriggerOnUseCard) {
                        __instance.hand.triggerOnOtherCardPlayed(c);
                    }
                    __instance.hand.removeCard(c);
                    __instance.cardInUse = c;

                    c.target_x = (Settings.WIDTH / 2);
                    c.target_y = (Settings.HEIGHT / 2);

                    //X-cost cards have their own energy handlers, but 0-cost cards do not
                    if (c.costForTurn > -1 && !c.freeToPlay() && !c.isInAutoplay && (
                    //if (c.costForTurn > 0 && !c.freeToPlay() && !c.isInAutoplay && (
                            !__instance.hasPower("Corruption") || c.type != AbstractCard.CardType.SKILL)) {
                        //__instance.energy.use(c.costForTurn);
                        //logger.info("BGConfusionPower useCard: "+finalAmount);
                        __instance.energy.use(finalAmount);
                    }


                    if (!__instance.hand.canUseAnyCard() && !__instance.endTurnQueued) {
                        AbstractDungeon.overlayMenu.endTurnButton.isGlowing = true;
                    }

                    //logger.info("done.  energyOnUse is now "+c.energyOnUse);

                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }




}


