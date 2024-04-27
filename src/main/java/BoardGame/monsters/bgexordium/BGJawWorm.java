package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;

import BoardGame.BoardGame;
import BoardGame.monsters.DieControlledMoves;
 
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.animations.SetAnimationAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGJawWorm extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {
    public static final String ID = "BGJawWorm";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("JawWorm");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 8;

    private static final int HP_MAX = 8;

    private static final int A_2_HP_MIN = 10;

    private static final int A_2_HP_MAX = 10;

    private static final float HB_X = 0.0F;

    private static final float HB_Y = -25.0F;

    private static final float HB_W = 260.0F;

    private static final float HB_H = 170.0F;
    private static final int CHOMP_DMG = 3;
    private static final int A_2_CHOMP_DMG = 4;
    private static final int THRASH_DMG = 2;
    private static final int THRASH_BLOCK = 1;
    private static final int A_2_THRASH_DMG = 3;
    private static final int A_2_THRASH_BLOCK = 1;
    private static final int BELLOW_STR = 1;
    private static final int BELLOW_BLOCK = 2;
    private static final int A_2_BELLOW_BLOCK = 3;

    private int bellowBlock; private int chompDmg; private int thrashDmg; private int thrashBlock;
    private int bellowStr; private static final byte CHOMP = 1; private static final byte BELLOW = 2;
    private static final byte THRASH = 3; private boolean firstMove = true;
    private int difficulty;

    public BGJawWorm(float x, float y, int difficulty, String behavior) {
        super(NAME, "BGJawWorm", 44, 0.0F, -25.0F, 260.0F, 170.0F, null, x, y);

        this.difficulty = difficulty;

        if(this.difficulty==0)
            setHp(8);
        else if(this.difficulty==3)
            setHp(7);
        else
            setHp(10);

        if(this.difficulty==0) {
            this.behavior="sda";
            this.bellowStr = 1;
            this.bellowBlock = 2;
            this.chompDmg = 3;
            this.thrashDmg = 2;
            this.thrashBlock = 1;
        }else if(this.difficulty==1){
            this.behavior="DAs";
            this.bellowStr = 1;
            this.bellowBlock = 2;
            this.chompDmg = 4;
            this.thrashDmg = 3;
            this.thrashBlock = 1;
        }else if(this.difficulty==2){
            this.behavior=behavior;
            this.bellowStr = 1;
            this.bellowBlock = 3;
            this.chompDmg = 4;
            this.thrashDmg = 3;
            this.thrashBlock = 1;
        }else if(this.difficulty==3){
            this.bellowStr = 1;
            this.bellowBlock = 2;
            this.chompDmg = 3;
            this.thrashDmg = 2;
            this.thrashBlock = 1;
            this.behavior="dsa";
        }

        this.damage.add(new DamageInfo((AbstractCreature)this, this.chompDmg));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.thrashDmg));

        loadAnimation("images/monsters/theBottom/jawWorm/skeleton.atlas", "images/monsters/theBottom/jawWorm/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }



//    public void usePreBattleAction() {
//        if (this.hardMode) {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.bellowStr), this.bellowStr));
//
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.bellowBlock));
//        }
//    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetAnimationAction((AbstractCreature)this, "chomp"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.3F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                break;
            case 2:
                this.state.setAnimation(0, "tailslam", false);
                this.state.addAnimation(0, "idle", true, 0.0F);
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_JAW_WORM_BELLOW"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShakeScreenAction(0.2F, ScreenShake.ShakeDur.SHORT, ScreenShake.ShakeIntensity.MED));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.bellowStr), this.bellowStr));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.bellowBlock));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateHopAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.thrashBlock));
                break;
        }

        //TODO: not 100% sure whether we should RollMoveAction here (sets intention to BLANK after acting) or skip (causes intention to suddenly change upon next die roll)
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void dieMove(int roll){
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGJawWorm: TheDie "+TheDie.monsterRoll);
        char move='-';
        if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)
            move=this.behavior.charAt(0);
        else if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4)
            move=this.behavior.charAt(1);
        else if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
            move=this.behavior.charAt(2);

        if(move=='S' || move=='s'){
            setMove(MOVES[0], (byte)2, AbstractMonster.Intent.DEFEND_BUFF);
        }else if(move=='D' || move=='d'){
            setMove((byte)3, AbstractMonster.Intent.ATTACK_DEFEND, ((DamageInfo)this.damage.get(1)).base);
        }else if(move=='A' || move=='a'){
            setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
        }

    }


    protected void getMove(int num) {
        setMove((byte)0, AbstractMonster.Intent.NONE);
    }



    public void die() {
        super.die();
        CardCrawlGame.sound.play("JAW_WORM_DEATH");
    }
}


