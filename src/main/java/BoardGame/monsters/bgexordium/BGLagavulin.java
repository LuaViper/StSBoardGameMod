package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;


import BoardGame.powers.BGWeakPower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
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
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGLagavulin extends AbstractBGMonster implements BGDamageIcons {
    private static final Logger logger = LogManager.getLogger(BGLagavulin.class.getName());
    public static final String ID = "BGLagavulin";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Lagavulin");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES; private static final int HP_MIN = 109; private static final int HP_MAX = 111; private static final int A_2_HP_MIN = 112; private static final int A_2_HP_MAX = 115; private static final byte DEBUFF = 1;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte STRONG_ATK = 3;
    private static final byte OPEN = 4;
    private static final byte IDLE = 5;
    private static final byte OPEN_NATURAL = 6;
    private static final String DEBUFF_NAME = MOVES[0]; private static final int STRONG_ATK_DMG = 18; private static final int DEBUFF_AMT = -1; private static final int A_18_DEBUFF_AMT = -2; private static final int A_2_STRONG_ATK_DMG = 20;
    private int attackDmg;
    private int debuff;
    private static final int ARMOR_AMT = 8;
    private boolean isOut = false;
    private boolean asleep;
    private boolean isOutTriggered = false;
    private int idleCount = 0, debuffTurnCount = 0;

    public BGLagavulin() {
        super(NAME, "BGLagavulin", 111, 0.0F, -25.0F, 320.0F, 220.0F, null, 0.0F, 20.0F);
        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -100.0F * Settings.scale;

        setHp(22);

        this.attackDmg = 4;

        if (AbstractDungeon.ascensionLevel >= 18) {
            this.debuff = -2;
        } else {
            this.debuff = -1;
        }

        this.damage.add(new DamageInfo((AbstractCreature)this, this.attackDmg));
        if(AbstractDungeon.ascensionLevel==0) {
            this.asleep = true;
        }else{
            this.asleep = false;
        }

        loadAnimation("images/monsters/theBottom/lagavulin/skeleton.atlas", "images/monsters/theBottom/lagavulin/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = null;
        if (!this.asleep) {
            this.isOut = true;
            this.isOutTriggered = true;
            e = this.state.setAnimation(0, "Idle_2", true);
            updateHitbox(0.0F, -25.0F, 320.0F, 370.0F);
        } else {
            e = this.state.setAnimation(0, "Idle_1", true);
        }
        this.stateData.setMix("Attack", "Idle_2", 0.25F);
        this.stateData.setMix("Hit", "Idle_2", 0.25F);
        this.stateData.setMix("Idle_1", "Idle_2", 0.5F);

        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        if(AbstractDungeon.ascensionLevel==0) {
            CardCrawlGame.music.precacheTempBgm("ELITE");
            //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, 8));
            //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new MetallicizePower((AbstractCreature)this, 8), 8));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[2], 0.5F, 2.0F));
        } else {

            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            CardCrawlGame.music.playTempBgmInstantly("ELITE");
            //logger.info("DEBUFF_NAME is "+DEBUFF_NAME);
            setMove(DEBUFF_NAME, (byte)7, AbstractMonster.Intent.STRONG_DEBUFF);
        }
    }


    public void takeTurn() {
        switch (this.nextMove) {


            case 1: //Siphon Soul
                this.debuffTurnCount = 0;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "DEBUFF"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 2, true), 2));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower(this, 1), 1));





                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 3:
                this.debuffTurnCount++;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 5: //Sleep
                this.idleCount++;
                if (true) {
                    logger.info("idle happened");
                    this.isOutTriggered = true;
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "OPEN"));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage
                            .get(0)).base));
                } else {
                    setMove((byte)5, AbstractMonster.Intent.SLEEP);
                }
//                switch (this.idleCount) {
//                    case 1:
//                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[1], 0.5F, 2.0F));
//                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
//                        break;
//                    case 2:
//                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[2], 0.5F, 2.0F));
//                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
//                        break;
//                }
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TextAboveCreatureAction((AbstractCreature)this, TextAboveCreatureAction.TextType.STUNNED));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;
            case 6:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "OPEN"));
                setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                createIntent();
                this.isOutTriggered = true;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;

            case 7:
                this.debuffTurnCount = 0;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "DEBUFF"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 2, true), 2));
                setMove((byte)8, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                break;
            case 8:
                this.debuffTurnCount++;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                setMove((byte)9, Intent.ATTACK_BUFF, ((DamageInfo)this.damage.get(0)).base);
                break;
            case 9:
                this.debuffTurnCount++;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower(this, 1), 1));

                setMove(DEBUFF_NAME, (byte)7, AbstractMonster.Intent.STRONG_DEBUFF);
                break;
        }
    }







    public void changeState(String stateName) {
        if (stateName.equals("ATTACK")) {
            this.state.setAnimation(0, "Attack", false);
            this.state.addAnimation(0, "Idle_2", true, 0.0F);
        } else if (stateName.equals("DEBUFF")) {
            this.state.setAnimation(0, "Debuff", false);
            this.state.addAnimation(0, "Idle_2", true, 0.0F);
        } else if (stateName.equals("OPEN") && !this.isDying) {
            this.isOut = true;
            updateHitbox(0.0F, -25.0F, 320.0F, 360.0F);
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[3], 0.5F, 2.0F));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ReducePowerAction((AbstractCreature)this, (AbstractCreature)this, "Metallicize", 8));

            CardCrawlGame.music.unsilenceBGM();
            AbstractDungeon.scene.fadeOutAmbiance();
            CardCrawlGame.music.playPrecachedTempBgm();
            this.state.setAnimation(0, "Coming_out", false);
            this.state.addAnimation(0, "Idle_2", true, 0.0F);
        }
    }


    public void damage(DamageInfo info) {
        int previousHealth = this.currentHealth;

        super.damage(info);

        if (this.currentHealth != previousHealth && !this.isOutTriggered) {
            setMove((byte)4, AbstractMonster.Intent.STUN);
            createIntent();
            this.isOutTriggered = true;
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "OPEN"));
        } else if (this.isOutTriggered && info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {

            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle_2", true, 0.0F);
        }
    }


    protected void getMove(int num) {

        if (this.isOut) {
            if (this.debuffTurnCount < 2) {
                if (lastTwoMoves((byte)3)) {
                    logger.info("DEBUFF_NAME is "+DEBUFF_NAME);
                    setMove(DEBUFF_NAME, (byte)1, AbstractMonster.Intent.STRONG_DEBUFF);
                } else {
                    setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                }
            } else {
                logger.info("DEBUFF_NAME is "+DEBUFF_NAME);
                setMove(DEBUFF_NAME, (byte)1, AbstractMonster.Intent.STRONG_DEBUFF);
            }
        } else {
            setMove((byte)5, AbstractMonster.Intent.SLEEP);
        }
    }


    public void die() {
        super.die();
        AbstractDungeon.scene.fadeInAmbiance();
        CardCrawlGame.music.fadeOutTempBGM();
    }
}


