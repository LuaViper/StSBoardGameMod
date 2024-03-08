//use NoSkillsPower+VelvetChoker as a base for BGTimeWarpPower


package BoardGame.monsters.bgbeyond; 
 import BoardGame.characters.BGWatcher;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.cards.BGStatus.BGSlimed;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGInvinciblePlayerPower;
import BoardGame.powers.BGTimeEaterPhase2WarningPower;
import BoardGame.powers.BGTimeWarpPower;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

public class BGTimeEater extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TimeEater");
    public static final String ID = "BGTimeEater";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final int HP = 456;
    public static final int A_2_HP = 480;
    private static final byte REVERBERATE = 2;
    private static final byte RIPPLE = 3;
    private static final byte HEAD_SLAM = 4;
    private static final byte HASTE = 5;
    private static final int REVERB_DMG = 7;
    private static final int REVERB_AMT = 3;
    private static final int A_2_REVERB_DMG = 8;
    private static final int RIPPLE_BLOCK = 20;
    private static final int HEAD_SLAM_DMG = 26;
    private static final int A_2_HEAD_SLAM_DMG = 32;
    private int reverbDmg;
    private int headSlamDmg;
    private static final int HEAD_SLAM_STICKY = 1;
    private static final int RIPPLE_DEBUFF_TURNS = 1;
    private boolean usedHaste = false, firstTurn = true;
    private int turnCount=0;
    private boolean phase2=false;

    public BGTimeEater() {
        super(NAME, "BGTimeEater", 456, -10.0F, -30.0F, 476.0F, 410.0F, null, -50.0F, 30.0F);

//        if (AbstractDungeon.ascensionLevel >= 9) {
//            setHp(480);
//        } else {
//            setHp(456);
//        }

        loadAnimation("images/monsters/theForest/timeEater/skeleton.atlas", "images/monsters/theForest/timeEater/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.8F);

        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -200.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

//        if (AbstractDungeon.ascensionLevel >= 4) {
//            this.reverbDmg = 8;
//            this.headSlamDmg = 32;
//        } else {
//            this.reverbDmg = 7;
//            this.headSlamDmg = 26;
//        }

        setHp(60);

        this.reverbDmg=2;
        this.headSlamDmg=6;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.reverbDmg, DamageInfo.DamageType.NORMAL));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.headSlamDmg, DamageInfo.DamageType.NORMAL));
    }


    public void usePreBattleAction() {
        if(!AbstractDungeon.player.hasRelic("BGWhite Beast Statue")) {
            (AbstractDungeon.getCurrRoom()).rewardAllowed = false; //game is hardcoded to check for TheBeyond / TheEnding dungeons.  here's a workaround -- but mind this also interferes with White Beast Statue
        }
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BEYOND");
        UnlockTracker.markBossAsSeen("WIZARD");
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGTimeEaterPhase2WarningPower((AbstractCreature)this)));
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGTimeWarpPower((AbstractCreature)AbstractDungeon.player, 5), 5));
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGFooPower((AbstractCreature)AbstractDungeon.player, 5), 5));

    }

    public void takeTurn() {
        int i;
        if (this.firstTurn) {
            if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.WATCHER || AbstractDungeon.player instanceof BGWatcher) {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[2], 0.5F, 2.0F));
            } else {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[0], 0.5F, 2.0F));
            }
            this.firstTurn = false;
        }
        switch (this.nextMove) {
            case 0:
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.BLUE_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.75F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(0), AbstractGameAction.AttackEffect.FIRE));

                }
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGTimeWarpPower((AbstractCreature)AbstractDungeon.player, 4), 4));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGSlimed(), 3));
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGTimeWarpPower((AbstractCreature)AbstractDungeon.player, 3), 3));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.POISON));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGTimeWarpPower((AbstractCreature)AbstractDungeon.player, 5), 5));
                break;

        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void changeState(String stateName) {
        switch (stateName) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    protected void getMove(int num) {
        //to make things slightly more organized here, first turn is turn 0, not turn 1
        if(turnCount%3==0){
            setMove((byte)0,AbstractMonster.Intent.ATTACK,2,2,true);
        }else if(turnCount%3==1){
            setMove((byte)1,AbstractMonster.Intent.DEBUFF);
        }else if(turnCount%3==2){
            setMove((byte)2,AbstractMonster.Intent.ATTACK_BUFF,6);
        }

        turnCount+=1;

    }




    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            super.die();
            onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("WIZARD");
            UnlockTracker.unlockAchievement("TIME_EATER");
            onFinalBossVictoryLogic();
        }else{
            if(!phase2) {
                phase2 = true;

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ShoutAction((AbstractCreature) this, DIALOG[1], 2.0F, 3.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TextAboveCreatureAction((AbstractCreature)this, "Haste"));    //TODO: localization
                addToBot((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature) this, (AbstractCreature) this, "BGTimeEaterPhase2WarningPower"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new HealAction((AbstractCreature) this, (AbstractCreature) this, 30));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature)this,(AbstractCreature)this,"BGVulnerable"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature)this,(AbstractCreature)this,"BGWeakened"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new CanLoseAction());
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction((AbstractMonster) this, (byte) 4, Intent.BUFF));
                //setMove((byte) 4, AbstractMonster.Intent.BUFF);
            }
        }
    }
}


