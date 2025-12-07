package CoopBoardGame.monsters.bgexordium;

import CoopBoardGame.monsters.AbstractBGMonster;
import CoopBoardGame.monsters.BGDamageIcons;
import CoopBoardGame.powers.BGAngerPower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGGremlinNob extends AbstractBGMonster implements BGDamageIcons {

    public static final String ID = "BGGremlinNob";
    private static final MonsterStrings monsterStrings =
        CardCrawlGame.languagePack.getMonsterStrings("GremlinNob");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public BGGremlinNob(float x, float y) {
        this(x, y, false);
    }

    private int bashDmg;
    private int rushDmg;
    private boolean usedBellow = false;

    public BGGremlinNob(float x, float y, boolean setVuln) {
        super(NAME, "BGGremlinNob", 86, -70.0F, -10.0F, 270.0F, 380.0F, null, x, y);
        this.intentOffsetX = -30.0F * Settings.scale;
        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -60.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        if (AbstractDungeon.ascensionLevel == 0) setHp(14);
        else if (AbstractDungeon.ascensionLevel < 12) setHp(17);
        else setHp(19);

        this.rushDmg = 1; //A12 first turn
        this.bashDmg = 3; //default attack

        this.damage.add(new DamageInfo((AbstractCreature) this, this.rushDmg));
        this.damage.add(new DamageInfo((AbstractCreature) this, this.bashDmg));

        loadAnimation(
            "images/monsters/theBottom/nobGremlin/skeleton.atlas",
            "images/monsters/theBottom/nobGremlin/skeleton.json",
            1.0F
        );

        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 3: //initial buff
                playSfx();
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new TalkAction(
                        (AbstractCreature) this,
                        DIALOG[0],
                        1.0F,
                        3.0F
                    )
                );
                AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) this,
                        (AbstractCreature) this,
                        (AbstractPower) new BGAngerPower((AbstractCreature) this, 1),
                        1
                    )
                );
                if (AbstractDungeon.ascensionLevel >= 12) {
                    AbstractDungeon.actionManager.addToBottom(
                        (AbstractGameAction) new DamageAction(
                            (AbstractCreature) AbstractDungeon.player,
                            this.damage.get(0),
                            AbstractGameAction.AttackEffect.BLUNT_HEAVY
                        )
                    );
                }
                break;
            case 2:
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
                if (AbstractDungeon.ascensionLevel >= 1) AbstractDungeon.actionManager.addToBottom(
                    (AbstractGameAction) new ApplyPowerAction(
                        (AbstractCreature) this,
                        (AbstractCreature) this,
                        (AbstractPower) new StrengthPower(this, 1),
                        1
                    )
                );
                break;
        }

        AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new SFXAction("VO_GREMLINNOB_1A")
            );
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new SFXAction("VO_GREMLINNOB_1B")
            );
        } else {
            AbstractDungeon.actionManager.addToBottom(
                (AbstractGameAction) new SFXAction("VO_GREMLINNOB_1C")
            );
        }
    }

    protected void getMove(int num) {
        if (!this.usedBellow) {
            this.usedBellow = true;
            if (AbstractDungeon.ascensionLevel < 12) {
                setMove((byte) 3, AbstractMonster.Intent.BUFF);
            } else {
                setMove(
                    (byte) 3,
                    AbstractMonster.Intent.ATTACK_BUFF,
                    ((DamageInfo) this.damage.get(0)).base
                );
            }

            return;
        }
        if (AbstractDungeon.ascensionLevel >= 1) {
            setMove(
                (byte) 2,
                AbstractMonster.Intent.ATTACK_BUFF,
                ((DamageInfo) this.damage.get(1)).base
            );
        } else {
            setMove(
                (byte) 2,
                AbstractMonster.Intent.ATTACK,
                ((DamageInfo) this.damage.get(1)).base
            );
        }
    }
}
