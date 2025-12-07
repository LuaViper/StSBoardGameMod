package CoopBoardGame.monsters.bgcity;

import CoopBoardGame.dungeons.BGTheCity;
import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.BGDamageIcons;
import CoopBoardGame.monsters.DieControlledMoves;
import CoopBoardGame.powers.BGFlightPower;
import CoopBoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGByrd extends AbstractBGMonster implements DieControlledMoves, BGDamageIcons {

    public static final String ID = "BGByrd";
    final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("Byrd");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String THREE_BYRDS = "3_Byrds";

    public static final String FOUR_BYRDS = "4_Byrds";

    public static final String IMAGE = "images/monsters/theCity/byrdFlying.png";

    public BGByrd(float x, float y) {
        this(x, y, BGTheCity.getSummonByrd(), 4);
    }

    public BGByrd(float x, float y, String behavior, int hp) {
        super(NAME, "BGByrd", 31, 0.0F, 50.0F, 240.0F, 180.0F, null, x, y);
        setHp(hp);
        this.behavior = behavior;

        this.peckDmg = 1;
        this.peckCount = 2;
        this.swoopDmg = 3;

        this.damage.add(new DamageInfo((AbstractCreature) this, this.peckDmg));
        this.damage.add(new DamageInfo((AbstractCreature) this, this.swoopDmg));

        loadAnimation(
            "images/monsters/theCity/byrd/flying.atlas",
            "images/monsters/theCity/byrd/flying.json",
            1.0F
        );
        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle_flap", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    private int peckDmg;
    private int peckCount;
    private int swoopDmg;
    public static final String FLY_STATE = "FLYING";
    public static final String GROUND_STATE = "GROUNDED";

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) this,
                (AbstractCreature) this,
                (AbstractPower) new BGFlightPower((AbstractCreature) this)
            )
        );
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1: //Peck 1+1
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateFastAttackAction((AbstractCreature) this)
                );
                for (i = 0; i < this.peckCount; i++) {
                    playRandomBirdSFx();
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new DamageAction(
                            (AbstractCreature) AbstractDungeon.player,
                            this.damage.get(0),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                            true
                        )
                    );
                }
                break;
            case 2: //Swoop 3
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(1),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );
                return;
            case 3: //Strength up
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new SFXAction("BYRD_DEATH")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new TalkAction(
                        (AbstractCreature) this,
                        DIALOG[0],
                        1.2F,
                        1.2F
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) this,
                        (AbstractCreature) this,
                        (AbstractPower) new StrengthPower((AbstractCreature) this, 1),
                        1
                    )
                );
                break;
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
    }

    private void playRandomBirdSFx() {
        AbstractDungeon.actionManager.addToBottom(
            (AbstractGameAction) new SFXAction("MONSTER_BYRD_ATTACK_" + MathUtils.random(0, 5))
        );
    }

    public void changeState(String stateName) {
        AnimationState.TrackEntry e;
        switch (stateName) {
            case "FLYING":
                loadAnimation(
                    "images/monsters/theCity/byrd/flying.atlas",
                    "images/monsters/theCity/byrd/flying.json",
                    1.0F
                );

                e = this.state.setAnimation(0, "idle_flap", true);
                e.setTime(e.getEndTime() * MathUtils.random());
                updateHitbox(0.0F, 50.0F, 240.0F, 180.0F);
                break;
            case "GROUNDED":
                setMove((byte) 4, AbstractMonster.Intent.STUN);
                createIntent();
                loadAnimation(
                    "images/monsters/theCity/byrd/grounded.atlas",
                    "images/monsters/theCity/byrd/grounded.json",
                    1.0F
                );

                e = this.state.setAnimation(0, "idle", true);
                e.setTime(e.getEndTime() * MathUtils.random());
                updateHitbox(10.0F, -50.0F, 240.0F, 180.0F);
                break;
        }
    }

    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

    public void dieMove(int roll) {
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2) move = this.behavior.charAt(0);
        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4) move = this.behavior.charAt(1);
        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6) move = this.behavior.charAt(2);

        if (move == '1') {
            setMove(
                (byte) 1,
                AbstractMonster.Intent.ATTACK,
                this.damage.get(0).base,
                this.peckCount,
                true
            );
        } else if (move == '3') {
            setMove((byte) 2, AbstractMonster.Intent.ATTACK, this.damage.get(1).base);
        } else if (move == 'S') {
            setMove((byte) 3, AbstractMonster.Intent.BUFF);
        }
    }

    public void die() {
        super.die();
        CardCrawlGame.sound.play("BYRD_DEATH");
    }
}
