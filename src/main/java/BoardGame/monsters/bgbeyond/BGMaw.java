package BoardGame.monsters.bgbeyond;
import BoardGame.BoardGame;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGThieveryPower;
import BoardGame.powers.BGVulnerablePower;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGMaw extends AbstractBGMonster {
    public static final String ID = "BGMaw";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Maw");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES; private static final int HP = 300; private static final float HB_X = 0.0F; private static final float HB_Y = -40.0F; private static final float HB_W = 430.0F; private static final float HB_H = 360.0F; private static final int SLAM_DMG = 25; private static final int NOM_DMG = 5; private static final int A_2_SLAM_DMG = 30;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private boolean roared = false;
    private int turnCount = 1; private int strUp; private int terrifyDur;

    public BGMaw(float x, float y) {
        super(NAME, "BGMaw", 300, 0.0F, -40.0F, 430.0F, 360.0F, null, x, y);

        loadAnimation("images/monsters/theForest/maw/skeleton.atlas", "images/monsters/theForest/maw/skeleton.json", 1.0F);



        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.dialogX = -160.0F * Settings.scale;
        this.dialogY = 40.0F * Settings.scale;

//        this.strUp = 3;
//        this.terrifyDur = 3;
//
//        if (AbstractDungeon.ascensionLevel >= 17) {
//            this.strUp += 2;
//            this.terrifyDur += 2;
//        }
//
//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.slamDmg = 30;
//            this.nomDmg = 5;
//        } else {
//            this.slamDmg = 25;
//            this.nomDmg = 5;
//        }

        setHp(28);

        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 6));
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MAW_DEATH", 0.1F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)this, (AbstractPower)new BGVulnerablePower((AbstractCreature)AbstractDungeon.player, 1, true), 1));

                this.roared = true;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)2, AbstractMonster.Intent.ATTACK,2,3,true));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));

                for (i = 0; i < 3; i++) {
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new BiteEffect(AbstractDungeon.player.hb.cX +


                            MathUtils.random(-50.0F, 50.0F) * Settings.scale, AbstractDungeon.player.hb.cY +
                            MathUtils.random(-50.0F, 50.0F) * Settings.scale, Color.SKY
                            .cpy())));

                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                            .get(0), AbstractGameAction.AttackEffect.NONE));
                }
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)3, AbstractMonster.Intent.BUFF));
                break;
            case 3:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 2), 2));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)4, AbstractMonster.Intent.ATTACK,6));
                break;
            case 4:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)2, AbstractMonster.Intent.ATTACK,2,3,true));
                break;



        }

        //AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    protected void getMove(int num) {
        if (this.turnCount==1) {
            setMove((byte)1, AbstractMonster.Intent.STRONG_DEBUFF);
            this.turnCount++;
        }else{
            setMove((byte)0, AbstractMonster.Intent.NONE);
        }
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ShoutAction((AbstractCreature)this, DIALOG[0], 1.0F, 2.0F));
    }





    public void die() {
        super.die();
        CardCrawlGame.sound.play("MAW_DEATH");
    }
}


