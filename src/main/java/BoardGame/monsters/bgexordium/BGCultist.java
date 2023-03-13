package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;
import BoardGame.BoardGame;

import com.badlogic.gdx.math.MathUtils;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGCultist extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGCultist";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Cultist");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; public static final String MURDER_ENCOUNTER_KEY = "Murder of Cultists"; private static final int HP_MIN = 48; private static final int HP_MAX = 54; private static final int A_2_HP_MIN = 50; private static final int A_2_HP_MAX = 56; private static final float HB_X = -8.0F;
    private static final float HB_Y = 10.0F;
    private static final float HB_W = 230.0F;
    private static final String INCANTATION_NAME = MOVES[2];

    private static final float HB_H = 240.0F;

    private static final int ATTACK_DMG = 1;
    private static final int strAmt = 1;

    private boolean firstMove = true;
    private boolean saidPower = false;
    private static final int RITUAL_AMT = 3;
    private static final int A_2_RITUAL_AMT = 4;
    private int ritualAmount = 0; private static final byte DARK_STRIKE = 1;
    private static final byte INCANTATION = 3;
    private boolean talky = true;

    public BGCultist(float x, float y, boolean talk) {
        super(NAME, "BGCultist", 54, -8.0F, 10.0F, 230.0F, 240.0F, null, x, y);

//        if (AbstractDungeon.ascensionLevel >= 7) {
//            setHp(50, 56);
//        } else {
            setHp(9, 9);
//        }

        this.dialogX = -50.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.ritualAmount = 4;  //NYI
//        } else {
            this.ritualAmount = 1;
//        }

        this.damage.add(new DamageInfo((AbstractCreature)this, 1));

        this.talky = talk;
        if (Settings.FAST_MODE) {
            this.talky = false;
        }

        loadAnimation("images/monsters/theBottom/cultist/skeleton.atlas", "images/monsters/theBottom/cultist/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "waving", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public BGCultist(float x, float y) {
        this(x, y, true);
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new RitualPower((AbstractCreature)this, this.ritualAmount, false)));
    }

    public void takeTurn() {
        int temp;
        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        logger.info("Monster: nextMove "+ this.nextMove);
        switch (this.nextMove) {
            case 3:
                temp = MathUtils.random(1, 10);
                if (this.talky) {
                    playSfx();
                    if (temp < 4) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[0], 1.0F, 2.0F));
                        this.saidPower = true;
                    } else if (temp < 7) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[1], 1.0F, 2.0F));
                    }
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                //NPC Ritual doesn't apply on round 1, so manually add 1 str here
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.strAmt), this.strAmt));
                break;


            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
        }



        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_CULTIST_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_CULTIST_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_CULTIST_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_CULTIST_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_CULTIST_2B");
        } else {
            CardCrawlGame.sound.play("VO_CULTIST_2C");
        }
    }


    public void die() {
        playDeathSfx();
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
        if (this.talky &&
                this.saidPower) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[2], false));
            this.deathTimer += 1.5F;
        }
        super.die();

        //Awakened One check
        if(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                useFastShakeAnimation(5.0F);
                CardCrawlGame.screenShake.rumble(4.0F);

                onBossVictoryLogic();
                UnlockTracker.hardUnlockOverride("CROW");
                UnlockTracker.unlockAchievement("CROW");
                onFinalBossVictoryLogic();
            }
        }
    }



    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            //setMove(INCANTATION_NAME, (byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
            setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
            return;
        }

        setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
    }
}


