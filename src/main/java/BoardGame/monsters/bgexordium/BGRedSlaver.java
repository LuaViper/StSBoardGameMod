package BoardGame.monsters.bgexordium;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.monsters.BGDamageIcons;


import BoardGame.BoardGame;
import BoardGame.monsters.DieControlledMoves;
 
import BoardGame.powers.BGVulnerablePower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGRedSlaver extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlaverRed");
    public static final String ID = "BGRedSlaver";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 46;
    private static final int HP_MAX = 50;
    private static final int A_2_HP_MIN = 48;
    private static final int A_2_HP_MAX = 52;
    private static final int STAB_DMG = 13;
    private static final int A_2_STAB_DMG = 14;
    private static final int SCRAPE_DMG = 8;
    private static final int A_2_SCRAPE_DMG = 9;
    private int stabDmg;
    private int scrapeDmg;
    private int VULN_AMT = 1;
    private static final byte STAB = 1;
    private static final byte ENTANGLE = 2;
    private static final byte SCRAPE = 3;
    private static final String SCRAPE_NAME = MOVES[0];
    private static final String ENTANGLE_NAME = MOVES[1];
    private boolean usedEntangle = false, firstTurn = true;


    public BGRedSlaver(float x, float y){
        this(x,y,"DV3");
    }
    public BGRedSlaver(float x, float y, String behavior) {
        super(NAME, "BGRedSlaver", 50, 0.0F, 0.0F, 170.0F, 230.0F, null, x, y);

        setHp(10);
        this.behavior=behavior;

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));

        loadAnimation("images/monsters/theBottom/redSlaver/skeleton.atlas", "images/monsters/theBottom/redSlaver/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.firstTurn = true;
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 2:     // entangle (2dmg+daze)
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "Use Net"));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

                this.usedEntangle = true;
                break;
            case 1:     // 3dmg
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            case 3:     // scrape (2dmg+vuln)
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGVulnerablePower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                break;
        }








        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_SLAVERRED_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_SLAVERRED_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_SLAVERRED_2A");
        } else {
            CardCrawlGame.sound.play("VO_SLAVERRED_2B");
        }
    }


    public void changeState(String stateName) {
        float tmp = this.state.getCurrent(0).getTime();
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idleNoNet", true);
        e.setTime(tmp);
    }


    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

    public void dieMove(int num) {
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("Monster: TheDie "+ TheDie.monsterRoll);
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
            move = this.behavior.charAt(0);
        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
            move = this.behavior.charAt(1);
        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
            move = this.behavior.charAt(2);


        if (move=='D') {
            setMove(ENTANGLE_NAME, (byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        }else if (move == 'V') {
            if(!this.halfDead && !this.isDying && !this.isEscaping)AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, "@Heh@ @heh...@", false));
            setMove(SCRAPE_NAME, (byte)3, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);

        } else if (move=='3'){
            setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(2)).base);
        }

    }



    public void die() {
        super.die();
        playDeathSfx();
    }
}


