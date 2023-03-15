//TODO: "At the end of its turn, gains #rat #rleast #b",

package BoardGame.monsters.bgbeyond; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGOrbWalkerPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GenericStrengthUpPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGOrbWalker extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    public static final String ID = "BGOrb Walker";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Orb Walker");
    public static final String DOUBLE_ENCOUNTER = "Double Orb Walker";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 90; private static final int HP_MAX = 96; private static final int A_2_HP_MIN = 92;
    private static final int A_2_HP_MAX = 102;
    public static final int LASER_DMG = 10;
    public static final int CLAW_DMG = 15;
    public static final int A_2_LASER_DMG = 11;
    public static final int A_2_CLAW_DMG = 16;
    private int clawDmg;
    private int laserDmg;
    private static final byte LASER = 1;
    private static final byte CLAW = 2;
    public String behavior="--";

    public BGOrbWalker(float x, float y, String behavior) {
        super(NAME, "BGOrb Walker", AbstractDungeon.monsterHpRng.random(90, 96), -20.0F, -14.0F, 280.0F, 250.0F, null, x, y);

        this.behavior=behavior;
//        if (AbstractDungeon.ascensionLevel >= 7) {
//            setHp(92, 102);
//        } else {
//            setHp(90, 96);
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.clawDmg = 16;
//            this.laserDmg = 11;
//        } else {
//            this.clawDmg = 15;
//            this.laserDmg = 10;
//        }

        setHp(22);

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));

        loadAnimation("images/monsters/theForest/orbWalker/skeleton.atlas", "images/monsters/theForest/orbWalker/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGOrbWalkerPower((AbstractCreature)this, MOVES[0], 1)));
    }



    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Dazed(), 1, false, true));
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
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base);
        } else if (move == '3') {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base);
        }
    }



    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }
}


