package BoardGame.monsters.bgbeyond; 
 import BoardGame.monsters.BGDamageIcons;

import BoardGame.actions.CreateIntentAction;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGRegrowPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.RegrowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGDarkling extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    private static final Logger logger = LogManager.getLogger(BGDarkling.class.getName());
    public static final String ID = "BGDarkling";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Darkling");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final int HP_MIN = 48;

    public static final int HP_MAX = 56;
    public static final int A_2_HP_MIN = 50;
    public static final int A_2_HP_MAX = 59;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = -20.0F;
    private static final float HB_W = 260.0F;
    private static final float HB_H = 200.0F;
    private static final int BITE_DMG = 8;
    private static final int A_2_BITE_DMG = 9;

    public String behavior="---";
    public int turnCount=0;
    public BGDarkling(float x, float y, String behavior) {
        super(NAME, "BGDarkling", 56, 0.0F, -20.0F, 260.0F, 200.0F, null, x, y + 20.0F);
        loadAnimation("images/monsters/theForest/darkling/skeleton.atlas", "images/monsters/theForest/darkling/skeleton.json", 1.0F);

        this.behavior=behavior;


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(MathUtils.random(0.75F, 1.0F));

//        if (AbstractDungeon.ascensionLevel >= 7) {
//            setHp(50, 59);
//        } else {
//            setHp(48, 56);
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.chompDmg = 9;
//            this.nipDmg = AbstractDungeon.monsterHpRng.random(9, 13);
//        } else {
//            this.chompDmg = 8;
//            this.nipDmg = AbstractDungeon.monsterHpRng.random(7, 11);
//        }

        setHp(8);

        this.dialogX = -50.0F * Settings.scale;
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
    }
    private int chompDmg; private int nipDmg; private static final int BLOCK_AMT = 12; private static final int CHOMP_AMT = 2; private static final byte CHOMP = 1; private static final byte HARDEN = 2; private static final byte NIP = 3; private static final byte COUNT = 4; private static final byte REINCARNATE = 5; private boolean firstMove = true;

    public void usePreBattleAction() {
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGRegrowPower((AbstractCreature)this)));
//        if(this.behavior=="CUBE") {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)1, AbstractMonster.Intent.DEFEND_BUFF));
//        }
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                turnCount+=1;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, 3));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)2, AbstractMonster.Intent.ATTACK,2,2,true));
                break;
            case 2:
                turnCount+=1;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)1, AbstractMonster.Intent.DEFEND_BUFF));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, 3));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new HealAction((AbstractCreature)this, (AbstractCreature)this, 2));
                break;
            case 6:

                break;
        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    protected void getMove(int num) {
//        if (this.halfDead) {
//            //this should never happen -- if we're halfdead, should have revived previous turn
//            setMove((byte) 6, AbstractMonster.Intent.BUFF);
//            return;
//        }
        if(this.behavior=="CUBE") {
            if(turnCount%2==0) {
                setMove((byte) 1, AbstractMonster.Intent.DEFEND_BUFF);
            }else{
                setMove((byte)2, AbstractMonster.Intent.ATTACK,2,2,true);
            }

            return;
        }
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
    public void dieMove(int roll){
//        if (this.halfDead) {
//            //this shouldn't happen
//            setMove((byte)6, AbstractMonster.Intent.BUFF);
//            return;
//        }
        if(this.behavior=="CUBE") return;
        final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
            move = this.behavior.charAt(0);
        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
            move = this.behavior.charAt(1);
        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
            move = this.behavior.charAt(2);

        if (move == 'S') {
            setMove((byte) 3, AbstractMonster.Intent.DEFEND_BUFF);
        } else if (move == '2') {
            setMove((byte) 4, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base,2,true);
        } else if (move == '3') {
            setMove((byte) 5, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(1)).base);
        }
    }



    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "REVIVE":
                this.halfDead = false;
                break;
        }
    }




    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            this.powers.clear();

            logger.info("This monster is now half dead.");
            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m.id.equals("BGDarkling") && !m.halfDead) {
                    allDead = false;
                }
            }

            logger.info("All dead: " + allDead);
            if (!allDead) {
                logger.info("Next move: "+this.nextMove);
                if (this.nextMove != 6) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TextAboveCreatureAction((AbstractCreature)this, DIALOG[0]));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)6, AbstractMonster.Intent.BUFF));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new CreateIntentAction(this));
                    //createIntent();
                }
            } else {
                (AbstractDungeon.getCurrRoom()).cannotLose = false;
                this.halfDead = false;
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    m.die();
                }
            }
        } else if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose)
            super.die();
    }


    public void regrowthCheck(){
        if (this.halfDead) {
            ((BGDarkling)this).turnCount = 0;
            if (MathUtils.randomBoolean()) {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("DARKLING_REGROW_2",
                        MathUtils.random(-0.1F, 0.1F)));
            } else {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("DARKLING_REGROW_1",
                        MathUtils.random(-0.1F, 0.1F)));
            }
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new HealAction((AbstractCreature) this, (AbstractCreature) this, 4));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ChangeStateAction((AbstractMonster)this, "REVIVE"));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new BGRegrowPower((AbstractCreature) this), 1));
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onSpawnMonster((AbstractMonster)this);
            }
        }
    }

    //jumping through a few hoops to pretend that darklings revive at the start of the next round...
    @SpirePatch2(clz= MonsterGroup.class,method="applyEndOfTurnPowers",paramtypez={})
    public static class applyEndOfTurnPowersPatch{
        @SpirePostfixPatch
        public static void Postfix(MonsterGroup __instance){
            for (AbstractMonster m : __instance.monsters) {
                if(m instanceof BGDarkling){
                    ((BGDarkling)m).regrowthCheck();
                }
            }
        }
    }
}


