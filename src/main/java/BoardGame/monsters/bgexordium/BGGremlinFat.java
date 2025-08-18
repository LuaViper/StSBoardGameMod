package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class BGGremlinFat extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinFat");
    public static final String ID = "BGGremlinFat";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 13;
    private static final int HP_MAX = 17;
    private static final int A_2_HP_MIN = 14;
    private static final int A_2_HP_MAX = 18;
    private static final int BLUNT_DAMAGE = 4;
    private static final int A_2_BLUNT_DAMAGE = 5;
    private static final int WEAK_AMT = 1;
    private static final byte BLUNT = 2;

    public BGGremlinFat(float x, float y) {
        super(NAME, "BGGremlinFat", 17, 0.0F, 0.0F, 110.0F, 220.0F, null, x, y);
        this.dialogY = 30.0F * Settings.scale;


        setHp(3);
        this.damage.add(new DamageInfo((AbstractCreature)this, 1));

        loadAnimation("images/monsters/theBottom/fatGremlin/skeleton.atlas", "images/monsters/theBottom/fatGremlin/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGWeakPower((AbstractCreature)AbstractDungeon.player, 1, true), 1));






                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE)); break;
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
                break;

            case 99:
                playSfx();
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[1], false));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE));
                break;
        }
    }



    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINFAT_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINFAT_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINFAT_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINFAT_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_GREMLINFAT_2B");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINFAT_2C");
        }
    }


    public void die() {
        super.die();
        playDeathSfx();
    }


    public void escapeNext() {
        if (!this.cannotEscape &&
                !this.escapeNext) {
            this.escapeNext = true;
            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
        }
    }




    protected void getMove(int num) {
        setMove(MOVES[0], (byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
    }



//    public void deathReact() {
//        if (this.intent != AbstractMonster.Intent.ESCAPE && !this.isDying) {
//            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
//            setMove((byte)99, AbstractMonster.Intent.ESCAPE);
//            createIntent();
//        }
//    }
}


