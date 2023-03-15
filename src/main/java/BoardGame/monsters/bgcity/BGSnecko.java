


package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGConfusionPower;
import BoardGame.powers.BGFlightPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
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
import com.megacrit.cardcrawl.powers.ConfusionPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import com.megacrit.cardcrawl.vfx.combat.IntimidateEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSnecko extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Snecko");
    public static final String ID = "BGSnecko";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final byte GLARE = 1; private static final byte BITE = 2; private static final byte TAIL = 3;
    private static final int BITE_DAMAGE = 15;
    private static final int TAIL_DAMAGE = 8;
    private static final int A_2_BITE_DAMAGE = 18;
    private static final int A_2_TAIL_DAMAGE = 10;
    private int biteDmg;
    private int tailDmg;
    private static final int VULNERABLE_AMT = 2;
    private static final int HP_MIN = 114;
    private static final int HP_MAX = 120;
    private static final int A_2_HP_MIN = 120;
    private static final int A_2_HP_MAX = 125;
    private boolean firstTurn = true;

    public BGSnecko() {
        this(0.0F, 0.0F);
    }

    public BGSnecko(float x, float y) {
        super(NAME, "BGSnecko", 120, -30.0F, -20.0F, 310.0F, 305.0F, null, x, y);
        loadAnimation("images/monsters/theCity/reptile/skeleton.atlas", "images/monsters/theCity/reptile/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.8F);

        setHp(23);


        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 4));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_SNECKO_GLARE"));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new IntimidateEffect(this.hb.cX, this.hb.cY), 0.5F));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new FastShakeAction((AbstractCreature)AbstractDungeon.player, 1.0F, 1.0F));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGConfusionPower((AbstractCreature)AbstractDungeon.player,-1),-1));

    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +

                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                        .cpy()), 0.3F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +

                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                        .cpy()), 0.3F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.NONE));
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +

                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                        .cpy()), 0.3F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.NONE));
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
            case "ATTACK_2":
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


    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
    public void dieMove(int roll) {
        final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
            move = '3';
        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
            move = '4';
        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
            move = '2';

        if (move == '3') {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) this, (AbstractPower) new BGConfusionPower((AbstractCreature) AbstractDungeon.player, 2), 2));
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else if (move == '4') {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) this, (AbstractPower) new BGConfusionPower((AbstractCreature) AbstractDungeon.player, 1), 1));
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
        } else if (move == '2') {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) AbstractDungeon.player, (AbstractCreature) this, (AbstractPower) new BGConfusionPower((AbstractCreature) AbstractDungeon.player, 3), 3));
            setMove((byte) 3, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(2)).base);
        }

//        if (AbstractDungeon.player.hasPower("BGConfusion")) {
//            AbstractPower p = AbstractDungeon.player.getPower("BGConfusion");
//            logger.info("Snecko update desc?",p.amount);    //doesn't work here -- amount hasn't been updated yet
//            p.updateDescription();
//        }
    }

    public void die() {
        super.die();
        CardCrawlGame.sound.play("SNECKO_DEATH");
    }
}


