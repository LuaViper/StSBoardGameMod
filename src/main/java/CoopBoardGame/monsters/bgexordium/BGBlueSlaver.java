package CoopBoardGame.monsters.bgexordium;

import CoopBoardGame.cards.BGStatus.BGDazed;
import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.BGDamageIcons;
import CoopBoardGame.monsters.DieControlledMoves;
import CoopBoardGame.powers.BGWeakPower;
import CoopBoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGBlueSlaver extends AbstractBGMonster implements BGDamageIcons, DieControlledMoves {

    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("SlaverBlue");
    public static final String ID = "BGBlueSlaver";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private int weakAmt = 1;

    //Summoned Act2 Slavers are thankfully identical to Act1 Slavers with the exception of behavior arrangement
    public BGBlueSlaver(float x, float y) {
        this(x, y, "W2d");
    }

    public BGBlueSlaver(float x, float y, String behavior) {
        super(NAME, "BGBlueSlaver", 50, 0.0F, 0.0F, 170.0F, 230.0F, null, x, y);
        setHp(10);
        this.behavior = behavior;

        this.damage.add(new DamageInfo((AbstractCreature) this, 2));
        this.damage.add(new DamageInfo((AbstractCreature) this, 3));

        loadAnimation(
            "images/monsters/theBottom/blueSlaver/skeleton.atlas",
            "images/monsters/theBottom/blueSlaver/skeleton.json",
            1.0F
        );

        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1: //stab (d)
                playSfx();
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
                    )
                );
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        1,
                        false,
                        true
                    )
                );
                break;
            case 2: //stab (D)
                playSfx();
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(1),
                        AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
                    )
                );
                addToBot(
                    (AbstractGameAction) new MakeTempCardInDrawPileAction(
                        (AbstractCard) new BGDazed(),
                        1,
                        false,
                        true
                    )
                );
                break;
            case 3: //rake (W)
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.SLASH_DIAGONAL
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) AbstractDungeon.player,
                        (AbstractCreature) this,
                        (AbstractPower) new BGWeakPower(
                            (AbstractCreature) AbstractDungeon.player,
                            this.weakAmt,
                            true
                        ),
                        this.weakAmt
                    )
                );
                break;
            case 4: //sweep (2)
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
                    )
                );
                break;
            case 5: //sweep (3)
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateSlowAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(1),
                        AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
                    )
                );
                break;
        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new SFXAction("VO_SLAVERBLUE_1A")
            );
        } else {
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new SFXAction("VO_SLAVERBLUE_1B")
            );
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_SLAVERBLUE_2A");
        } else {
            CardCrawlGame.sound.play("VO_SLAVERBLUE_2B");
        }
    }

    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

    public void dieMove(int num) {
        char move = '-';
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2) move = this.behavior.charAt(0);
        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4) move = this.behavior.charAt(1);
        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6) move = this.behavior.charAt(2);

        if (move == 'd') {
            setMove("Stab", (byte) 1, AbstractMonster.Intent.ATTACK_DEBUFF, 2);
        } else if (move == 'D') {
            setMove("Stab", (byte) 2, AbstractMonster.Intent.ATTACK_DEBUFF, 3);
        } else if (move == 'W') {
            setMove(MOVES[0], (byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, 2);
        } else if (move == '2') {
            setMove((byte) 4, AbstractMonster.Intent.ATTACK, 2);
        } else if (move == '3') {
            setMove((byte) 5, AbstractMonster.Intent.ATTACK, 3);
        }
    }

    public void die() {
        super.die();
        playDeathSfx();
    }
}
