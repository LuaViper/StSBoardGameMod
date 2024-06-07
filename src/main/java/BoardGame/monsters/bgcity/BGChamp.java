package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;

import BoardGame.powers.BGChampPhase2WarningPower;
import BoardGame.powers.BGWeakPower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateJumpAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.GoldenSlashEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import java.util.ArrayList;

public class BGChamp
        extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGChamp";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Champ");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; public static final int HP = 420; public static final int A_9_HP = 440; private static final byte HEAVY_SLASH = 1;
    private static final byte DEFENSIVE_STANCE = 2;
    private static final byte EXECUTE = 3;
    private static final byte FACE_SLAP = 4;
    private static final byte GLOAT = 5;
    private static final byte TAUNT = 6;
    private static final byte ANGER = 7;
    private static final String STANCE_NAME = MOVES[0]; private static final String EXECUTE_NAME = MOVES[1]; private static final String SLAP_NAME = MOVES[2];

    private int slashDmg;
    private int executeDmg;
    private int slapDmg;
    private int blockAmt;
    private static final int DEBUFF_AMT = 1;
    private static final int EXEC_COUNT = 2;
    private int numTurns = 0;
    private static final int BLOCK_AMT = 3;
    private int strAmt=1; private int forgeAmt=3;
    private int executeStrAmt=1;
    private int forgeTimes = 0; private int forgeThreshold = 2;
    private boolean thresholdReached = false, firstTurn = true;

    private boolean phase2=false;

    public BGChamp() {
        super(NAME, "BGChamp", 420, 0.0F, -15.0F, 400.0F, 410.0F, null, -90.0F, 0.0F);
        this.type = AbstractMonster.EnemyType.BOSS;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;

        loadAnimation("images/monsters/theCity/champ/skeleton.atlas", "images/monsters/theCity/champ/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(0.8F);


        setHp((AbstractDungeon.ascensionLevel<10) ? 40 : 45);

        executeStrAmt=(AbstractDungeon.ascensionLevel<10) ? 1 : 2;

        this.damage.add(new DamageInfo((AbstractCreature)this, 4));
        this.damage.add(new DamageInfo((AbstractCreature)this, (AbstractDungeon.ascensionLevel<10) ? 5 : 6));
        this.damage.add(new DamageInfo((AbstractCreature)this, 4));

    }


    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
        UnlockTracker.markBossAsSeen("CHAMP");
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGChampPhase2WarningPower((AbstractCreature)this)));
    }


    public void takeTurn() {
        float vfxSpeed = 0.1F;

        if (Settings.FAST_MODE) {
            vfxSpeed = 0.0F;
        }

        if (this.firstTurn) {
            this.firstTurn = false;
            if (AbstractDungeon.player.hasRelic("Champion Belt")) {
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[8], 0.5F, 2.0F));
            }
        }

        switch (this.nextMove) {
            case 1: //4 dmg
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new GoldenSlashEffect(AbstractDungeon.player.hb.cX - 60.0F * Settings.scale, AbstractDungeon.player.hb.cY, false), vfxSpeed));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                setMove(SLAP_NAME,(byte)2, AbstractMonster.Intent.DEBUFF);
                break;

            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_CHAMP_SLAP"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, getTaunt()));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.strAmt), this.strAmt));
                setMove(STANCE_NAME,(byte)3, AbstractMonster.Intent.ATTACK_DEFEND, ((DamageInfo)this.damage.get(1)).base);
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.blockAmt));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new GoldenSlashEffect(AbstractDungeon.player.hb.cX - 60.0F * Settings.scale, AbstractDungeon.player.hb.cY, false), vfxSpeed));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, 3));
                setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                break;
            case 4: //debuff
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_CHAMP_CHARGE"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new InflameEffect((AbstractCreature)this), 0.25F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new InflameEffect((AbstractCreature)this), 0.25F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractCreature)this, (AbstractGameEffect)new InflameEffect((AbstractCreature)this), 0.25F));
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this, "BGWeakened"));
                addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this, "BGVulnerable"));
                setMove(EXECUTE_NAME, (byte)5, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(2)).base, 2, true);
                AbstractDungeon.actionManager.addToTop((AbstractGameAction)new TalkAction((AbstractCreature)this, getDeathQuote(), 2.0F, 2.0F));
                break;
            case 5: //execute
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateJumpAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new GoldenSlashEffect(AbstractDungeon.player.hb.cX - 60.0F * Settings.scale, AbstractDungeon.player.hb.cY, true), vfxSpeed));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new GoldenSlashEffect(AbstractDungeon.player.hb.cX + 60.0F * Settings.scale, AbstractDungeon.player.hb.cY, true), vfxSpeed));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.NONE));
                setMove((byte)6, AbstractMonster.Intent.BUFF);
                break;
            case 6:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, this.executeStrAmt), this.executeStrAmt));
                setMove(EXECUTE_NAME, (byte)5, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(2)).base, 2, true);
                break;


        }





        //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
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

    private String getTaunt() {
        ArrayList<String> derp = new ArrayList<>();
        derp.add(DIALOG[0]);
        derp.add(DIALOG[1]);
        derp.add(DIALOG[2]);
        derp.add(DIALOG[3]);
        return derp.get(MathUtils.random(derp.size() - 1));
    }

    private String getLimitBreak() {
        ArrayList<String> derp = new ArrayList<>();
        derp.add(DIALOG[4]);
        derp.add(DIALOG[5]);
        return derp.get(MathUtils.random(derp.size() - 1));
    }

    private String getDeathQuote() {
        ArrayList<String> derp = new ArrayList<>();
        derp.add(DIALOG[6]);
        derp.add(DIALOG[7]);
        return derp.get(MathUtils.random(derp.size() - 1));
    }



    protected void getMove(int num) {
        this.numTurns++;
        setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
//
//        if (this.currentHealth < this.maxHealth / 2 && !this.thresholdReached) {
//            this.thresholdReached = true;
//            setMove((byte)7, AbstractMonster.Intent.BUFF);
//
//            return;
//        }
//        if (!lastMove((byte)3) && !lastMoveBefore((byte)3) && this.thresholdReached) {
//            AbstractDungeon.actionManager.addToTop((AbstractGameAction)new TalkAction((AbstractCreature)this, getDeathQuote(), 2.0F, 2.0F));
//            setMove(EXECUTE_NAME, (byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base, 2, true);
//
//            return;
//        }
//        if (this.numTurns == 4 && !this.thresholdReached) {
//            setMove((byte)6, AbstractMonster.Intent.DEBUFF);
//            this.numTurns = 0;
//
//            return;
//        }
//        if (AbstractDungeon.ascensionLevel >= 19) {
//
//            if (!lastMove((byte)2) && this.forgeTimes < this.forgeThreshold && num <= 30) {
//                this.forgeTimes++;
//                setMove(STANCE_NAME, (byte)2, AbstractMonster.Intent.DEFEND_BUFF);
//
//
//                return;
//            }
//        } else if (!lastMove((byte)2) && this.forgeTimes < this.forgeThreshold && num <= 15) {
//            this.forgeTimes++;
//            setMove(STANCE_NAME, (byte)2, AbstractMonster.Intent.DEFEND_BUFF);
//
//
//            return;
//        }
//
//        if (!lastMove((byte)5) && !lastMove((byte)2) && num <= 30) {
//            setMove((byte)5, AbstractMonster.Intent.BUFF);
//
//            return;
//        }
//
//        if (!lastMove((byte)4) && num <= 55) {
//            setMove(SLAP_NAME, (byte)4, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(2)).base);
//
//            return;
//        }
//        if (!lastMove((byte)1)) {
//            setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
//        } else {
//            setMove(SLAP_NAME, (byte)4, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(2)).base);
//        }
    }



    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            super.die();
            if (MathUtils.randomBoolean()) {
                CardCrawlGame.sound.play("VO_CHAMP_3A");
            } else {
                CardCrawlGame.sound.play("VO_CHAMP_3B");
            }
            AbstractDungeon.scene.fadeInAmbiance();
            onBossVictoryLogic();
            UnlockTracker.hardUnlockOverride("CHAMP");
            UnlockTracker.unlockAchievement("CHAMP");
        }else{
            if(!phase2) {
                phase2 = true;

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ShoutAction((AbstractCreature) this, getLimitBreak(), 2.0F, 3.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TextAboveCreatureAction((AbstractCreature)this, "Anger"));    //TODO: localization
                addToBot((AbstractGameAction) new RemoveSpecificPowerAction((AbstractCreature) this, (AbstractCreature) this, "BGChampPhase2WarningPower"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new HealAction((AbstractCreature) this, (AbstractCreature) this, this.maxHealth));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new CanLoseAction());
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction((AbstractMonster) this, (byte) 4, Intent.BUFF));
                setMove((byte) 4, AbstractMonster.Intent.BUFF);
                createIntent();
            }
        }
    }
}


