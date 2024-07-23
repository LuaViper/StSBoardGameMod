//TODO: there is an unhandled edge case where right dagger kills itself via Thorns after Reptomancer has already checked if daggers are dead

package BoardGame.monsters.bgbeyond; 
 import BoardGame.cards.BGStatus.BGDazed;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGWeakPower;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
 import com.megacrit.cardcrawl.cards.AbstractCard;
 import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
 import com.megacrit.cardcrawl.powers.StrengthPower;
 import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class BGReptomancer extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGReptomancer";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Reptomancer");
    public static final String NAME = monsterStrings.NAME;

    private static final int HP_MIN = 180;
    private static final int HP_MAX = 190;
    private static final int A_2_HP_MIN = 190;
    private static final int A_2_HP_MAX = 200;
    private static final int BITE_DMG = 30;
    private static final int A_2_BITE_DMG = 34;
    public static final float[] POSX = new float[] { 210.0F, -220.0F, 180.0F, -250.0F }; private static final int SNAKE_STRIKE_DMG = 13; private static final int A_2_SNAKE_STRIKE_DMG = 16; private static final int DAGGERS_PER_SPAWN = 1; private static final int ASC_2_DAGGERS_PER_SPAWN = 2; private static final byte SNAKE_STRIKE = 1; private static final byte SPAWN_DAGGER = 2; private static final byte BIG_BITE = 3;
    public static final float[] POSY = new float[] { 75.0F, 115.0F, 345.0F, 335.0F };
    private int daggersPerSpawn;
    private AbstractMonster[] daggers = new AbstractMonster[2];
    private boolean firstMove = true;

    private int dazeAmt;
    public BGReptomancer() {
        super(NAME, "BGReptomancer", AbstractDungeon.monsterHpRng.random(180, 190), 0.0F, -30.0F, 220.0F, 320.0F, null, -20.0F, 10.0F);
        this.type = AbstractMonster.EnemyType.ELITE;

        loadAnimation("images/monsters/theForest/mage/skeleton.atlas", "images/monsters/theForest/mage/skeleton.json", 1.0F);





        this.daggersPerSpawn = 2;




        if(AbstractDungeon.ascensionLevel<1) {
            setHp(35);
        }else if(AbstractDungeon.ascensionLevel<12) {
            setHp(40);
        }else{
            setHp(42);
        }
        dazeAmt=(AbstractDungeon.ascensionLevel<12 ? 1 : 2);

        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 7));
        this.damage.add(new DamageInfo((AbstractCreature)this, 7));
        this.damage.add(new DamageInfo((AbstractCreature)this, 4));

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Idle", "Sumon", 0.1F);
        this.stateData.setMix("Sumon", "Idle", 0.1F);
        this.stateData.setMix("Hurt", "Idle", 0.1F);
        this.stateData.setMix("Idle", "Hurt", 0.1F);
        this.stateData.setMix("Attack", "Idle", 0.1F);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
//        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
//            if (!m.id.equals(this.id)) {
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new MinionPower((AbstractCreature)this)));
//            }
//            if (m instanceof BGSnakeDagger) {
//
//                if ((AbstractDungeon.getMonsters()).monsters.indexOf(m) > (AbstractDungeon.getMonsters()).monsters.indexOf(this)) {
//
//                    this.daggers[0] = m; continue;
//                }
//                this.daggers[1] = m;
//            }
//        }
    }


    public void takeTurn() {
        int daggersSpawned, i;
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "SUMMON"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));

                daggersSpawned = 0;
                for (i = 0; daggersSpawned < this.daggersPerSpawn && i < this.daggers.length; i++) {
                    if (this.daggers[i] == null || this.daggers[i].isDeadOrEscaped()) {
                        BGSnakeDagger daggerToSpawn = new BGSnakeDagger(POSX[i], POSY[i]);
                        this.daggers[i] = daggerToSpawn;
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SpawnMonsterAction(daggerToSpawn, false));
                        daggersSpawned++;
                    }
                }
                if(AbstractDungeon.ascensionLevel<1) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction(this, (byte) 1, AbstractMonster.Intent.ATTACK, 3, 2, true));
                }else {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction(this, (byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 7, 1, false));
                }
                break;
            case 1: //Ascension 0 3x2
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +


                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.ORANGE
                        .cpy()), 0.1F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +


                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.ORANGE
                        .cpy()), 0.1F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.NONE));
                {
                    boolean daggerStillAlive = false;
                    for (i = 0; i < this.daggers.length; i++) {
                        if (!(this.daggers[i] == null || this.daggers[i].isDeadOrEscaped())) {
                            daggerStillAlive = true;
                        }
                    }
                    if (daggerStillAlive) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction(this, (byte) 2, AbstractMonster.Intent.ATTACK, 7));
                    } else {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction(this, (byte) 0, AbstractMonster.Intent.UNKNOWN));
                    }
                }
                break;

            case 2: //Ascension 0 7
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +


                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                        .cpy()), 0.1F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.NONE));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)0, AbstractMonster.Intent.UNKNOWN));
                break;


            case 3: //Ascension 1+ 7
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateFastAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +


                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.CHARTREUSE
                        .cpy()), 0.1F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.NONE));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), dazeAmt, false, true));
                {
                    boolean daggerStillAlive = false;
                    for (i = 0; i < this.daggers.length; i++) {
                        if (!(this.daggers[i] == null || this.daggers[i].isDeadOrEscaped())) {
                            daggerStillAlive = true;
                        }
                    }
                    if (daggerStillAlive) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction(this, (byte) 4, AbstractMonster.Intent.ATTACK_BUFF, 4,2,true));
                    } else {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SetMoveAction(this, (byte) 0, AbstractMonster.Intent.UNKNOWN));
                    }
                }
                break;

            case 4: //Ascension 1+ 4x2
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.ORANGE
                        .cpy()), 0.1F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.NONE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                        MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.ORANGE
                        .cpy()), 0.1F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.NONE));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower(this, 1), 1));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)0, AbstractMonster.Intent.UNKNOWN));

                break;
        }
        //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }

    private boolean canSpawn() {
        int aliveCount = 0;
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (m != this && !m.isDying) {
                aliveCount++;
            }
        }
        if (aliveCount > 3) {
            return false;
        }
        return true;
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hurt", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


//    public void die() {
//        super.die();
//        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
//            if (!m.isDead && !m.isDying) {
//                AbstractDungeon.actionManager.addToTop((AbstractGameAction)new HideHealthBarAction((AbstractCreature)m));
//                AbstractDungeon.actionManager.addToTop((AbstractGameAction)new SuicideAction(m));
//            }
//        }
//    }


    protected void getMove(int num) {
        if (this.firstMove) {
            this.firstMove = false;
            setMove((byte)0, AbstractMonster.Intent.UNKNOWN);

            return;
        }
    }



    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "SUMMON":
                this.state.setAnimation(0, "Sumon", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }
}


