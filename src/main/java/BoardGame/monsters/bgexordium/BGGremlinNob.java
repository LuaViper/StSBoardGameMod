package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;

import BoardGame.powers.BGAngerPower;
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
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class BGGremlinNob extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGGremlinNob";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinNob");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 82;

    private static final int HP_MAX = 86;

    private static final int A_2_HP_MIN = 85;

    private static final int A_2_HP_MAX = 90;

    private static final int BASH_DMG = 6;

    private static final int RUSH_DMG = 14;
    private static final int A_2_BASH_DMG = 8;
    private static final int A_2_RUSH_DMG = 16;

    public BGGremlinNob(float x, float y) {
        this(x, y, false);
    }
    private static final int DEBUFF_AMT = 2; private int bashDmg; private int rushDmg; private static final byte BULL_RUSH = 1; private static final byte SKULL_BASH = 2; private static final byte BELLOW = 3; private static final int ANGRY_LEVEL = 2; private boolean usedBellow = false; private boolean canVuln;
    public BGGremlinNob(float x, float y, boolean setVuln) {
        super(NAME, "GremlinNob", 86, -70.0F, -10.0F, 270.0F, 380.0F, null, x, y);
        this.intentOffsetX = -30.0F * Settings.scale;
        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -60.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;
        this.canVuln = setVuln;

        //setHp(17);
        setHp(15);

        this.rushDmg = 3;
        this.bashDmg = 3;

        this.damage.add(new DamageInfo((AbstractCreature)this, this.rushDmg));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.bashDmg));

        loadAnimation("images/monsters/theBottom/nobGremlin/skeleton.atlas", "images/monsters/theBottom/nobGremlin/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }









    public void takeTurn() {
        switch (this.nextMove) {
            case 3:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[0], 1.0F, 3.0F));
                if (AbstractDungeon.ascensionLevel >= 18) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGAngerPower((AbstractCreature)this, 1), 1));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGAngerPower((AbstractCreature)this, 1), 1));
                break;


            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                if (this.canVuln) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new VulnerablePower((AbstractCreature)AbstractDungeon.player, 2, true), 2));
                }
                break;





            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
        }


        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINNOB_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINNOB_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINNOB_1C"));
        }
    }




    protected void getMove(int num) {
        if (!this.usedBellow) {
            this.usedBellow = true;
            setMove((byte)3, AbstractMonster.Intent.BUFF);

            return;
        }
        if (AbstractDungeon.ascensionLevel >= 18) {
            if (!lastMove((byte)2) && !lastMoveBefore((byte)2)) {
                if (this.canVuln) {
                    setMove(MOVES[0], (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                } else {
                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                }
                return;
            }
            if (lastTwoMoves((byte)1)) {
                if (this.canVuln) {
                    setMove(MOVES[0], (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                } else {
                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                }
            } else {
                setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
            }
        } else {

            if (num < 33) {
                if (this.canVuln) {
                    setMove(MOVES[0], (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                } else {
                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                }

                return;
            }

            if (lastTwoMoves((byte)1)) {
                if (this.canVuln) {
                    setMove(MOVES[0], (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                } else {
                    setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                }
            } else {
                setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
            }
        }
    }
}


