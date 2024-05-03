package BoardGame.monsters.bgexordium;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.monsters.BGDamageIcons;
import BoardGame.BoardGame;
import BoardGame.dungeons.BGExordium;
import BoardGame.monsters.DieControlledMoves;
 
import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGAcidSlime_M extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {

    public static final String ID = "BGAcidSlime_M";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("AcidSlime_M");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    //C/L/A
    private static final String WOUND_NAME = MOVES[0]; private static final String WEAK_NAME = MOVES[1];

    public static final int HP_MIN = 28;

    public static final int HP_MAX = 32;
    public static final int A_2_HP_MIN = 29;
    public static final int A_2_HP_MAX = 34;
    public static final int W_TACKLE_DMG = 7;
    public static final int WOUND_COUNT = 1;


    public BGAcidSlime_M(float x, float y) {
        this(x, y, 0, 5);
    }
    public static final int N_TACKLE_DMG = 10; public static final int A_2_W_TACKLE_DMG = 8; public static final int A_2_N_TACKLE_DMG = 12; public static final int WEAK_TURNS = 1; private static final byte WOUND_TACKLE = 1; private static final byte NORMAL_TACKLE = 2; private static final byte WEAK_LICK = 4;
    public BGAcidSlime_M(float x, float y, int poisonAmount, int newHealth) {
        super(NAME, "BGAcidSlime_M", newHealth, 0.0F, 0.0F, 170.0F, 130.0F, null, x, y, true);

        behavior = BGExordium.getSummonAcidSlime();

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        setHp(5,5);

        if (poisonAmount >= 1) {
            this.powers.add(new PoisonPower((AbstractCreature)this, (AbstractCreature)this, poisonAmount));
        }

        loadAnimation("images/monsters/theBottom/slimeM/skeleton.atlas", "images/monsters/theBottom/slimeM/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.addListener((AnimationState.AnimationStateListener)new SlimeAnimListener());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));





                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new Slimed(), 1));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
        }
    }

    public void dieMove(int roll){
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGAcidSlime_M: TheDie "+ TheDie.monsterRoll);
        char move='-';
        if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)
            move=this.behavior.charAt(0);
        else if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4)
            move=this.behavior.charAt(1);
        else if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
            move=this.behavior.charAt(2);


        if(move=='C')
            setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
        else if(move=='L')
            setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
        else if(move=='A')
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);

    }

    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

//    protected void getMove(int num) {
//        if (AbstractDungeon.ascensionLevel >= 17) {
//
//            if (num < 40) {
//                if (lastTwoMoves((byte)1)) {
//                    if (AbstractDungeon.aiRng.randomBoolean()) {
//                        setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
//                    } else {
//                        setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//                    }
//                } else {
//                    setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//                }
//
//            }
//            else if (num < 80) {
//                if (lastTwoMoves((byte)2)) {
//                    if (AbstractDungeon.aiRng.randomBoolean(0.5F)) {
//                        setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//                    } else {
//                        setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//                    }
//                } else {
//                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
//
//                }
//
//            }
//            else if (lastMove((byte)4)) {
//                if (AbstractDungeon.aiRng.randomBoolean(0.4F)) {
//                    setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//                } else {
//                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
//                }
//            } else {
//                setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//
//            }
//
//        }
//        else if (num < 30) {
//            if (lastTwoMoves((byte)1)) {
//                if (AbstractDungeon.aiRng.randomBoolean()) {
//                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
//                } else {
//                    setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//                }
//            } else {
//                setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//            }
//
//        }
//        else if (num < 70) {
//            if (lastMove((byte)2)) {
//                if (AbstractDungeon.aiRng.randomBoolean(0.4F)) {
//                    setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//                } else {
//                    setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//                }
//            } else {
//                setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
//
//            }
//
//        }
//        else if (lastTwoMoves((byte)4)) {
//            if (AbstractDungeon.aiRng.randomBoolean(0.4F)) {
//                setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//            } else {
//                setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
//            }
//        } else {
//            setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//        }
//    }




    public void die() {
        super.die();

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() &&
                AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss) {
            onBossVictoryLogic();
            //UnlockTracker.hardUnlockOverride("SLIME");
            //UnlockTracker.unlockAchievement("SLIME_BOSS");
        }
    }
}


