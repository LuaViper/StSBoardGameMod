package BoardGame.monsters.bgexordium;

import BoardGame.powers.BGModeShiftPower;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: patch clearPostCombatActions so that DAMAGE actions aren't processed after combat ends in AbstractBGDungeon

public class BGTheGuardian extends AbstractBGMonster {
    private static final Logger logger = LogManager.getLogger(BGTheGuardian.class.getName());
    public static final String ID = "BGTheGuardian";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TheGuardian");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final String DEFENSIVE_MODE = "Defensive Mode";

    private static final String OFFENSIVE_MODE = "Offensive Mode";

    private static final String RESET_THRESH = "Reset Threshold";
    public static final int HP = 240;
    public static final int A_2_HP = 250;
    private static final int DMG_THRESHOLD = 30;
    private static final int A_2_DMG_THRESHOLD = 35;
    private static final int A_19_DMG_THRESHOLD = 40;
    private int dmgThreshold;
    private int dmgThresholdIncrease = 10;
    private int dmgTaken;
    private static final int FIERCE_BASH_DMG = 32;
    private static final int A_2_FIERCE_BASH_DMG = 36;
    private static final int ROLL_DMG = 9;
    private static final int A_2_ROLL_DMG = 10;
    private int fierceBashDamage;
    private int whirlwindDamage = 5, twinSlamDamage = 8, rollDamage, whirlwindCount = 4, DEFENSIVE_BLOCK = 20;

    private int blockAmount = 9; private int thornsDamage = 3; private int VENT_DEBUFF = 2;
    private boolean isOpen = true;
    private boolean closeUpTriggered = false;
    private static final byte CLOSE_UP = 1;
    private static final byte FIERCE_BASH = 2;
    private static final String CLOSEUP_NAME = MOVES[0], FIERCEBASH_NAME = MOVES[1], TWINSLAM_NAME = MOVES[3]; private static final byte ROLL_ATTACK = 3; private static final byte TWIN_SLAM = 4; private static final byte WHIRLWIND = 5; private static final byte CHARGE_UP = 6; private static final byte VENT_STEAM = 7;
    private static final String WHIRLWIND_NAME = MOVES[4], CHARGEUP_NAME = MOVES[5], VENTSTEAM_NAME = MOVES[6];

    public BGTheGuardian() {
        super(NAME, "BGTheGuardian", 240, 0.0F, 95.0F, 440.0F, 350.0F, null, -50.0F, -100.0F);
        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        setHp(40);
        this.whirlwindDamage=2;
        this.whirlwindCount=1;
        this.fierceBashDamage=6;
        this.rollDamage=2;
        this.twinSlamDamage=4;
        this.DEFENSIVE_BLOCK=5;
        this.blockAmount=5;
        this.thornsDamage=1;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.whirlwindDamage));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.fierceBashDamage));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.rollDamage));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.twinSlamDamage));

        loadAnimation("images/monsters/theBottom/boss/guardian/skeleton.atlas", "images/monsters/theBottom/boss/guardian/skeleton.json", 2.0F);



        this.state.setAnimation(0, "idle", true);
    }


    public void usePreBattleAction() {
        if (AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss) {
            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGModeShiftPower((AbstractCreature)this)));

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Reset Threshold"));
        UnlockTracker.markBossAsSeen("GUARDIAN");
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                useCloseUp();   //transition to Defensive Mode / apply Sharp Hide
                return;
            case 2:
                useFierceBash();    //6 attack (attack turn 2)
                return;
            case 3:
                useRollAttack();    //2 attack (defense turn 1)

                return;
            case 4:
                useTwinSmash();     //4 attack+str (defense turn 2)
                return;
            case 5:
                useWhirlwind(); //attack+block (attack turn 2)
                useChargeUp();
                return;
            case 6:
                //unused (combined with Whirlwind)
                return;
            case 7:
                //unused
                return;
        }
        logger.info("ERROR");
    }



    private void useFierceBash() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        setMove(WHIRLWIND_NAME, (byte)5, AbstractMonster.Intent.ATTACK_DEFEND, this.whirlwindDamage);
    }


    private void useCloseUp() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TextAboveCreatureAction((AbstractCreature)this, DIALOG[1]));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new SharpHidePower((AbstractCreature)this, this.thornsDamage)));


        setMove((byte)3, AbstractMonster.Intent.ATTACK, this.rollDamage);
    }

    private void useTwinSmash() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Offensive Mode"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                .get(3), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower(this, 1), 1));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this, "Sharp Hide"));
        setMove(WHIRLWIND_NAME, (byte)5, AbstractMonster.Intent.ATTACK_DEFEND, this.whirlwindDamage);
    }

    private void useRollAttack() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                .get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        setMove(TWINSLAM_NAME, (byte)4, AbstractMonster.Intent.ATTACK_BUFF, this.twinSlamDamage);
    }

    private void useWhirlwind() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("ATTACK_WHIRLWIND"));
        for (int i = 0; i < this.whirlwindCount; i++) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("ATTACK_HEAVY"));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new CleaveEffect(true), 0.15F));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                    .get(0), AbstractGameAction.AttackEffect.NONE, true));
        }

        //setmove is in useChargeUp, which occurs on the same turn
    }




    private void useChargeUp() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.blockAmount));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_GUARDIAN_DESTROY"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[2], 1.0F, 2.5F));

        setMove(FIERCEBASH_NAME, (byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
    }


    protected void getMove(int num) {
        if (this.isOpen) {
            setMove(WHIRLWIND_NAME, (byte)5, AbstractMonster.Intent.ATTACK_DEFEND, this.whirlwindDamage);
        } else {
            setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(2)).base);
        }
    }





    public void changeState(String stateName) {
        switch (stateName) {
            case "Defensive Mode":
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this, "BGMode Shift"));

                CardCrawlGame.sound.play("GUARDIAN_ROLL_UP");
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.DEFENSIVE_BLOCK));
                this.stateData.setMix("idle", "transition", 0.1F);
                this.state.setTimeScale(2.0F);
                this.state.setAnimation(0, "transition", false);
                this.state.addAnimation(0, "defensive", true, 0.0F);
                this.dmgThreshold += this.dmgThresholdIncrease;
                setMove(CLOSEUP_NAME, (byte)1, AbstractMonster.Intent.BUFF);
                createIntent();
                this.isOpen = false;
                updateHitbox(0.0F, 95.0F, 440.0F, 250.0F);
                healthBarUpdatedEvent();
                break;
            case "Offensive Mode":
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGModeShiftPower((AbstractCreature)this)));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Reset Threshold"));
                if (this.currentBlock != 0) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new LoseBlockAction((AbstractCreature)this, (AbstractCreature)this, this.currentBlock));
                }
                this.stateData.setMix("defensive", "idle", 0.2F);
                this.state.setTimeScale(1.0F);
                this.state.setAnimation(0, "idle", true);
                this.isOpen = true;
                this.closeUpTriggered = false;
                updateHitbox(0.0F, 95.0F, 440.0F, 350.0F);
                healthBarUpdatedEvent();
                break;
            case "Reset Threshold":
                this.dmgTaken = 0;
                break;
        }
    }








