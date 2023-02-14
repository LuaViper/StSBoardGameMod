package BoardGame.monsters.bgcity;
import BoardGame.cards.BGStatus.BGBurn;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.monsters.AbstractBGMonster;

import BoardGame.powers.BGWeakPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.scenes.TheCityScene;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import com.megacrit.cardcrawl.vfx.GlowyFireEyesEffect;
import com.megacrit.cardcrawl.vfx.StaffFireEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGTheCollector extends AbstractBGMonster {
    private static final Logger logger = LogManager.getLogger(BGTheCollector.class.getName());
    public static final String ID = "TheCollector";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("TheCollector");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final int HP = 282;

    public static final int A_2_HP = 300;

    private static final int FIREBALL_DMG = 18;
    private static final int STR_AMT = 3;
    private static final int BLOCK_AMT = 15;
    private static final int A_2_FIREBALL_DMG = 21;
    private int turnsTaken = 0; private static final int A_2_STR_AMT = 4; private static final int A_2_BLOCK_AMT = 18; private int rakeDmg; private int strAmt; private int blockAmt; private int megaDebuffAmt; private static final int MEGA_DEBUFF_AMT = 3;
    private float spawnX = -100.0F;
    private float fireTimer = 0.0F;

    private static final float FIRE_TIME = 0.07F;
    private boolean ultUsed = false;
    private boolean initialSpawn = true;
    private HashMap<Integer, AbstractMonster> enemySlots = new HashMap<>(); private static final byte SPAWN = 1; private static final byte FIREBALL = 2; private static final byte BUFF = 3;
    private static final byte MEGA_DEBUFF = 4;
    private static final byte REVIVE = 5;

    public BGTheCollector() {
        super(NAME, "TheCollector", 282, 15.0F, -40.0F, 300.0F, 390.0F, null, 60.0F, 135.0F);

        this.dialogX = -90.0F * Settings.scale;
        this.dialogY = 10.0F * Settings.scale;
        this.type = AbstractMonster.EnemyType.BOSS;


        setHp(57);

        this.strAmt = 1;
        this.megaDebuffAmt = 2;


        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
        this.damage.add(new DamageInfo((AbstractCreature)this, 5));


        loadAnimation("images/monsters/theCity/collector/skeleton.atlas", "images/monsters/theCity/collector/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
        UnlockTracker.markBossAsSeen("COLLECTOR");

        ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
        //logger.info("checking gremlin slot "+slot);


    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                if(this.initialSpawn) {
                    for (i = 1; i < 3; i++) {
                        AbstractMonster m = new BGTorchHead(this.spawnX + -185.0F * i, MathUtils.random(-5.0F, 25.0F));
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_COLLECTOR_SUMMON"));
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SpawnMonsterAction(m, false));
                        this.enemySlots.put(Integer.valueOf(i), m);
                    }
                    this.initialSpawn = false;
                }else{
                    for (Map.Entry<Integer, AbstractMonster> m : this.enemySlots.entrySet()) {
                        if (((AbstractMonster) m.getValue()).isDying) {
                            AbstractMonster newMonster = new BGTorchHead(this.spawnX + -185.0F * ((Integer) m.getKey()).intValue(), MathUtils.random(-5.0F, 25.0F));
                            int key = ((Integer) m.getKey()).intValue();
                            this.enemySlots.put(Integer.valueOf(key), newMonster);
                            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new SpawnMonsterAction(newMonster, false));
                        }
                    }
                }
                setMove((byte) 2, AbstractMonster.Intent.ATTACK_BUFF, 3);
                break;


            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) m, (AbstractCreature) this, (AbstractPower) new StrengthPower((AbstractCreature) m, this.strAmt), this.strAmt));
                    }
                }
                setMove((byte) 3, AbstractMonster.Intent.ATTACK, 5);
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                if (!ultUsed) {
                    setMove((byte) 4, AbstractMonster.Intent.STRONG_DEBUFF);
                } else {
                    setMove((byte) 1, AbstractMonster.Intent.UNKNOWN);
                }
                break;

            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[0]));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_COLLECTOR_DEBUFF"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new CollectorCurseEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 2.0F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, this.megaDebuffAmt, true), this.megaDebuffAmt));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), this.megaDebuffAmt, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)new BGBurn(), this.megaDebuffAmt));
                this.ultUsed = true;
                setMove((byte) 1, AbstractMonster.Intent.UNKNOWN);

                break;


            default:
                logger.info("ERROR: Default Take Turn was called on " + this.name);
                break;
        }
        this.turnsTaken++;

    }


    protected void getMove(int num) {
            setMove((byte)1, AbstractMonster.Intent.UNKNOWN);
    }

    private boolean isMinionDead() {
        for (Map.Entry<Integer, AbstractMonster> m : this.enemySlots.entrySet()) {
            if (((AbstractMonster)m.getValue()).isDying) {
                return true;
            }
        }

        return false;
    }


    public void update() {
        super.update();
        if (!this.isDying) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.07F;
                AbstractDungeon.effectList.add(new GlowyFireEyesEffect(this.skeleton

                        .getX() + this.skeleton.findBone("lefteyefireslot").getX(), this.skeleton
                        .getY() + this.skeleton.findBone("lefteyefireslot").getY() + 140.0F * Settings.scale));

                AbstractDungeon.effectList.add(new GlowyFireEyesEffect(this.skeleton

                        .getX() + this.skeleton.findBone("righteyefireslot").getX(), this.skeleton
                        .getY() + this.skeleton.findBone("righteyefireslot").getY() + 140.0F * Settings.scale));

                AbstractDungeon.effectList.add(new StaffFireEffect(this.skeleton

                        .getX() + this.skeleton.findBone("fireslot").getX() - 120.0F * Settings.scale, this.skeleton
                        .getY() + this.skeleton.findBone("fireslot").getY() + 390.0F * Settings.scale));
            }
        }
    }


    public void die() {
        super.die();

        boolean done=true;
        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                done=false;
            }
        }
        if(done) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            onBossVictoryLogic();
        }
        UnlockTracker.hardUnlockOverride("COLLECTOR");
        UnlockTracker.unlockAchievement("COLLECTOR");
    }



    @SpirePatch2(clz= TheCityScene.class, method="randomizeScene",
        paramtypez={})
    public static class CityScenePatch{
        @SpirePostfixPatch
        public static void randomizeScene(@ByRef boolean[] ___renderThrone){
            if (AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss && (AbstractDungeon.getCurrRoom()).monsters
                    .getMonster("BGTheCollector") != null) {
                ___renderThrone[0]=true;
            }
        }
    }

}


