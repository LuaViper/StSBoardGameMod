package BoardGame.monsters.bgexordium;
import BoardGame.actions.BGSpawnTwoGremlinsAction;
import BoardGame.actions.BGSpawnTwoGremlinsForGremlinLeaderAction;
import BoardGame.monsters.BGDamageIcons;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
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

public class BGGremlinSneaky extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGGremlinSneaky";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinThief");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int THIEF_DAMAGE = 9;

    private static final int A_2_THIEF_DAMAGE = 10;

    private static final byte PUNCTURE = 1;
    private static final int HP_MIN = 10;

    private boolean leader;
    public BGGremlinSneaky(float x, float y, boolean leader) {
        super(NAME, "BGGremlinSneaky", 14, 0.0F, 0.0F, 120.0F, 160.0F, null, x, y);
        this.dialogY = 50.0F * Settings.scale;
        this.leader=leader;

        setHp(2);

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));

        loadAnimation("images/monsters/theBottom/thiefGremlin/skeleton.atlas", "images/monsters/theBottom/thiefGremlin/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }
    private static final int HP_MAX = 14; private static final int A_2_HP_MIN = 11; private static final int A_2_HP_MAX = 15; private int thiefDamage;

    public void usePreBattleAction() {
        if (this.leader) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new BGSpawnTwoGremlinsAction());
        }
    }

    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

                if (true) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)1, AbstractMonster.Intent.ATTACK, 2));
                    break;
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE));
                break;

//            case 99:
//                playSfx();
//                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[1], false));
//
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new EscapeAction(this));
//                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE));
//                break;
        }
    }



    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINSPAZZY_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINSPAZZY_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINSPAZZY_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_GREMLINSPAZZY_2B");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINSPAZZY_2C");
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


