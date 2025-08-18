package BoardGame.monsters.bgbeyond;
 import BoardGame.cards.BGStatus.BGBurn;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.powers.BGIntangiblePower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
 import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
 import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.NemesisFireParticle;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

 import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.ascensionLevel;


public class BGNemesis extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Nemesis");
    public static final String ID = "BGNemesis";
    public static final String NAME = monsterStrings.NAME;
    private static final int HP = 185;
    private static final int A_2_HP = 200;
    private static final int SCYTHE_COOLDOWN_TURNS = 2;
    private static final float HB_X = 5.0F;
    private static final float HB_Y = -10.0F;
    private static final int SCYTHE_DMG = 45;
    private static final int FIRE_DMG = 6;
    private static final int FIRE_TIMES = 3;
    private static final int A_2_FIRE_DMG = 7;
    private static final int BURN_AMT = 3;
    private int fireDmg;
    private int scytheCooldown = 0; private static final byte TRI_ATTACK = 2;
    private static final byte SCYTHE = 3;
    private static final byte TRI_BURN = 4;
    private float fireTimer = 0.0F; private static final float FIRE_TIME = 0.05F; private Bone eye1;
    private Bone eye2;
    private Bone eye3;
    private boolean firstMove = true;
    private int burnCount=4;
    private int multiAmt=2;
    private int secondaryBurnCount=1;

    public BGNemesis() {
        super(NAME, "BGNemesis", 185, 5.0F, -10.0F, 350.0F, 440.0F, null, 0.0F, 0.0F);
        this.type = AbstractMonster.EnemyType.ELITE;

        loadAnimation("images/monsters/theForest/nemesis/skeleton.atlas", "images/monsters/theForest/nemesis/skeleton.json", 1.0F);


        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.8F);
        this.eye1 = this.skeleton.findBone("eye0");
        this.eye2 = this.skeleton.findBone("eye1");
        this.eye3 = this.skeleton.findBone("eye2");

//        if (AbstractDungeon.ascensionLevel >= 8) {
//            setHp(200);
//        } else {
//            setHp(185);
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 3) {
//            this.fireDmg = 7;
//        } else {
//            this.fireDmg = 6;
//        }

        if(ascensionLevel<1) {
            setHp(30);
            burnCount=4;secondaryBurnCount=1;multiAmt=2;
            this.damage.add(new DamageInfo((AbstractCreature) this, 4));
            this.damage.add(new DamageInfo((AbstractCreature) this, 2));
            this.damage.add(new DamageInfo((AbstractCreature) this, 7));
        } else if(ascensionLevel<12) {
            setHp(35);
            burnCount=5;secondaryBurnCount=1;multiAmt=2;
            this.damage.add(new DamageInfo((AbstractCreature) this, 5));
            this.damage.add(new DamageInfo((AbstractCreature) this, 2));
            this.damage.add(new DamageInfo((AbstractCreature) this, 8));
        } else {
            setHp(36);
            burnCount=5;secondaryBurnCount=2;multiAmt=3;
            this.damage.add(new DamageInfo((AbstractCreature) this, 6));
            this.damage.add(new DamageInfo((AbstractCreature) this, 2));
            this.damage.add(new DamageInfo((AbstractCreature) this, 8));
        }



    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_NEMESIS_1C"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 1.5F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGBurn(), burnCount));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base));
                break;

            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, this.damage.get(1).base,multiAmt,true));
                break;

            case 2:
                for (i = 0; i < multiAmt; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(1), AbstractGameAction.AttackEffect.FIRE));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGBurn(), secondaryBurnCount));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)3, AbstractMonster.Intent.ATTACK, this.damage.get(2).base));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, this.damage.get(1).base,multiAmt,true));
                break;

        }



        if (!hasPower("Intangible")) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGIntangiblePower((AbstractCreature)this, 0)));
        }


    }


    public void damage(DamageInfo info) {
        if (info.output > 0 && hasPower("BGIntangible")) {
            info.output = 0;
        }

        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            AnimationState.TrackEntry e = this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
            e.setTimeScale(0.8F);
        }
        super.damage(info);
    }

    public void changeState(String key) {
        AnimationState.TrackEntry e;
        switch (key) {
            case "ATTACK":
                e = this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                e.setTimeScale(0.8F);
                break;
        }
    }




    protected void getMove(int num) {
        this.scytheCooldown--;
        if (this.firstMove) {
            this.firstMove = false;

            setMove((byte)0, AbstractMonster.Intent.DEBUFF);
            return;
        }else{
            setMove((byte)255, AbstractMonster.Intent.NONE);
        }

    }



    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_NEMESIS_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_NEMESIS_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_NEMESIS_2A");
        } else {
            CardCrawlGame.sound.play("VO_NEMESIS_2B");
        }
    }


    public void die() {
        playDeathSfx();
        super.die();
    }


    public void update() {
        super.update();
        if (!this.isDying) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.05F;
                AbstractDungeon.effectList.add(new NemesisFireParticle(this.skeleton
                        .getX() + this.eye1.getWorldX(), this.skeleton.getY() + this.eye1.getWorldY()));
                AbstractDungeon.effectList.add(new NemesisFireParticle(this.skeleton
                        .getX() + this.eye2.getWorldX(), this.skeleton.getY() + this.eye2.getWorldY()));
                AbstractDungeon.effectList.add(new NemesisFireParticle(this.skeleton
                        .getX() + this.eye3.getWorldX(), this.skeleton.getY() + this.eye3.getWorldY()));
            }
        }
    }
}


