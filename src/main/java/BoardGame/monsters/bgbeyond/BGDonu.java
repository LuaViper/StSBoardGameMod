package BoardGame.monsters.bgbeyond;
import BoardGame.monsters.DieControlledMoves;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGDonu extends AbstractBGMonster {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Donu");
    public static final String ID = "BGDonu";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final int HP = 250;
    public static final int A_2_HP = 265;
    private static final byte BEAM = 0;
    private static final byte CIRCLE_OF_PROTECTION = 2;
    private static final int ARTIFACT_AMT = 2;
    private static final int BEAM_DMG = 10;
    private static final int BEAM_AMT = 2;
    private static final int A_2_BEAM_DMG = 12;
    private int beamDmg;
    private static final String CIRCLE_NAME = MOVES[0];

    private static final int CIRCLE_STR_AMT = 3;
    private boolean isAttacking;

    public BGDonu() {
        super(NAME, "BGDonu", 250, 0.0F, -20.0F, 390.0F, 390.0F, null, 100.0F, 20.0F);
        loadAnimation("images/monsters/theForest/donu/skeleton.atlas", "images/monsters/theForest/donu/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        this.stateData.setMix("Attack_2", "Idle", 0.1F);

        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -200.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

//        if (AbstractDungeon.ascensionLevel >= 9) {
//            setHp(265);
//        } else {
//            setHp(250);
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 4) {
//            this.beamDmg = 12;
//        } else {
//            this.beamDmg = 10;
//        }

        setHp(50);
        this.beamDmg=3;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.beamDmg));
        this.isAttacking = false;
    }


    public void changeState(String stateName) {
        switch (stateName) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack_2", false);
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


//    public void usePreBattleAction() {
//        if (AbstractDungeon.ascensionLevel >= 19) {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new ArtifactPower((AbstractCreature)this, 3)));
//        } else {
//
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new ArtifactPower((AbstractCreature)this, 2)));
//        }
//    }


    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                for (i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(0), AbstractGameAction.AttackEffect.FIRE));
                }
                this.isAttacking = false;
                break;
            case 2:
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_DONU_DEFENSE"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)m, 1), 1));
                }

                this.isAttacking = true;
                break;
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.isAttacking) {
            setMove((byte)0, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base, 2, true);
        } else {
            setMove(CIRCLE_NAME, (byte)2, AbstractMonster.Intent.BUFF);
        }
    }


    public void die() {
        super.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("DONUT");
            UnlockTracker.unlockAchievement("SHAPES");
            onFinalBossVictoryLogic();
        }
    }
}


