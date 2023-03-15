package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.BoardGame;
import BoardGame.actions.DieMoveAction;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.GainBlockRandomMonsterAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGCenturion extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    public static final String ID = "BGCenturion";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Centurion");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES; private static final float IDLE_TIMESCALE = 0.8F; private static final int HP_MIN = 76; private static final int HP_MAX = 80; private static final int A_2_HP_MIN = 78; private static final int A_2_HP_MAX = 83; private static final int SLASH_DMG = 12; private static final int FURY_DMG = 6;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public String behavior="--";
    private static final int FURY_HITS = 3;
    private static final int A_2_SLASH_DMG = 14;
    private static final int A_2_FURY_DMG = 7;
    private int slashDmg;
    private boolean furymode=false;
    private int furyStr;
    private int blockAmount;
    private int BLOCK_AMOUNT = 15; private int A_17_BLOCK_AMOUNT = 20; private static final byte SLASH = 1; private static final byte PROTECT = 2;
    private static final byte FURY = 3;

    public BGCenturion(float x, float y, String behavior) {
        super(NAME, "BGCenturion", 80, -14.0F, -20.0F, 250.0F, 330.0F, null, x, y);

        this.behavior=behavior;

        setHp(15);

        this.blockAmount = 3;

        this.slashDmg = 3;
        this.furyStr = 1;
        this.furymode=false;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.slashDmg));


        loadAnimation("images/monsters/theCity/tank/skeleton.atlas", "images/monsters/theCity/tank/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.state.setTimeScale(0.8F);
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "MACE_HIT"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.25F));
                //in BG, always block for mystic
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)(AbstractDungeon.getMonsters()).monsters.get(1),this.blockAmount));
                break;
            case 3:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "MACE_HIT"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
        }


        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_TANK_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_TANK_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_TANK_1C"));
        }
    }


    public void changeState(String key) {
        switch (key) {

            case "MACE_HIT":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }




    protected void getMove(int num) {
            setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
    public void dieMove(int roll){
        final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
        logger.info("Centurion: "+this.furymode+", "+roll);
        if(this.furymode) {
            setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
            return;
        }else{

            char move = '-';
            if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2 || TheDie.monsterRoll == 3)
                move = this.behavior.charAt(0);
            else if (TheDie.monsterRoll == 4 || TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
                move = this.behavior.charAt(1);

            if (move == '3') {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            } else if (move == 'B') {
                setMove((byte) 2, AbstractMonster.Intent.DEFEND);
            }
        }
    }


    public void furyBuff(){
        //if(!this.furymode){
            if (!this.isDying && !this.isEscaping) {
                this.furymode = true;
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, "WHAT?!", false));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new StrengthPower((AbstractCreature) this, this.furyStr), this.furyStr));
                addToBot((AbstractGameAction)new DieMoveAction(this));

            }
        //}
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
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
        super.die();
    }


}


