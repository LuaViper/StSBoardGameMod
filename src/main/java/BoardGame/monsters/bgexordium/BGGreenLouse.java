package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;

import BoardGame.BoardGame;
import BoardGame.dungeons.BGExordium;
import BoardGame.monsters.DieControlledMoves;
 
import BoardGame.powers.BGCurlUpPower;
import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
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
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.WebEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BGGreenLouse extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {
    final Logger logger = LogManager.getLogger(BoardGame.class.getName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FuzzyLouseDefensive"); public static final String ID = "FuzzyLouseDefensive";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 11; private static final int HP_MAX = 17;
    private static final int A_2_HP_MIN = 12;
    private static final int A_2_HP_MAX = 18;
    private static final byte BITE = 3;
    private static final byte WEAKEN = 4;
    private boolean isOpen = true;
    private static final String CLOSED_STATE = "CLOSED";
    private static final String OPEN_STATE = "OPEN";
    private static final String REAR_IDLE = "REAR_IDLE";
    private static final int WEAK_AMT = 2;

    public String behavior="---";

    public BGGreenLouse(float x, float y) {
        super(NAME, "FuzzyLouseDefensive", 17, 0.0F, -5.0F, 180.0F, 140.0F, null, x, y);

        loadAnimation("images/monsters/theBottom/louseGreen/skeleton.atlas", "images/monsters/theBottom/louseGreen/skeleton.json", 1.0F);

        behavior = BGExordium.getSummonGreenLouse();
        logger.info("BGGreenLouse: behavior "+behavior);

        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        setHp(3);

        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
    }



    public void usePreBattleAction() {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGCurlUpPower((AbstractCreature)this, 2)));


    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 3:
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "OPEN"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 4:
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "REAR"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(1.2F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("ATTACK_MAGIC_FAST_3",
                            MathUtils.random(0.88F, 0.92F), true));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new WebEffect((AbstractCreature)AbstractDungeon.player, this.hb.cX - 70.0F * Settings.scale, this.hb.cY + 10.0F * Settings.scale)));


                }
                else {


                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "REAR_IDLE"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.9F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("ATTACK_MAGIC_FAST_3",
                            MathUtils.random(0.88F, 0.92F), true));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new WebEffect((AbstractCreature)AbstractDungeon.player, this.hb.cX - 70.0F * Settings.scale, this.hb.cY + 10.0F * Settings.scale)));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                break;
            case 5:
                if (!this.isOpen) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "OPEN"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
        }







        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void changeState(String stateName) {
        if (stateName.equals("CLOSED")) {
            this.state.setAnimation(0, "transitiontoclosed", false);
            this.state.addAnimation(0, "idle closed", true, 0.0F);
            this.isOpen = false;
        } else if (stateName.equals("OPEN")) {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "idle", true, 0.0F);
            this.isOpen = true;
        } else if (stateName.equals("REAR_IDLE")) {
            this.state.setAnimation(0, "rear", false);
            this.state.addAnimation(0, "idle", true, 0.0F);
            this.isOpen = true;
        } else {
            this.state.setAnimation(0, "transitiontoopened", false);
            this.state.addAnimation(0, "rear", false, 0.0F);
            this.state.addAnimation(0, "idle", true, 0.0F);
            this.isOpen = true;
        }
    }

    public void dieMove(int roll){

        char move='-';
        if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)
            move=this.behavior.charAt(0);
        else if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4)
            move=this.behavior.charAt(1);
        else if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
            move=this.behavior.charAt(2);

        logger.info("BGGreenLouse: TheDie "+ TheDie.monsterRoll+" "+move);
        if(move=='W')
            setMove(MOVES[0], (byte)4, AbstractMonster.Intent.DEBUFF);
        else if(move=='1')
            setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
        else if(move=='2')
            setMove((byte)5, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);

    }

    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
//    protected void getMove(int num) {
//        if (AbstractDungeon.ascensionLevel >= 17) {
//            if (num < 25) {
//                if (lastMove((byte)4)) {
//                    setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
//                } else {
//                    setMove(MOVES[0], (byte)4, AbstractMonster.Intent.DEBUFF);
//                }
//
//            } else if (lastTwoMoves((byte)3)) {
//                setMove(MOVES[0], (byte)4, AbstractMonster.Intent.DEBUFF);
//            } else {
//                setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
//            }
//
//        }
//        else if (num < 25) {
//            if (lastTwoMoves((byte)4)) {
//                setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
//            } else {
//                setMove(MOVES[0], (byte)4, AbstractMonster.Intent.DEBUFF);
//            }
//
//        } else if (lastTwoMoves((byte)3)) {
//            setMove(MOVES[0], (byte)4, AbstractMonster.Intent.DEBUFF);
//        } else {
//            setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
//        }
//    }
}


