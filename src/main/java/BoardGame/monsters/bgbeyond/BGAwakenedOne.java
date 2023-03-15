package BoardGame.monsters.bgbeyond; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.cards.BGStatus.BGSlimed;
import BoardGame.cards.BGStatus.BGVoidCard;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGCuriosityPower;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CuriosityPower;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.AwakenedEyeParticle;
import com.megacrit.cardcrawl.vfx.AwakenedWingParticle;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;





public class BGAwakenedOne
        extends AbstractBGMonster
{
    private static final Logger logger = LogManager.getLogger(BGAwakenedOne.class.getName());
    public static final String ID = "BGAwakenedOne";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("AwakenedOne");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private boolean form1 = true; private boolean firstTurn = true;
    private boolean saidPower = false;
    public static final int STAGE_1_HP = 300;
    public static final int STAGE_2_HP = 300;
    public static final int A_9_STAGE_1_HP = 320;
    public static final int A_9_STAGE_2_HP = 320;
    private static final int A_4_STR = 2;
    private static final byte SLASH = 1;
    private static final byte SOUL_STRIKE = 2;
    private static final byte REBIRTH = 3;
    private static final String SS_NAME = MOVES[0];
    private static final int SLASH_DMG = 20;
    private static final int SS_DMG = 6;
    private static final int SS_AMT = 4;
    private static final int REGEN_AMT = 10;
    private static final String DARK_ECHO_NAME = MOVES[1]; private static final int STR_AMT = 1; private static final byte DARK_ECHO = 5; private static final byte SLUDGE = 6; private static final byte TACKLE = 8; private static final String SLUDGE_NAME = MOVES[3]; private static final int ECHO_DMG = 40;
    private static final int SLUDGE_DMG = 18;
    private static final int TACKLE_DMG = 10;
    private static final int TACKLE_AMT = 3;
    private float fireTimer = 0.0F; private static final float FIRE_TIME = 0.1F;
    private Bone eye;
    private Bone back;
    private boolean animateParticles = false;
    private ArrayList<AwakenedWingParticle> wParticles = new ArrayList<>();



    public BGAwakenedOne(float x, float y) {
        super(NAME, "BGAwakenedOne", 300, 40.0F, -30.0F, 460.0F, 250.0F, null, x, y);

//        if (AbstractDungeon.ascensionLevel >= 9) {
//            setHp(320);
//        } else {
//            setHp(300);
//        }

        loadAnimation("images/monsters/theForest/awakenedOne/skeleton.atlas", "images/monsters/theForest/awakenedOne/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle_1", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle_1", 0.3F);
        this.stateData.setMix("Hit", "Idle_2", 0.2F);
        this.stateData.setMix("Attack_1", "Idle_1", 0.2F);
        this.stateData.setMix("Attack_2", "Idle_2", 0.2F);
        this.state.getData().setMix("Idle_1", "Idle_2", 1.0F);

        this.eye = this.skeleton.findBone("Eye");

        for (Bone b : this.skeleton.getBones()) {
            logger.info(b.getData().getName());
        }
        this.back = this.skeleton.findBone("Hips");

        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -200.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

//        this.damage.add(new DamageInfo((AbstractCreature)this, 20));
//        this.damage.add(new DamageInfo((AbstractCreature)this, 6));
//        this.damage.add(new DamageInfo((AbstractCreature)this, 40));
//        this.damage.add(new DamageInfo((AbstractCreature)this, 18));
//        this.damage.add(new DamageInfo((AbstractCreature)this, 10));

        setHp(50);
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 5));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 7));
        this.damage.add(new DamageInfo((AbstractCreature)this, 4));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));

    }


    public void usePreBattleAction() {
        (AbstractDungeon.getCurrRoom()).rewardAllowed = false; //game is hardcoded to check for TheBeyond / TheEnding dungeons.  here's a workaround
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BEYOND");
        (AbstractDungeon.getCurrRoom()).cannotLose = true;

//        if (AbstractDungeon.ascensionLevel >= 19) {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new RegenerateMonsterPower(this, 15)));
//
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new CuriosityPower((AbstractCreature)this, 2)));
//        } else {
//
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new RegenerateMonsterPower(this, 10)));
//
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new CuriosityPower((AbstractCreature)this, 1)));
//        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGCuriosityPower((AbstractCreature)this, 1)));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new UnawakenedPower((AbstractCreature)this)));

