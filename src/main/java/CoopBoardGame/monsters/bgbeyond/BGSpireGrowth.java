package CoopBoardGame.monsters.bgbeyond;

import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.DieControlledMoves;
import CoopBoardGame.monsters.MixedAttacks;
import CoopBoardGame.thedie.TheDie;
import basemod.ReflectionHacks;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
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

public class BGSpireGrowth extends AbstractBGMonster implements MixedAttacks, DieControlledMoves {

    public static final String ID = "BGSerpent";
    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("Serpent");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private boolean firstMove = true;

    public BGSpireGrowth() {
        super(NAME, "BGSerpent", 170, -10.0F, -35.0F, 480.0F, 430.0F, null, 0.0F, 10.0F);
        loadAnimation(
            "images/monsters/theForest/spireGrowth/skeleton.atlas",
            "images/monsters/theForest/spireGrowth/skeleton.json",
            1.0F
        );

        behavior = "24"; //only checked by bestiary mod

        setHp(28);

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        e.setTimeScale(1.3F);
        this.stateData.setMix("Hurt", "Idle", 0.2F);
        this.stateData.setMix("Idle", "Hurt", 0.2F);

        this.damage.add(new DamageInfo((AbstractCreature) this, 2));
        this.damage.add(new DamageInfo((AbstractCreature) this, 3));
        this.damage.add(new DamageInfo((AbstractCreature) this, 4));
        this.damage.add(new DamageInfo((AbstractCreature) this, 2));
    };

    public void takeTurn() {
        switch (this.nextMove) {
            case 0:
                this.firstMove = false;
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new AnimateFastAttackAction((AbstractCreature) this)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(0),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new WaitAction(0.4F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(1),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );

                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ChangeStateAction(this, "ATTACK")
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new WaitAction(0.4F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(2),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new WaitAction(0.4F)
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new DamageAction(
                        (AbstractCreature) AbstractDungeon.player,
                        this.damage.get(3),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    )
                );
                break;
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
    }

    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }

    public void dieMove(int roll) {
        if (this.firstMove) {
            setMove((byte) 0, AbstractMonster.Intent.STUN);
            return;
        }
        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2 || TheDie.monsterRoll == 3) setMove(
            (byte) 1,
            AbstractMonster.Intent.ATTACK,
            ((DamageInfo) this.damage.get(0)).base
        );
        else if (
            TheDie.monsterRoll == 4 || TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6
        ) setMove((byte) 2, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(2)).base);
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hurt", false);
            this.state.setTimeScale(1.3F);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.setTimeScale(1.3F);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }

    public int intentDmg1() {
        int dmg = (this.nextMove == 1 ? 2 : 4);
        ReflectionHacks.privateMethod(AbstractMonster.class, "calculateDamage", int.class).invoke(
            (AbstractMonster) this,
            dmg
        );
        return ReflectionHacks.getPrivate(
            (AbstractMonster) this,
            AbstractMonster.class,
            "intentDmg"
        );
    }

    public int intentDmg2() {
        int dmg = (this.nextMove == 1 ? 3 : 2);
        ReflectionHacks.privateMethod(AbstractMonster.class, "calculateDamage", int.class).invoke(
            (AbstractMonster) this,
            dmg
        );
        return ReflectionHacks.getPrivate(
            (AbstractMonster) this,
            AbstractMonster.class,
            "intentDmg"
        );
    }

    public boolean isMixedAttacking() {
        return (this.nextMove > 0);
    }
}
