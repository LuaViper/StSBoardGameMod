package BoardGame.monsters.bgexordium;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.cards.BGStatus.BGSlimed;
import BoardGame.monsters.BGDamageIcons;

import BoardGame.BoardGame;
import BoardGame.dungeons.BGExordium;
import BoardGame.monsters.DieControlledMoves;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGAcidSlime_L
        extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves  {
    public static final String ID = "BGAcidSlime_L";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("AcidSlime_L");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final String WOUND_NAME = MOVES[0]; private static final String SPLIT_NAME = MOVES[1]; private static final String WEAK_NAME = MOVES[2];

    public static final int HP_MIN = 65;

    public static final int HP_MAX = 69;
    public static final int A_2_HP_MIN = 68;
    public static final int A_2_HP_MAX = 72;
    public static final int W_TACKLE_DMG = 11;
    public static final int N_TACKLE_DMG = 16;
    public static final int A_2_W_TACKLE_DMG = 12;
    public static final int A_2_N_TACKLE_DMG = 18;
    public static final float[] POSX = new float[] { -240.0F, -80.0F, 90.0F, 280.0F };
    public static final float[] POSY = new float[] { -15.0F, 0.0F, -7.5F, -7.5F };
    public String behavior="---";
    public boolean firstMove=true;
    public boolean hard;
    private int dazeAmount=1;
    private AbstractMonster[] mediumslimes = new AbstractMonster[4];

    public static final int WEAK_TURNS = 2; public static final int WOUND_COUNT = 2; private static final byte SLIME_TACKLE = 1; private static final byte NORMAL_TACKLE = 2; private static final byte SPLIT = 3; private static final byte WEAK_LICK = 4; private float saveX; private float saveY; private boolean splitTriggered;
    public BGAcidSlime_L(float x, float y, boolean hard) {
        super(NAME, "BGAcidSlime_L", 12, 0.0F, 0.0F, 300.0F, 180.0F, null, x, y, true);
        this.hard=hard;
        this.saveX = x;
        this.saveY = y;
        this.splitTriggered = false;

        if(hard){
            this.behavior= BGExordium.getSummonLargeSlime();
            setHp(12);
            if(AbstractDungeon.ascensionLevel>=10){
                addToBot(new ApplyPowerAction(this,this,new StrengthPower(this,1),1));
            }
        }else{
            //this.behavior="4XD";
            this.behavior="----";
            if(AbstractDungeon.ascensionLevel<7){
                setHp(8);
            }else{
                setHp(9);
            }
        }

        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 4));
        if(AbstractDungeon.ascensionLevel<7){
            this.damage.add(new DamageInfo(this, 1));
            dazeAmount=1;
        }else{
            this.damage.add(new DamageInfo(this, 2));
            dazeAmount=2;
        }



        loadAnimation("images/monsters/theBottom/slimeL/skeleton.atlas", "images/monsters/theBottom/slimeL/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.addListener((AnimationState.AnimationStateListener)new SlimeAnimListener());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1: //2x slime, 3 damage
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("MONSTER_SLIME_ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new MakeTempCardInDiscardAction((AbstractCard) new BGSlimed(), 2));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
                break;
            case 2: //4 damage
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                        .get(3), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
                break;
            case 3: //summon
            {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new AnimateShakeAction((AbstractCreature) this, 1.0F, 0.1F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("SLIME_SPLIT"));
                int slimesSpawned = 0;
                int slimesPerSpawn = 1;
                for (int i = 0; slimesSpawned < slimesPerSpawn && i < this.mediumslimes.length; i++) {
                    if (this.mediumslimes[i] == null || this.mediumslimes[i].isDeadOrEscaped()) {
                        BGAcidSlime_M slimeToSpawn = new BGAcidSlime_M(POSX[i], POSY[i]);
                        this.mediumslimes[i] = slimeToSpawn;
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SpawnMonsterAction(slimeToSpawn, false));
                        slimesSpawned++;
                    }
                }
                if (slimesSpawned == 0) {
                    AbstractDungeon.effectList.add(new ThoughtBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, "Why are you still here?", false));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
                break;
            }
            case 4: //2x weak, 1 damage
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 5: //2x daze, 2 damage
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_SLIME_ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 2, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 6: //AOE 1 damage (A7 2 damage)
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(4), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,WOUND_NAME,(byte)7,Intent.ATTACK_DEBUFF,damage.get(3).base));
                break;
            case 7: //4 damage, 1 daze (A7 2 daze)
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_SLIME_ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(3), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), dazeAmount, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,"Summon",(byte)8,Intent.UNKNOWN));
                break;
            case 8: //summon
            {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new AnimateShakeAction((AbstractCreature) this, 1.0F, 0.1F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SFXAction("SLIME_SPLIT"));
                int slimesSpawned = 0;
                int slimesPerSpawn = 1;
                for (int i = 0; slimesSpawned < slimesPerSpawn && i < this.mediumslimes.length; i++) {
                    if (this.mediumslimes[i] == null || this.mediumslimes[i].isDeadOrEscaped()) {
                        BGAcidSlime_M slimeToSpawn = new BGAcidSlime_M(POSX[i], POSY[i]);
                        this.mediumslimes[i] = slimeToSpawn;
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SpawnMonsterAction(slimeToSpawn, false));
                        slimesSpawned++;
                    }
                }
                if (slimesSpawned == 0) {
                    AbstractDungeon.effectList.add(new ThoughtBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, "Why are you still here?", false));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,"Splash",(byte)6,Intent.ATTACK,damage.get(4).base));
                break;
            }
        }
    }




    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
    public void dieMove(int roll){
        if(this.behavior.equals("----")){
            if(firstMove) {
                firstMove=false;
                setMove("Splash", (byte) 6, Intent.ATTACK, damage.get(4).base);
            }
            return;
        }

        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGAcidSlime_M: TheDie "+ TheDie.monsterRoll);
        char move='-';
        if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)
            move=this.behavior.charAt(0);
        else if(TheDie.monsterRoll==3 || TheDie.monsterRoll==4)
            move=this.behavior.charAt(1);
        else if(TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
            move=this.behavior.charAt(2);

        if(move=='4')
            setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(3)).base);
        else if(move=='X')
            setMove("Summon", (byte)3, AbstractMonster.Intent.UNKNOWN);
        else if(move=='S')
            setMove(WOUND_NAME, (byte)1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(2)).base);
        else if(move=='D')
            setMove(WOUND_NAME, (byte)5, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
        else if(move=='W')
            setMove(WEAK_NAME, (byte)4, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);


    }




    public void die() {
        super.die();

        for (AbstractGameAction a : AbstractDungeon.actionManager.actions) {
            if (a instanceof SpawnMonsterAction) {
                return;
            }
        }

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() &&
                AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss) {
            onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("SLIME");
            UnlockTracker.unlockAchievement("SLIME_BOSS");
        }
    }
}


