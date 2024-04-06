package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;


import BoardGame.BoardGame;
import BoardGame.monsters.DieControlledMoves;
 
import BoardGame.powers.BGSporeCloudPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGFungiBeast extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {
    public static final String ID = "BGFungiBeast";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("FungiBeast"); public static final String DOUBLE_ENCOUNTER = "TwoFungiBeasts";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 22; private static final int HP_MAX = 28;
    private static final int A_2_HP_MIN = 24;
    private static final int A_2_HP_MAX = 28;
    private static final float HB_X = 0.0F;
    private static final float HB_Y = -16.0F;
    private static final float HB_W = 260.0F;
    private static final float HB_H = 170.0F;
    private int biteDamage;
    private int strAmt;
    private static final int BITE_DMG = 6;
    private static final int GROW_STR = 3;
    private static final int A_2_GROW_STR = 4;
    private static final byte BITE = 1;
    private static final byte GROW = 2;
    private static final int VULN_AMT = 2;
    private int strAmount;
    private String behavior;

    public BGFungiBeast(float x, float y, String behavior, boolean a7summon) {
        super(NAME, "BGFungiBeast", 28, 0.0F, -16.0F, 260.0F, 170.0F, null, x, y);

        this.behavior=behavior;

        if(a7summon){
            setHp(6);
            strAmount=1;
        }else {
            setHp(5);
            strAmount=2;
        }

        loadAnimation("images/monsters/theBottom/fungi/skeleton.atlas", "images/monsters/theBottom/fungi/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(MathUtils.random(0.7F, 1.0F));

        this.damage.add(new DamageInfo((AbstractCreature) this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 1));


    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGSporeCloudPower((AbstractCreature)this, 1)));
    }



    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, strAmount), strAmount));
                break;
        }



        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }





        public void dieMove(int roll) {
            final Logger logger = LogManager.getLogger(BoardGame.class.getName());
            //logger.info("Monster: TheDie "+ TheDie.monsterRoll);
            char move = '-';
            if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
                move = this.behavior.charAt(0);
            else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
                move = this.behavior.charAt(1);
            else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
                move = this.behavior.charAt(2);


            if (move == '2') {
                setMove((byte) 1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
            } else if (move=='S'){
                setMove(MOVES[0], (byte) 3, AbstractMonster.Intent.BUFF);
            } else if (move=='1'){
                setMove("Power Up", (byte) 2, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(1)).base);
            } else if (move=='!'){
                //TODO: unlike '1', this is not an AOE attack -- need to create new (byte)4 move
                setMove("Power Up", (byte) 2, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(1)).base);
            }
        }

    protected void getMove(int num) {

            setMove((byte) 0, AbstractMonster.Intent.NONE);

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
}


