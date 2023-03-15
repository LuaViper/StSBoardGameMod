package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGVulnerablePower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
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
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class BGShelledParasite extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    public static final String ID = "BGShelled Parasite";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Shelled Parasite");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 68;
    private static final int HP_MAX = 72;
    private static final int A_2_HP_MIN = 70;
    private static final int A_2_HP_MAX = 75;
    private static final float HB_X_F = 20.0F;
    private static final float HB_Y_F = -6.0F;
    private static final float HB_W = 350.0F;
    private static final float HB_H = 260.0F;
    private static final int PLATED_ARMOR_AMT = 14;
    private static final int FELL_DMG = 18;
    private static final int DOUBLE_STRIKE_DMG = 6;
    private static final int SUCK_DMG = 10;
    private static final int A_2_FELL_DMG = 21;

    public BGShelledParasite(float x, float y) {
        super(NAME, "BGShelled Parasite", 72, 20.0F, -6.0F, 350.0F, 260.0F, null, x, y);

        loadAnimation("images/monsters/theCity/shellMonster/skeleton.atlas", "images/monsters/theCity/shellMonster/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        e.setTimeScale(0.8F);

        this.dialogX = -50.0F * Settings.scale;

        setHp(18);



        this.damage.add(new DamageInfo((AbstractCreature)this, 4));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
    }
    private static final int A_2_DOUBLE_STRIKE_DMG = 7; private static final int A_2_SUCK_DMG = 12; private int fellDmg; private int doubleStrikeDmg; private int suckDmg; private static final int DOUBLE_STRIKE_COUNT = 2; private static final int FELL_FRAIL_AMT = 2; private static final byte FELL = 1; private static final byte DOUBLE_STRIKE = 2; private static final byte LIFE_SUCK = 3; private static final byte STUNNED = 4; private boolean firstMove = true; public static final String ARMOR_BREAK = "ARMOR_BREAK";
    public BGShelledParasite() {
        this(-20.0F, 10.0F);
    }




    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, 2));
                setMove((byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                break;
            case 2:

                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateHopAction((AbstractCreature)this));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.2F));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGVulnerablePower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, 2));
                setMove((byte)3, AbstractMonster.Intent.ATTACK_DEFEND, ((DamageInfo)this.damage.get(2)).base);
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +
                        MathUtils.random(-25.0F, 25.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-25.0F, 25.0F) * Settings.scale, Color.GOLD
                        .cpy()), 0.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, 2));
                setMove((byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                break;
        }

    }


    public void changeState(String stateName) {
        switch (stateName) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "ARMOR_BREAK":
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateHopAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateHopAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateHopAction((AbstractCreature)this));
                setMove((byte)4, AbstractMonster.Intent.STUN);
                createIntent();
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



    protected void getMove(int num) {
        setMove((byte)1, AbstractMonster.Intent.ATTACK_DEFEND, ((DamageInfo)this.damage.get(0)).base); return;
    }

    @Override
    public void dieMove(int roll) {
        if(this.nextMove==2){
            if(!this.halfDead && !this.isDying && !this.isEscaping)AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, "@6.022140857×10^23@", false));
        }
    }
}


