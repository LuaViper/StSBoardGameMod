package BoardGame.monsters.bgexordium;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.monsters.BGDamageIcons;

import BoardGame.BoardGame;
import BoardGame.dungeons.BGExordium;
import BoardGame.monsters.DieControlledMoves;
 
import BoardGame.powers.BGVulnerablePower;
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
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSpikeSlime_M extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SpikeSlime_M");
    public static final String ID = "BGSpikeSlime_M";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; public static final int HP_MIN = 28;
    public static final int HP_MAX = 32;
    public static final int A_2_HP_MIN = 29;
    public static final int A_2_HP_MAX = 34;
    public static final int TACKLE_DAMAGE = 8;
    public static final int WOUND_COUNT = 1;
    public static final int A_2_TACKLE_DAMAGE = 10;
    public static final int FRAIL_TURNS = 1;
    private static final byte FLAME_TACKLE = 1;
    private static final byte FRAIL_LICK = 4;
    private static final String FRAIL_NAME = MOVES[0];

    public String behavior="---";

    public BGSpikeSlime_M(float x, float y) {
        this(x, y, 0, 5);
    }

    public BGSpikeSlime_M(float x, float y, int poisonAmount, int newHealth) {
        super(NAME, "BGSpikeSlime_M", newHealth, 0.0F, -25.0F, 170.0F, 130.0F, null, x, y, true);

        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));

        behavior= BGExordium.getSummonSpikeSlime();

        loadAnimation("images/monsters/theBottom/slimeAltM/skeleton.atlas", "images/monsters/theBottom/slimeAltM/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGVulnerablePower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;

        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
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


        if(move=='V') {
            setMove("Lick", (byte) 4, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
            if(!this.halfDead && !this.isDying && !this.isEscaping)AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, "@BLORP!!@", false));
        }
        else if(move=='D')
            setMove("Corrosive Spit", (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
        else if(move=='2')
            setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(2)).base);

    }

    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

//    protected void getMove(int num) {
//        if (AbstractDungeon.ascensionLevel >= 17) {
//
//            if (num < 30) {
//                if (lastTwoMoves((byte)1)) {
//                    setMove(FRAIL_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//                } else {
//                    setMove((byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//                }
//
//            } else if (lastMove((byte)4)) {
//                setMove((byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//            } else {
//                setMove(FRAIL_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//
//            }
//
//        }
//        else if (num < 30) {
//            if (lastTwoMoves((byte)1)) {
//                setMove(FRAIL_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//            } else {
//                setMove((byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//            }
//
//        } else if (lastTwoMoves((byte)4)) {
//            setMove((byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
//        } else {
//            setMove(FRAIL_NAME, (byte)4, AbstractMonster.Intent.DEBUFF);
//        }
//    }




    public void die() {
        super.die();

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() &&
                AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss) {
            onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("SLIME");
            UnlockTracker.unlockAchievement("SLIME_BOSS");
        }
    }
}


