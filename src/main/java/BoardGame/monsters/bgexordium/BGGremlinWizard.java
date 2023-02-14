package BoardGame.monsters.bgexordium;
import BoardGame.monsters.BGDamageIcons;


import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class BGGremlinWizard extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGGremlinWizard";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinWizard");

    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final int HP_MIN = 21;
    private static final int HP_MAX = 25;
    private static final int A_2_HP_MIN = 22;
    private static final int A_2_HP_MAX = 26;
    private static final int MAGIC_DAMAGE = 25;
    private static final int A_2_MAGIC_DAMAGE = 30;
    private static final int CHARGE_LIMIT = 3;
    private int currentCharge = 1;
    private static final byte DOPE_MAGIC = 1;
    private boolean spoken_charging=false;
    private boolean spoken_hereitcomes=false;

    public BGGremlinWizard(float x, float y) {
        super(NAME, "BGGremlinWizard", 25, 40.0F, -5.0F, 130.0F, 180.0F, null, x - 35.0F, y);

        this.dialogX = 0.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;


        setHp(4);



        this.damage.add(new DamageInfo((AbstractCreature)this, 3));


        loadAnimation("images/monsters/theBottom/wizardGremlin/skeleton.atlas", "images/monsters/theBottom/wizardGremlin/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }
    private static final byte CHARGE = 2;

    public void takeTurn() {
        switch (this.nextMove) {
            case 2:
                this.currentCharge++;


//                if (this.escapeNext) {
//                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE)); break;
//                }
                //if (this.currentCharge == 3) {
                if(true){
                    playSfx();
                    //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[2], 1.5F, 3.0F));
                    if(!spoken_hereitcomes) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, DIALOG[2], 1.5F, 3.0F));
                        spoken_hereitcomes=true;
                    }
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, MOVES[1], (byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage
                            .get(0)).base)); break;
                }
                //setMove(MOVES[0], (byte)2, AbstractMonster.Intent.UNKNOWN);
                break;


            case 1:
                this.currentCharge = 0;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.FIRE));
//                if (this.escapeNext) {
//                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE)); break;
//                }
                //if (AbstractDungeon.ascensionLevel >= 17) {
                if(true){
                    setMove(MOVES[1], (byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base); break;
                }
                //setMove(MOVES[0], (byte)2, AbstractMonster.Intent.UNKNOWN);
                break;


            case 99:
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[3], false));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)99, AbstractMonster.Intent.ESCAPE));
                break;
        }
    }



    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINDOPEY_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_GREMLINDOPEY_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_GREMLINDOPEY_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_GREMLINDOPEY_2B");
        } else {
            CardCrawlGame.sound.play("VO_GREMLINDOPEY_2C");
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
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 3.0F, DIALOG[4], false));
        }
    }




    protected void getMove(int num) {
        if(!spoken_charging) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new TextAboveCreatureAction((AbstractCreature) this, DIALOG[1]));
            spoken_charging = true;
        }

        setMove(MOVES[0], (byte)2, AbstractMonster.Intent.UNKNOWN);
    }


//    public void deathReact() {
//        if (this.intent != AbstractMonster.Intent.ESCAPE && !this.isDying) {
//            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 3.0F, DIALOG[4], false));
//            setMove((byte)99, AbstractMonster.Intent.ESCAPE);
//            createIntent();
//        }
//    }
}


