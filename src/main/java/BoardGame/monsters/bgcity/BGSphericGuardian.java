package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.FrailPower;

public class BGSphericGuardian extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGSphericGuardian";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SphericGuardian");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final float IDLE_TIMESCALE = 0.8F;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 280.0F;
    private static final float HB_H = 280.0F;
    private static final int DMG = 10;
    private static final int A_2_DMG = 11;
    private int dmg;
    private static final int SLAM_AMT = 2;

    public BGSphericGuardian() {
        this(0.0F, 0.0F);
    }
    private static final int HARDEN_BLOCK = 15; private static final int FRAIL_AMT = 5; private static final int ACTIVATE_BLOCK = 25; private static final int ARTIFACT_AMT = 3; private static final int STARTING_BLOCK_AMT = 40; private static final byte BIG_ATTACK = 1; private static final byte INITIAL_BLOCK_GAIN = 2; private static final byte BLOCK_ATTACK = 3; private static final byte FRAIL_ATTACK = 4; private boolean firstMove = true, secondMove = true;
    public BGSphericGuardian(float x, float y) {
        super(NAME, "BGSphericGuardian", 20, 0.0F, 10.0F, 280.0F, 280.0F, null, x, y);

        setHp(5);

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 5));

        loadAnimation("images/monsters/theCity/sphere/skeleton.atlas", "images/monsters/theCity/sphere/skeleton.json", 1.0F);




        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.stateData.setMix("Idle", "Attack", 0.1F);
        this.state.setTimeScale(0.8F);
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BarricadePower((AbstractCreature)this)));

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, 10));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this,5));
                setMove((byte) 2, AbstractMonster.Intent.ATTACK, 5);
                break;
            case 2:

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEFEND, 2);
                break;


        }

    }


    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.setTimeScale(0.8F);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }




    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.setTimeScale(0.8F);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    protected void getMove(int num) {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEFEND, 2);
    }


    public void die() {
        super.die();
        if (MathUtils.randomBoolean()) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("SPHERE_DETECT_VO_1"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("SPHERE_DETECT_VO_2"));
        }
    }
}