//    public void damage(DamageInfo info) {
//        int tmpHealth = this.currentHealth;
//        super.damage(info);
//
//        if (this.isOpen && !this.closeUpTriggered &&
//                tmpHealth > this.currentHealth && !this.isDying) {
//            this.dmgTaken += tmpHealth - this.currentHealth;
//            if (getPower("Mode Shift") != null) {
//                (getPower("Mode Shift")).amount -= tmpHealth - this.currentHealth;
//                getPower("Mode Shift").updateDescription();
//            }
//
//            if (this.dmgTaken >= this.dmgThreshold) {
//                this.dmgTaken = 0;
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new IntenseZoomEffect(this.hb.cX, this.hb.cY, false), 0.05F, true));
//
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Defensive Mode"));
//                this.closeUpTriggered = true;
//            }
//        }
//    }

    protected int decrementBlock(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.HP_LOSS && this.currentBlock > 0) {
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);


            if (damageAmount > this.currentBlock) {
                damageAmount -= this.currentBlock;
                if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedNumberEffect(this.hb.cX, this.hb.cY + this.hb.height / 2.0F,
                            Integer.toString(this.currentBlock)));
                }
                loseBlock();
                brokeBlock();

            }
            else if (damageAmount == this.currentBlock) {
                damageAmount = 0;
                loseBlock();
                brokeBlock();
                //The "intent bug" has been solved.  Apparently "Blocked" appears *twice* if you break an enemy's block exactly and Settings.SHOW_DMG_BLOCK is enabled.
                //TODO: localization
                AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, "Blocked"));
            }
            else {

                CardCrawlGame.sound.play("BLOCK_ATTACK");
                loseBlock(damageAmount);
                for (int i = 0; i < 18; i++) {
                    AbstractDungeon.effectList.add(new BlockImpactLineEffect(this.hb.cX, this.hb.cY));
                }
                if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedNumberEffect(this.hb.cX, this.hb.cY + this.hb.height / 2.0F,
                            Integer.toString(damageAmount)));
                }
                damageAmount = 0;
            }
        }
        return damageAmount;
    }

    private void brokeBlock() {
        if (this instanceof AbstractMonster) {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onBlockBroken(this);
            }
        }

        AbstractDungeon.effectList.add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0F + BLOCK_ICON_X, this.hb.cY - this.hb.height / 2.0F + BLOCK_ICON_Y));

        CardCrawlGame.sound.play("BLOCK_BREAK");

        if (this.isOpen && !this.closeUpTriggered && !this.isDying) {
            if (getPower("BGMode Shift") != null) {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new VFXAction((AbstractCreature) this, (AbstractGameEffect) new IntenseZoomEffect(this.hb.cX, this.hb.cY, false), 0.05F, true));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ChangeStateAction(this, "Defensive Mode"));
                this.closeUpTriggered = true;
            }
        }
    }




    public void render(SpriteBatch sb) {
        super.render(sb);
    }


    public void die() {
        useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        super.die();
        onBossVictoryLogic();
        UnlockTracker.hardUnlockOverride("GUARDIAN");
        UnlockTracker.unlockAchievement("GUARDIAN");
    }
}


