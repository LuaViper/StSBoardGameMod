//TODO: use GenericStrengthUpPower

package BoardGame.monsters.bgcity; 
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGPainfulStabsPower;
import BoardGame.thedie.TheDie;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PainfulStabsPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBookOfStabbing extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGBookOfStabbing";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("BookOfStabbing");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int HP_MIN = 160;

    private static final int HP_MAX = 164;

    private static final int A_2_HP_MIN = 168;
    private static final int A_2_HP_MAX = 172;
    private static final int STAB_DAMAGE = 6;
    private static final int BIG_STAB_DAMAGE = 21;
    private int stabCount = 1; private static final int A_2_STAB_DAMAGE = 7; private static final int A_2_BIG_STAB_DAMAGE = 24; private int stabDmg; private int bigStabDmg; private static final byte STAB = 1; private static final byte BIG_STAB = 2;

    public BGBookOfStabbing() {
        super(NAME, "BGBookOfStabbing", 164, 0.0F, -10.0F, 320.0F, 420.0F, null, 0.0F, 5.0F);
        loadAnimation("images/monsters/theCity/stabBook/skeleton.atlas", "images/monsters/theCity/stabBook/skeleton.json", 1.0F);

        //behavior="131";
        behavior="--";

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        e.setTimeScale(0.8F);

        this.type = AbstractMonster.EnemyType.ELITE;
        this.dialogX = -70.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 12) {
            setHp(36);
        }else if (AbstractDungeon.ascensionLevel >= 1) {
            setHp(35);
        }else{
            setHp(30);
        }

        this.stabDmg = 1;
        this.bigStabDmg = (AbstractDungeon.ascensionLevel<12 ? 3 : 4);


        this.damage.add(new DamageInfo((AbstractCreature)this, this.stabDmg));
        this.damage.add(new DamageInfo((AbstractCreature)this, this.bigStabDmg));


        if(AbstractDungeon.ascensionLevel>=1){
            setMove((byte) 2, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(1)).base,1, false);
        }else{
            setMove((byte) 1, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base,2, true);
        }
    }


    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGPainfulStabsPower((AbstractCreature)this)));
    }

    public void takeTurn() {
        int i;
        switch (this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)this, (AbstractCreature)this, "BGPainful Stabs"));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_BOOK_STAB_" +
                        MathUtils.random(0, 3)));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL, false, true));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new BGPainfulStabsPower((AbstractCreature)this)));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("MONSTER_BOOK_STAB_" +
                        MathUtils.random(0, 3)));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL, false, true));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new StrengthPower((AbstractCreature) this, 1), 1));

                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)2, AbstractMonster.Intent.ATTACK_BUFF,this.damage.get(1).base,1,false));

                break;


            case 2:
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ChangeStateAction(this, "ATTACK_2"));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this, (AbstractCreature) this, (AbstractPower) new StrengthPower((AbstractCreature) this, 1), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)1, AbstractMonster.Intent.ATTACK_BUFF,this.damage.get(0).base,2,true));



                break;
        }


        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    public void changeState(String stateName) {
        switch (stateName) {
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "ATTACK_2":
                this.state.setAnimation(0, "Attack_2", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            this.state.setAnimation(0, "Hit", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }


    protected void getMove(int num) {
        //Moves are set in constructor and thereafter in takeTurn
        //setMove((byte) 0, AbstractMonster.Intent.NONE);

    }
//    public void dieMove(int roll){
//        final Logger logger = LogManager.getLogger(DieControlledMoves.class.getName());
//        char move = '-';
//        if (TheDie.monsterRoll == 1 || TheDie.monsterRoll == 2)
//            move = this.behavior.charAt(0);
//        else if (TheDie.monsterRoll == 3 || TheDie.monsterRoll == 4)
//            move = this.behavior.charAt(1);
//        else if (TheDie.monsterRoll == 5 || TheDie.monsterRoll == 6)
//            move = this.behavior.charAt(2);
//
//        if (move == '1') {
//            setMove((byte) 1, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base,2, true);
//        } else if (move == '3') {
//            setMove((byte) 2, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(1)).base);
//        } else if (move == '1') {
//            setMove((byte) 3, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base, 2, true);
//        }
//    }



    public void die() {
        super.die();
        CardCrawlGame.sound.play("STAB_BOOK_DEATH");
    }
}


