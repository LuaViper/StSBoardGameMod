package BoardGame.monsters.bgcity;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.actions.BGFakeStealGoldDamageAction;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.powers.BGThieveryPower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
 import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
 import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

public class BGMugger extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Mugger"); public static final String ID = "Mugger";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 48; private static final int HP_MAX = 52;
    private static final int A_2_HP_MIN = 50;
    private static final int A_2_HP_MAX = 54;
    public static final String ENCOUNTER_NAME = "City Looters";
    private int swipeDmg;
    private int bigSwipeDmg;
    private int goldAmt;

    private int fakeGoldAmt=15;
    private int escapeDef = 2; private static final byte MUG = 1; private static final byte SMOKE_BOMB = 2; private static final byte ESCAPE = 3;
    private static final byte BIGSWIPE = 4;
    private static final String SLASH_MSG1 = DIALOG[0];
    private static final String RUN_MSG = DIALOG[1];
    private int slashCount = 0;
    private int stolenGold = 0;

    public BGMugger(float x, float y) {
        super(NAME, "BGMugger", 52, 0.0F, 0.0F, 200.0F, 220.0F, null, x, y);
        this.dialogX = -30.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;


        this.goldAmt = 2;
        setHp(10);


        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 4));

        loadAnimation("images/monsters/theCity/looterAlt/skeleton.atlas", "images/monsters/theCity/looterAlt/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGThieveryPower((AbstractCreature)this, this.goldAmt)));
    }



    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                playSfx();
                this.slashCount++;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BGFakeStealGoldDamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), this.fakeGoldAmt));

                if (AbstractDungeon.aiRng.randomBoolean(0.6F)) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, SLASH_MSG1, 0.3F, 2.0F));
                }

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, 1));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)2, AbstractMonster.Intent.ATTACK, 2));
                break;
            case 2:



                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BGFakeStealGoldDamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), this.fakeGoldAmt));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)3, AbstractMonster.Intent.ATTACK_DEFEND, 4));
                break;
            case 3:
                playSfx();
                this.slashCount++;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BGFakeStealGoldDamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), this.fakeGoldAmt));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new GainBlockAction((AbstractCreature)this, (AbstractCreature)this, this.escapeDef));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)4, AbstractMonster.Intent.ESCAPE));
                break;
            case 4:
                int goldAmt=this.goldAmt;
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction()
                {
                    public void update()
                    {
                        if (AbstractDungeon.player.gold > goldAmt) {
                            AbstractDungeon.player.gold -= goldAmt;
                        }else{
                            AbstractDungeon.player.gold=0;
                        }
                        this.isDone=true;
                    }
                });
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new TalkAction((AbstractCreature)this, RUN_MSG, 0.3F, 2.5F));
                (AbstractDungeon.getCurrRoom()).mugged = true;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new SmokeBombEffect(this.hb.cX, this.hb.cY)));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new EscapeAction(this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this, (byte)3, AbstractMonster.Intent.ESCAPE));


                break;
        }
    }



    private void playSfx() {
        int roll = AbstractDungeon.aiRng.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_MUGGER_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_MUGGER_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = AbstractDungeon.aiRng.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_MUGGER_2A");
        } else {
            CardCrawlGame.sound.play("VO_MUGGER_2B");
        }
    }


    public void die() {
        playDeathSfx();
        this.state.setTimeScale(0.1F);
        useShakeAnimation(5.0F);
//        if (this.stolenGold > 0) {
//            AbstractDungeon.getCurrRoom().addStolenGoldToRewards(this.stolenGold);
//        }
        super.die();
    }


    protected void getMove(int num) {
        setMove((byte)1, AbstractMonster.Intent.ATTACK_DEFEND, ((DamageInfo)this.damage.get(0)).base);
    }
}