//        if (AbstractDungeon.ascensionLevel >= 4) {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 2), 2));
//        }


        UnlockTracker.markBossAsSeen("CROW");
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_AWAKENED_POUNCE"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_1"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                setMove((byte)1, AbstractMonster.Intent.ATTACK, 5);
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_AWAKENED_POUNCE"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_1"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                setMove((byte)2, AbstractMonster.Intent.ATTACK, 2,2,true);
                break;
            case 2:
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(2), AbstractGameAction.AttackEffect.FIRE));
                }
                setMove((byte)0, AbstractMonster.Intent.ATTACK, 3);
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_AWAKENEDONE_1"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new IntenseZoomEffect(this.hb.cX, this.hb.cY, true), 0.05F, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "REBIRTH"));
                setMove((byte)4, AbstractMonster.Intent.ATTACK, 7);
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.1F));
                this.firstTurn = false;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_AWAKENEDONE_3"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new ShockWaveEffect(this.hb.cX, this.hb.cY, new Color(0.1F, 0.0F, 0.2F, 1.0F), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new ShockWaveEffect(this.hb.cX, this.hb.cY, new Color(0.3F, 0.2F, 0.4F, 1.0F), ShockWaveEffect.ShockWaveType.CHAOTIC), 1.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(3), AbstractGameAction.AttackEffect.SMASH));
                setMove((byte)5, AbstractMonster.Intent.ATTACK_DEBUFF, 4);
                break;
            case 5:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(4), AbstractGameAction.AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGVoidCard(), 2));
                setMove((byte)6, AbstractMonster.Intent.ATTACK_BUFF, 3,2,true);
                break;

            case 6:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_AWAKENED_ATTACK"));
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.06F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(5), AbstractGameAction.AttackEffect.FIRE, true));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                setMove((byte)4, AbstractMonster.Intent.ATTACK, 7);
                break;
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void changeState(String key) {
        switch (key) {
            case "REBIRTH":
//                if (AbstractDungeon.ascensionLevel >= 9) {
//                    this.maxHealth = 320;
//                } else {
//                    this.maxHealth = 300;
//                }
                this.maxHealth=50;
                if (Settings.isEndless && AbstractDungeon.player.hasBlight("ToughEnemies")) {
                    float mod = AbstractDungeon.player.getBlight("ToughEnemies").effectFloat();
                    this.maxHealth = (int)(this.maxHealth * mod);
                }

                if (ModHelper.isModEnabled("MonsterHunter")) {
                    this.currentHealth = (int)(this.currentHealth * 1.5F);
                }

                this.state.setAnimation(0, "Idle_2", true);
                this.halfDead = false;
                this.animateParticles = true;

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new HealAction((AbstractCreature)this, (AbstractCreature)this, this.maxHealth));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new CanLoseAction());
                break;
            case "ATTACK_1":
                this.state.setAnimation(0, "Attack_1", false);
                this.state.addAnimation(0, "Idle_1", true, 0.0F);
                break;
            case "ATTACK_2":
                this.state.setAnimation(0, "Attack_2", false);
                this.state.addAnimation(0, "Idle_2", true, 0.0F);
                break;
        }
    }






    protected void getMove(int num) {
        if (this.form1) {
            if (this.firstTurn) {
                setMove((byte)0, AbstractMonster.Intent.ATTACK, 3);
                this.firstTurn = false;

                return;
            }
            //setMove((byte)1, AbstractMonster.Intent.ATTACK, 5);
                    //setMove(SS_NAME, (byte)2, AbstractMonster.Intent.ATTACK, 6, 4, true);
        }
        else {
            if (this.firstTurn) {
                setMove(DARK_ECHO_NAME, (byte)4, AbstractMonster.Intent.ATTACK, 7);

                return;
            }

                    //setMove(SLUDGE_NAME, (byte)5, AbstractMonster.Intent.ATTACK_DEBUFF, 18);
                    //setMove((byte)6, AbstractMonster.Intent.ATTACK, 10, 3, true);
        }
    }



    public void damage(DamageInfo info) {
        super.damage(info);

        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            if (this.form1) {
                this.state.addAnimation(0, "Idle_1", true, 0.0F);
            } else {
                this.state.addAnimation(0, "Idle_2", true, 0.0F);
            }
        }

        if (this.currentHealth <= 0 && !this.halfDead) {
            if ((AbstractDungeon.getCurrRoom()).cannotLose == true) {
                this.halfDead = true;
            }
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            addToTop((AbstractGameAction)new ClearCardQueueAction());

//            for (Iterator<AbstractPower> s = this.powers.iterator(); s.hasNext(); ) {
//                AbstractPower p = s.next();
//                if (p.type == AbstractPower.PowerType.DEBUFF || p.ID.equals("Curiosity") || p.ID.equals("Unawakened") || p.ID
//                        .equals("Shackled")) {
//                    s.remove();
//                }
//            }
            this.powers.clear();

            setMove((byte)3, AbstractMonster.Intent.UNKNOWN);
            createIntent();
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShoutAction((AbstractCreature)this, DIALOG[0]));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)3, AbstractMonster.Intent.UNKNOWN));
            applyPowers();
            this.firstTurn = true;
            this.form1 = false;


            if (GameActionManager.turn <= 1) {
                UnlockTracker.unlockAchievement("YOU_ARE_NOTHING");
            }
        }
    }


    public void update() {
        super.update();
        if (!this.isDying && this.animateParticles) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.1F;
                AbstractDungeon.effectList.add(new AwakenedEyeParticle(this.skeleton
                        .getX() + this.eye.getWorldX(), this.skeleton.getY() + this.eye.getWorldY()));
                this.wParticles.add(new AwakenedWingParticle());
            }
        }


        for (Iterator<AwakenedWingParticle> p = this.wParticles.iterator(); p.hasNext(); ) {
            AwakenedWingParticle e = p.next();
            e.update();
            if (e.isDone) {
                p.remove();
            }
        }
    }


    public void render(SpriteBatch sb) {
        for (AwakenedWingParticle p : this.wParticles) {
            if (p.renderBehind) {
                p.render(sb, this.skeleton.getX() + this.back.getWorldX(), this.skeleton.getY() + this.back.getWorldY());
            }
        }

        super.render(sb);

        for (AwakenedWingParticle p : this.wParticles) {
            if (!p.renderBehind) {
                p.render(sb, this.skeleton.getX() + this.back.getWorldX(), this.skeleton.getY() + this.back.getWorldY());
            }
        }
    }


    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
            boolean enemiesStillAlive=false;
            for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                if (!m.isDying) {
                    enemiesStillAlive=true;
                }
            }

            if(!enemiesStillAlive) {
                useFastShakeAnimation(5.0F);
                CardCrawlGame.screenShake.rumble(4.0F);
                if (this.saidPower) {
                    CardCrawlGame.sound.play("VO_AWAKENEDONE_2");
                    AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[1], false));

                    this.saidPower = true;
                }

                onBossVictoryLogic();
                UnlockTracker.hardUnlockOverride("CROW");
                UnlockTracker.unlockAchievement("CROW");
                onFinalBossVictoryLogic();
            }
        }
    }
}


