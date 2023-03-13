package BoardGame.monsters.bgbeyond;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGShiftingPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import BoardGame.monsters.AbstractBGMonster;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FadingPower;
import com.megacrit.cardcrawl.powers.ShiftingPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGTransient extends AbstractBGMonster {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Transient");
    public static final String ID = "BGTransient";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP = 999;
    private int count = 0;
    private static final int DEATH_DMG = 30;
    private static final int INCREMENT_DMG = 10;
    private static final int A_2_DEATH_DMG = 40;
    private int startingDeathDmg;
    private static final byte ATTACK = 1;

    public BGTransient() {
        super(NAME, "BGTransient", 999, 0.0F, -15.0F, 370.0F, 340.0F, null, 0.0F, 20.0F);
        loadAnimation("images/monsters/theForest/transient/skeleton.atlas", "images/monsters/theForest/transient/skeleton.json", 1.0F);




        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.gold = 1;
        this.dialogX = -100.0F * Settings.scale;
        this.dialogY -= 20.0F * Settings.scale;

//        if (AbstractDungeon.ascensionLevel >= 2) {
//            this.startingDeathDmg = 40;
//        } else {
//            this.startingDeathDmg = 30;
//        }

        setHp(99);

        this.damage.add(new DamageInfo((AbstractCreature)this, 6));
        this.damage.add(new DamageInfo((AbstractCreature)this, 9));
        this.damage.add(new DamageInfo((AbstractCreature)this, 12));
        this.damage.add(new DamageInfo((AbstractCreature)this, 15));
        this.damage.add(new DamageInfo((AbstractCreature)this, 18));
        this.damage.add(new DamageInfo((AbstractCreature)this, 21));
        this.damage.add(new DamageInfo((AbstractCreature)this, 24));
        this.damage.add(new DamageInfo((AbstractCreature)this, 27));
        this.damage.add(new DamageInfo((AbstractCreature)this, 30));
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new FadingPower((AbstractCreature)this, 4)));
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGShiftingPower((AbstractCreature)this)));
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.4F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(this.count), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                this.count++;
                setMove((byte)1, AbstractMonster.Intent.ATTACK, this.damage.get(this.count).base);
                break;
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hurt", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    public void changeState(String key) {
        switch (key) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }




    public void die() {
        super.die();
        UnlockTracker.unlockAchievement("TRANSIENT");
    }


    protected void getMove(int num) {
        setMove((byte)1, AbstractMonster.Intent.ATTACK, this.damage.get(0).base);
    }
}


