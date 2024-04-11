package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.dungeons.BGExordium;
import BoardGame.dungeons.BGTheCity;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGHealer extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    public static final String ID = "BGHealer";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Healer");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final float IDLE_TIMESCALE = 0.8F;

    public static final String ENC_NAME = "HealerTank";

    private static final int HP_MIN = 48;
    private static final int HP_MAX = 56;
    private static final int A_2_HP_MIN = 50;
    private static final int A_2_HP_MAX = 58;
    private static final int MAGIC_DMG = 8;
    private static final int HEAL_AMT = 16;
    private String behavior="---";

    public BGHealer(float x, float y) {
        super(NAME, "BGHealer", 56, 0.0F, -20.0F, 230.0F, 250.0F, null, x, y);

        this.behavior = BGTheCity.getSummonHealer();
        setHp(12);

        this.magicDmg = 2;
        this.strAmt = 1;
        this.healAmt = 3;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.magicDmg));

        loadAnimation("images/monsters/theCity/healer/skeleton.atlas", "images/monsters/theCity/healer/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Hit", "Idle", 0.2F);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.setTimeScale(0.8F);
    }
    private static final int STR_AMOUNT = 2; private static final int A_2_MAGIC_DMG = 9; private static final int A_2_STR_AMOUNT = 3; private int magicDmg; private int strAmt; private int healAmt; private static final byte ATTACK = 1; private static final byte HEAL = 2; private static final byte BUFF = 3;

    public void takeTurn() {
        switch (this.nextMove) {
            case 1: //attack + weak
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                break;

            case 2: //heal all
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "STAFF_RAISE"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.25F));
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new HealAction((AbstractCreature)m, (AbstractCreature)this, this.healAmt));
                    }
                }
                break;
            case 3: //str all
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "STAFF_RAISE"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.25F));
                //TODO: are there any act 2 monsters that summon?  use BuffAllEnemiesAction instead
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)m, this.strAmt), this.strAmt));
                    }
                }
                break;
        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }

    private void playSfx() {
        if (MathUtils.randomBoolean()) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_HEALER_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_HEALER_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_HEALER_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_HEALER_2B");
        } else {
            CardCrawlGame.sound.play("VO_HEALER_2C");
        }
    }


    public void changeState(String key) {
        switch (key) {
            case "STAFF_RAISE":
                this.state.setAnimation(0, "Attack", false);
                this.state.setTimeScale(0.8F);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }




    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
    public void dieMove(int roll){
        final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
            move = this.behavior.charAt(0);
        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
            move = this.behavior.charAt(1);
        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
            move = this.behavior.charAt(2);

        if (move == '2') {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        } else if (move == 'H') {
            setMove((byte) 2, AbstractMonster.Intent.DEFEND_BUFF);
        } else if (move == 'S') {
            setMove((byte) 3, AbstractMonster.Intent.BUFF);
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


    public void die() {
        int aliveCount = 0;
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (!m.isDying && !m.isEscaping && m instanceof BGCenturion) {
                ((BGCenturion)m).furyBuff();
            }
        }
        playDeathSfx();
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
        super.die();
    }
}


