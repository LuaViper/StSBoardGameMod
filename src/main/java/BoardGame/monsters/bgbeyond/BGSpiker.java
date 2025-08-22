
package BoardGame.monsters.bgbeyond;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.dungeons.BGTheBeyond;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGSpikerPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
 import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
 import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSpiker extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Spiker"); public static final String ID = "Spiker";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String ENCOUNTER_NAME = "Ancient Shapes";

    private static final int HP_MIN = 42;
    private static final int HP_MAX = 56;
    private static final int A_2_HP_MIN = 44;
    private static final int A_2_HP_MAX = 60;
    private static final float HB_X = -8.0F;
    private static final float HB_Y = -10.0F;
    private static final float HB_W = 150.0F;
    private static final float HB_H = 150.0F;
    private static final int STARTING_THORNS = 3;
    private static final int A_2_STARTING_THORNS = 4;
    private int startingThorns;
    private static final byte ATTACK = 1;
    private static final int ATTACK_DMG = 7;
    private static final int A_2_ATTACK_DMG = 9;
    private int attackDmg;
    private static final byte BUFF_THORNS = 2;
    private static final int BUFF_AMT = 2;
    private int thornsCount = 0;

//TODO NEXT NEXT: final attack against GREMLIN NOB is bugged too.
//              occurs with any multi-hit attack that Spiker survives the first hit of.
//                  does not occur with Guardian defensive mode.
    public BGSpiker(float x, float y) {
        super(NAME, "BGSpiker", 56, -8.0F, -10.0F, 150.0F, 150.0F, null, x, y + 10.0F);

        loadAnimation("images/monsters/theForest/spiker/skeleton.atlas", "images/monsters/theForest/spiker/skeleton.json", 1.0F);

        this.behavior= BGTheBeyond.getSummonSpiker();


        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

//        if (AbstractDungeon.ascensionLevel >= 7) {
//            setHp(44, 60);
//        } else {
//            setHp(42, 56);
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.startingThorns = 4;
//            this.attackDmg = 9;
//        } else {
//            this.startingThorns = 3;
//            this.attackDmg = 7;
//        }

        setHp(10);
        this.startingThorns = 1;
        this.attackDmg=2;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.attackDmg));
    }


    public void usePreBattleAction() {
//        if (AbstractDungeon.ascensionLevel >= 17) {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new ThornsPower((AbstractCreature)this, this.startingThorns + 3)));
//        } else {
//            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new ThornsPower((AbstractCreature)this, this.startingThorns)));
//        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGSpikerPower((AbstractCreature)this, this.startingThorns)));
    }



    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            case 2:
                this.thornsCount++;
                //if(this.thornsCount<5) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new BGSpikerPower((AbstractCreature) this, 1), 1));
                //}
                break;
        }



        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
    public void dieMove(int roll){
        final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2 || TheDie.monsterRoll == 3)
            move = this.behavior.charAt(0);
        else if (TheDie.monsterRoll == 4 || TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
            move = this.behavior.charAt(1);

        if (move == '2') {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base);
        } else if (move == 'T') {
            setMove((byte) 2, AbstractMonster.Intent.BUFF);
        }
    }

    //as of latest discussion, spiker DOES reflect damage IF combat isn't over
    public void die() {
        //BGSpikerPowerProcced won't activate if the player's attack also ends combat, so check here
        AbstractPower p=this.getPower("BoardGame:BGSpikerProcced");
        if(p!=null){
            addToBot((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, new DamageInfo(this, p.amount,
                    DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, true));
            //if spiker died and DIDN'T end combat, ...actually i'm not sure whether onAfterUseCard still happens here, but remove proc to be safe
            p.amount=0;
            addToBot((AbstractGameAction) new RemoveSpecificPowerAction(this, this, "BGSpikerProcced"));
        }
        super.die();
    }


}


