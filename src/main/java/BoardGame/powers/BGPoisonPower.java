package BoardGame.powers;



import BoardGame.cards.BGGoldenTicket;
import BoardGame.cards.BGRed.BGSeverSoul;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.monsters.bgexordium.BGGremlinAngry;
import BoardGame.multicharacter.patches.AbstractDungeonMonsterPatches;
import BoardGame.multicharacter.patches.UseCardPatch;
import basemod.AutoAdd;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.PoisonLoseHpAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import org.clapper.util.classutil.ClassInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BGPoisonPower extends AbstractBGPower {
    public static final String POWER_ID = "BGPoison";

    private static final int MAX_POISON_TOKENS=30;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGPoison");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private AbstractCreature source;

    public BGPoisonPower(AbstractCreature owner, AbstractCreature source, int poisonAmt) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.source = source;
        this.amount = poisonAmt;
        //TODO: this is currently 30 across ALL enemies, but it doesn't take physical token limits into account yet
        if (this.amount >= 30)
            this.amount = 30;
        updateDescription();
        loadRegion("poison");
        this.type = AbstractPower.PowerType.DEBUFF;
        //this.isTurnBased = true;
        capPoisonOnEnemy((AbstractMonster) owner);
        //TODO: if this is a multicharacter, apply only to one player (possibly the original multichar)!
        addToTop(new ApplyPowerAction(AbstractDungeon.player,AbstractDungeon.player,new BGPoisonProccerPower(AbstractDungeon.player)));
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_POISON", 0.05F);
    }

    public void updateDescription() {
        if (this.owner == null || this.owner.isPlayer) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[1];
        }
    }

    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
//        if (this.amount > 29 && AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.THE_SILENT)
//            UnlockTracker.unlockAchievement("CATALYST");
        //TODO: physical token limits -- also this should be 30 across ALL enemies
        if(this.amount>30)
            this.amount=30;
        capPoisonOnEnemy((AbstractMonster) owner);
    }

    public void capPoisonOnEnemy(AbstractMonster m){
        int total=0;
        MonsterGroup oM = AbstractDungeonMonsterPatches.Field.originalMonsters.get(AbstractDungeon.getCurrRoom());
        ArrayList<AbstractMonster> monsters;
        if(oM!=null && !oM.monsters.isEmpty())
            monsters=oM.monsters;
        else
            monsters=AbstractDungeon.getMonsters().monsters;

        for (AbstractMonster m2 : monsters) {
            if(m2!=m) {
                AbstractPower p = m2.getPower("BGPoison");
                if (p != null) {
                    total+=p.amount;
                }
            }
        }
        //BoardGame.BoardGame.logger.info("Current total: "+total);
//        if(total>30){
//            BoardGame.BoardGame.logger.info("Total is >30; something's wrong!");
//        }
        //TODO: if max<0, logger debug message
        int maximumAllowedPoisonOnCurrentEnemy=Math.max(0,MAX_POISON_TOKENS-total);
//        BoardGame.BoardGame.logger.info("Target poison stacks: "+this.amount);
//        BoardGame.BoardGame.logger.info("Remaining tokens: "+maximumAllowedPoisonOnCurrentEnemy);

        if(this.amount>maximumAllowedPoisonOnCurrentEnemy) {
            //TODO: "Out of poison tokens" thought bubble
            //BoardGame.BoardGame.logger.info("Out of poison tokens, cap to " + maximumAllowedPoisonOnCurrentEnemy);
            this.amount = maximumAllowedPoisonOnCurrentEnemy;
        }
        if(this.amount<=0){
            //TODO: for reasons we don't yet understand, both addToBot and addToTop fail to remove a 0-stack poison here unless the poison stack already existed (has it not been registered with the powers list?)
            //BoardGame.BoardGame.logger.info("Amount is <0, attempt to remove poison");
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner,this.source,this));
        }
        //BoardGame.BoardGame.logger.info("Final amount: "+this.amount);
    }

    public void proc() {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flashWithoutSound();
            //note that we're calling vanilla PoisonLoseHpAction here --
            // it only checks for vanilla poison, so it won't decrement BGpoison, and it still uses the damage amount we pass it
            addToBot((AbstractGameAction)new PoisonLoseHpAction(this.owner, this.source, this.amount, AbstractGameAction.AttackEffect.POISON));
        }
        if(this.amount<=0){
            addToBot((AbstractGameAction)new RemoveSpecificPowerAction(this.owner,this.source,this));
        }
    }


    @SpirePatch2(clz= AbstractCreature.class, method="renderHealth",
            paramtypez={SpriteBatch.class})
    public static class PoisonHealthBarPatch1 {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"x","y"}
        )
        public static void Foo(AbstractCreature __instance, SpriteBatch sb, float ___x, float ___y) {
            if (__instance.hasPower(BGPoisonPower.POWER_ID))
                ReflectionHacks.privateMethod(AbstractCreature.class,"renderGreenHealthBar",SpriteBatch.class,float.class,float.class).invoke(__instance,sb,___x,___y);
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    public static boolean isPoisoned(AbstractCreature instance) {
        return instance.hasPower(PoisonPower.POWER_ID) || instance.hasPower(BGPoisonPower.POWER_ID);
    }


    @SpirePatch2(clz= AbstractCreature.class, method="renderRedHealthBar",
            paramtypez={SpriteBatch.class, float.class, float.class})
    public static class PoisonHealthBarPatch2 {
        private static int counter=0;
        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (counter == 0 && m.getClassName().equals(AbstractCreature.class.getName()) && m.getMethodName().equals("hasPower")) {
                    //if (m.getClassName().equals(AbstractCreature.class.getName()) && m.getMethodName().equals("hasPower")) {
                        counter += 1; //targeting hasPower("Poison")
                        m.replace("$_ =  "+BGPoisonPower.class.getName()+".isPoisoned($0);");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz= AbstractCreature.class, method="renderRedHealthBar",
            paramtypez={SpriteBatch.class, float.class, float.class})
    public static class PoisonHealthBarPatch3 {
        @SpireInstrumentPatch
        public static ExprEditor Foo() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCreature.class.getName()) && m.getMethodName().equals("getPower")) {
                        //targeting int poisonAmt = (getPower("Poison")).amount;
                        //at this point we know creature has either Poison or BGPoison, but we don't know which one
                        //just stop getPower("Poison") from crashing.
                        //we'll fill in the correct poisonAmt immediately afterward, in patch 4
                        m.replace("$_ = new "+PoisonPower.class.getName()+"(null,null,0);");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz= AbstractCreature.class, method="renderRedHealthBar",
            paramtypez={SpriteBatch.class, float.class, float.class})
    public static class PoisonHealthBarPatch4 {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"poisonAmt"}
        )
        public static void Foo(AbstractCreature __instance, @ByRef int[] ___poisonAmt) {
            if(__instance.hasPower(PoisonPower.POWER_ID)){
                ___poisonAmt[0]=__instance.getPower(PoisonPower.POWER_ID).amount;
            }
            if(__instance.hasPower(BGPoisonPower.POWER_ID)){
                ___poisonAmt[0]=Math.max(___poisonAmt[0],__instance.getPower(BGPoisonPower.POWER_ID).amount);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "hasPower");
                return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher)[1]};
            }
        }
    }

}

