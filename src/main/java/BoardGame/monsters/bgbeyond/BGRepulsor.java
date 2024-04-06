package BoardGame.monsters.bgbeyond; 
 import BoardGame.cards.BGStatus.BGDazed;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGRepulsor extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Repulsor"); public static final String ID = "Repulsor";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; public static final String ENCOUNTER_NAME_W = "Ancient Shapes Weak";
    public static final String ENCOUNTER_NAME = "Ancient Shapes";
    private static final float HB_X = -8.0F;
    private static final float HB_Y = -10.0F;
    private static final float HB_W = 150.0F;
    private static final float HB_H = 150.0F;
    private static final byte DAZE = 1;
    private static final byte ATTACK = 2;
    private int attackDmg;
    private int dazeAmt;

    public String behavior="--";

    public BGRepulsor(float x, float y, String behavior) {
        super(NAME, "BGRepulsor", 35, -8.0F, -10.0F, 150.0F, 150.0F, null, x, y + 10.0F);

        loadAnimation("images/monsters/theForest/repulser/skeleton.atlas", "images/monsters/theForest/repulser/skeleton.json", 1.0F);

        this.behavior=behavior;

        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.dazeAmt = 2;

//        if (AbstractDungeon.ascensionLevel >= 7) {
//            setHp(31, 38);
//        } else {
//            setHp(29, 35);
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.attackDmg = 13;
//        } else {
//            this.attackDmg = 11;
//        }

        setHp(7);

        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 3));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1: //1 dmg+daze
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), this.dazeAmt, false, true));
                break;
            case 2: //3 dmg
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
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

        if (move == 'D') {
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        } else if (move == '3') {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base);
        }
    }
}


