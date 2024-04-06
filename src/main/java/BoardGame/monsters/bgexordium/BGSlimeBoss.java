package BoardGame.monsters.bgexordium;

import BoardGame.cards.BGStatus.BGSlimed;
import BoardGame.powers.BGSplitPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateJumpAction;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BGSlimeBoss
        extends AbstractBGMonster
{
    private static final Logger logger = LogManager.getLogger(BGSlimeBoss.class.getName());
    public static final String ID = "BGSlimeBoss";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlimeBoss");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final int HP = 140;

    public static final int A_2_HP = 150;

    public static final int TACKLE_DAMAGE = 9;

    public static final int SLAM_DAMAGE = 35;
    private int slimedAmt;
    public static final int A_2_TACKLE_DAMAGE = 10;
    public static final int A_2_SLAM_DAMAGE = 38;
    private static final String SLAM_NAME = MOVES[0], PREP_NAME = MOVES[1], SPLIT_NAME = MOVES[2]; private int tackleDmg; private int slamDmg; public static final int STICKY_TURNS = 3; private static final byte SLAM = 1; private static final byte PREP_SLAM = 2; private static final byte SPLIT = 3; private static final byte STICKY = 4;
    private static final String STICKY_NAME = MOVES[3];
    private boolean firstTurn = true;

    public BGSlimeBoss() {
        super(NAME, "BGSlimeBoss", 140, 0.0F, -30.0F, 400.0F, 350.0F, null, 0.0F, 28.0F);
        this.type = AbstractMonster.EnemyType.BOSS;

        this.dialogX = -150.0F * Settings.scale;
        this.dialogY = -70.0F * Settings.scale;

        setHp((AbstractDungeon.ascensionLevel<10) ? 22 : 23);

        slimedAmt=(AbstractDungeon.ascensionLevel<10) ? 3 : 4;
        this.tackleDmg = 3;
        this.slamDmg = 6;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.tackleDmg));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.slamDmg));
        this.powers.add(new BGSplitPower((AbstractCreature)this));

        loadAnimation("images/monsters/theBottom/boss/slime/skeleton.atlas", "images/monsters/theBottom/boss/slime/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        if (AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss) {
            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
        }

        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        UnlockTracker.markBossAsSeen("SLIME");
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1: //3 slimed
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_SLIME_ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGSlimed(), slimedAmt));

                //setMove(PREP_NAME, (byte)2, AbstractMonster.Intent.UNKNOWN);
                setMove("Tackle", (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
                break;
            case 99: // Slime... CRUSH (unused)
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShoutAction((AbstractCreature)this, DIALOG[0], 1.0F, 2.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShakeScreenAction(0.3F, ScreenShake.ShakeDur.LONG, ScreenShake.ShakeIntensity.LOW));

                setMove(SLAM_NAME, (byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                break;
            case 2: // medium attack + 2 slimed
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_SLIME_ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGSlimed(), 2));

                //"SLIME CRUSH"
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.8F));
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShoutAction((AbstractCreature)this, DIALOG[0], 1.0F, 2.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShakeScreenAction(0.3F, ScreenShake.ShakeDur.LONG, ScreenShake.ShakeIntensity.LOW));
                setMove(SLAM_NAME, (byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);


                break;
            case 3: // big attack
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateJumpAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new WeightyImpactEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, new Color(0.1F, 1.0F, 0.1F, 0.0F))));


                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.8F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.POISON));
                setMove(STICKY_NAME, (byte)1, AbstractMonster.Intent.STRONG_DEBUFF);
                break;
            case 4: // split
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new CannotLoseAction());
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateShakeAction((AbstractCreature)this, 1.0F, 0.1F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new HideHealthBarAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SuicideAction(this, false));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("SLIME_SPLIT"));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SpawnMonsterAction(new BGAcidSlime_L(-385.0F, 20.0F, true), false));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SpawnMonsterAction(new BGAcidSlime_M(-132.0F, 6.0F), false));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SpawnMonsterAction(new BGSpikeSlime_M(120.0F, -8.0F), false));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new CanLoseAction());
                setMove(SPLIT_NAME, (byte)4, AbstractMonster.Intent.UNKNOWN);
                break;
        }
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_SLIMEBOSS_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_SLIMEBOSS_1B"));
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);

        //if (!this.isDying && this.currentHealth <= this.maxHealth / 2.0F && this.nextMove != 3) {
        if(this.currentHealth<=0 && this.nextMove!=4){
            logger.info("SPLIT");
            setMove(SPLIT_NAME, (byte)4, AbstractMonster.Intent.UNKNOWN);
            createIntent();
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TextAboveCreatureAction((AbstractCreature)this, TextAboveCreatureAction.TextType.INTERRUPTED));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, SPLIT_NAME, (byte)4, AbstractMonster.Intent.UNKNOWN));
        }
    }


    protected void getMove(int num) {
        if (this.firstTurn) {
            this.firstTurn = false;
            setMove(STICKY_NAME, (byte)1, AbstractMonster.Intent.STRONG_DEBUFF);
            return;
        }
    }


    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
            CardCrawlGame.sound.play("VO_SLIMEBOSS_2A");

            for (AbstractGameAction a : AbstractDungeon.actionManager.actions) {
                if (a instanceof SpawnMonsterAction) {
                    return;
                }
            }

            if (this.currentHealth <= 0) {
                useFastShakeAnimation(5.0F);
                CardCrawlGame.screenShake.rumble(4.0F);
                onBossVictoryLogic();
                UnlockTracker.hardUnlockOverride("SLIME");
                UnlockTracker.unlockAchievement("SLIME_BOSS");
            }
        }
    }
}


