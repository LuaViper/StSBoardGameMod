
package BoardGame.monsters.bgcity;
 import BoardGame.actions.BGBuffAllEnemiesAction;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.actions.BGSpawnTwoGremlinsForGremlinLeaderAction;
import BoardGame.dungeons.BGExordium;
import BoardGame.monsters.AbstractBGMonster;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
 import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

//TODO: Carve Reality is improperly cancelling against live minions

public class BGGremlinLeader extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinLeader");
    public static final String ID = "BGGremlinLeader";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 140;
    private static final int HP_MAX = 148;
    private static final int A_2_HP_MIN = 145;
    private static final int A_2_HP_MAX = 155;
    public static final String ENC_NAME = "Gremlin Leader Combat";
    public AbstractMonster[] gremlins = new AbstractMonster[3];
    public static final float[] POSX = new float[]{-366.0F, -170.0F, -532.0F};
    public static final float[] POSY = new float[]{-4.0F, 6.0F, 0.0F};


    private static final byte RALLY = 2;


    private static final String RALLY_NAME = MOVES[0];

    private static final byte ENCOURAGE = 3;
    private static final int STR_AMT = 3;
    private static final int BLOCK_AMT = 6;
    private static final int A_2_STR_AMT = 4;
    private static final int A_18_STR_AMT = 5;
    private static final int A_18_BLOCK_AMT = 10;
    private int strAmt;
    private int blockAmt;
    private static final byte STAB = 4;
    private int STAB_DMG = 6, STAB_AMT = 3;

    public String[] gremlintypes;

    public BGGremlinLeader() {
        super(NAME, "BGGremlinLeader", 148, 0.0F, -15.0F, 200.0F, 310.0F, null, 35.0F, 0.0F);
        this.type = AbstractMonster.EnemyType.ELITE;

        loadAnimation("images/monsters/theCity/gremlinleader/skeleton.atlas", "images/monsters/theCity/gremlinleader/skeleton.json", 1.0F);

        this.gremlintypes= BGExordium.pickTwoGremlins();

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.8F);


        if(AbstractDungeon.ascensionLevel<1)
            setHp(30);
        else if(AbstractDungeon.ascensionLevel<12)
            setHp(35);
        else
            setHp(40);


        this.blockAmt = 1;
        this.strAmt = 1;


        this.dialogX = -80.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;
        if(AbstractDungeon.ascensionLevel<1)
            this.damage.add(new DamageInfo((AbstractCreature) this, 2));
        else
            this.damage.add(new DamageInfo((AbstractCreature) this, 3));
        this.damage.add(new DamageInfo((AbstractCreature) this, 5));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new BGSpawnTwoGremlinsForGremlinLeaderAction(this));

    }


    public void takeTurn() {
        this.gremlins[0] = (AbstractDungeon.getMonsters()).monsters.get(1);
        this.gremlins[1] = (AbstractDungeon.getMonsters()).monsters.get(2);
        this.gremlins[2] = null;

        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShoutAction((AbstractCreature)this, getEncourageQuote()));
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)m, this.strAmt), this.strAmt));
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)m, (AbstractCreature)this, this.blockAmt));
                    }
                }
                setMove((byte) 2, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
                break;



            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                setMove(RALLY_NAME, (byte) 3, AbstractMonster.Intent.UNKNOWN);
                break;

            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "CALL"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new BGSpawnTwoGremlinsForGremlinLeaderAction(this));
                if(AbstractDungeon.ascensionLevel>=12){
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new BGBuffAllEnemiesAction(this));
                }
                setMove((byte) 1, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
                break;
        }

    }

    private String getEncourageQuote() {
        ArrayList<String> list = new ArrayList<>();
        list.add(DIALOG[0]);
        list.add(DIALOG[1]);
        list.add(DIALOG[2]);

        return list.get(AbstractDungeon.aiRng.random(0, list.size() - 1));
    }




    protected void getMove(int num) {
        setMove((byte) 1, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
    }


    private int numAliveGremlins() {
        int count = 0;
        for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
            if (m != null && m != this && !m.isDying) {
                count++;
            }
        }
        return count;
    }


    public void changeState(String stateName) {
        switch (stateName) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "CALL":
                this.state.setAnimation(0, "Call", false);
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

}

