package BoardGame.monsters.bgcity;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
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
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import com.megacrit.cardcrawl.vfx.combat.LaserBeamEffect;

public class BGBronzeAutomaton extends AbstractBGMonster {
    public static final String ID = "BGBronzeAutomaton";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BronzeAutomaton");
    public static final String NAME = monsterStrings.NAME;
    private static final String[] MOVES = monsterStrings.MOVES; private static final int HP = 300; private static final int A_2_HP = 320;
    private static final byte FLAIL = 1;
    private static final byte HYPER_BEAM = 2;
    private static final byte STUNNED = 3;
    private static final byte SPAWN_ORBS = 4;
    private static final byte BOOST = 5;
    private static final String BEAM_NAME = MOVES[0];

    private static final int FLAIL_DMG = 7;

    private static final int BEAM_DMG = 45;

    private static final int A_2_FLAIL_DMG = 8;
    private static final int A_2_BEAM_DMG = 50;
    private int flailDmg;
    private int beamDmg;
    private int numTurns = 0; private static final int BLOCK_AMT = 9; private static final int STR_AMT = 3; private static final int A_2_BLOCK_AMT = 12; private static final int A_2_STR_AMT = 4; private int strAmt; private int blockAmt;
    private boolean firstTurn = true;

    public BGBronzeAutomaton() {
        super(NAME, "BGBronzeAutomaton", 300, 0.0F, -30.0F, 270.0F, 400.0F, null, -50.0F, 20.0F);
        loadAnimation("images/monsters/theCity/automaton/skeleton.atlas", "images/monsters/theCity/automaton/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

        setHp(55);
        this.blockAmt = 0;
        this.flailDmg = 1;
        this.beamDmg = 8;
        this.strAmt = 1;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.flailDmg));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.beamDmg));
    }


    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");

        UnlockTracker.markBossAsSeen("AUTOMATON");

        if (MathUtils.randomBoolean()) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("AUTOMATON_ORB_SPAWN",
                    MathUtils.random(-0.1F, 0.1F)));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_AUTOMATON_SUMMON",
                    MathUtils.random(-0.1F, 0.1F)));
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SpawnMonsterAction(new BGBronzeOrb(-300.0F, 200.0F, 0), false));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.strAmt), this.strAmt));
                setMove((byte)2, AbstractMonster.Intent.ATTACK, 1, 2, true);
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                setMove((byte)3, AbstractMonster.Intent.BUFF);
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.blockAmt));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.strAmt), this.strAmt));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this,"BGWeakened"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this,"BGVulnerable"));
                setMove(BEAM_NAME, (byte)4, AbstractMonster.Intent.ATTACK, 8);
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new LaserBeamEffect(this.hb.cX, this.hb.cY + 60.0F * Settings.scale), 1.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.NONE));
                setMove((byte)1, AbstractMonster.Intent.BUFF);
                break;
        }

    }


    protected void getMove(int num) {
        setMove((byte)1, AbstractMonster.Intent.BUFF);

    }


    public void die() {
        super.die();

        boolean done=true;
        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                done=false;
            }
        }
        if(done) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("AUTOMATON");
            UnlockTracker.unlockAchievement("AUTOMATON");
        }
    }
}


