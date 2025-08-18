
package BoardGame.monsters.bgexordium;
import BoardGame.actions.BGSpawnTwoGremlinsAction;
import BoardGame.monsters.BGDamageIcons;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
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
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class BGGremlinAngry extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinWarrior");
    public static final String ID = "BGGremlinAngry";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int SCRATCH_DAMAGE = 4;
    private static final int A_2_SCRATCH_DAMAGE = 5;
    private static final byte SCRATCH = 1;
    private static final int HP_MIN = 20;
    private static final int HP_MAX = 24;
    private static final int A_2_HP_MIN = 21;
    private static final int A_2_HP_MAX = 25;
    private boolean leader;
    public BGGremlinAngry(float x, float y, boolean leader) {
        super(NAME, "BGGremlinAngry", 24, -4.0F, 12.0F, 130.0F, 194.0F, null, x, y);
        this.dialogY = 30.0F * Settings.scale;
        this.leader=leader;


        setHp(4);


        this.damage.add(new DamageInfo((AbstractCreature)this, 1));


        loadAnimation("images/monsters/theBottom/angryGremlin/skeleton.atlas", "images/monsters/theBottom/angryGremlin/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        if(this.leader){
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BGSpawnTwoGremlinsAction());
        }
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new AngryPower((AbstractCreature)this, 1)));

    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                if (this.escapeNext) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE)); break;
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage
                        .get(0)).base));
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
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINANGRY_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINANGRY_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINANGRY_1C"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINANGRY_2A");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINANGRY_2B");
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
        setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
    }


//    public void deathReact() {
//        if (this.intent != AbstractMonster.Intent.ESCAPE && !this.isDying) {
//            AbstractDungeon.effectList.add(new SpeechBubble(this.dialogX, this.dialogY, 3.0F, DIALOG[2], false));
//            setMove((byte)99, AbstractMonster.Intent.ESCAPE);
//            createIntent();
//        }
//    }
}


