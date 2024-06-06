//TODO: on A1+, should Taskmaster's 2nd action be ATTACK_DEBUFF or ATTACK_BUFF?

package BoardGame.monsters.bgcity; 
 import BoardGame.cards.BGStatus.BGDazed;
 import BoardGame.monsters.BGDamageIcons;
import BoardGame.monsters.AbstractBGMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
 import com.megacrit.cardcrawl.actions.common.*;
 import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGTaskmaster extends AbstractBGMonster implements BGDamageIcons {
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SlaverBoss");
    public static final String ID = "BGSlaverBoss";
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG; private static final int HP_MIN = 54; private static final int HP_MAX = 60;
    private static final int A_2_HP_MIN = 57;
    private static final int A_2_HP_MAX = 64;
    private static final int WHIP_DMG = 4;
    private static final int SCOURING_WHIP_DMG = 7;
    private static final int WOUNDS = 1;
    private static final int A_2_WOUNDS = 2;
    private int woundCount;
    private static final byte SCOURING_WHIP = 2;

    public BGTaskmaster(float x, float y) {
        super(NAME, "BGSlaverBoss", AbstractDungeon.monsterHpRng.random(54, 60), -10.0F, -8.0F, 200.0F, 280.0F, null, x, y);
        this.type = AbstractMonster.EnemyType.ELITE;

        if(AbstractDungeon.ascensionLevel>=12) {
            setHp(16);
        }else if(AbstractDungeon.ascensionLevel>1){
            setHp(15);
        }else{
            setHp(13);
        }


        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 1));
        this.damage.add(new DamageInfo((AbstractCreature)this, 2));
        this.damage.add(new DamageInfo((AbstractCreature)this, 1));

        loadAnimation("images/monsters/theCity/slaverMaster/skeleton.atlas", "images/monsters/theCity/slaverMaster/skeleton.json", 1.0F);

        if(AbstractDungeon.ascensionLevel>=12) {
            setMove((byte) 3, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(3)).base,1, false);
        }else if(AbstractDungeon.ascensionLevel>=1){
            setMove((byte) 1, AbstractMonster.Intent.ATTACK, ((DamageInfo) this.damage.get(1)).base,1, false);
        }else{
            setMove((byte) 0, AbstractMonster.Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(0)).base,1, false);
        }

        AnimationState.TrackEntry e = this.state.setAnimation(0, "idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }


    public void takeTurn() {
        switch (this.nextMove) {
            case 0:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)0, AbstractMonster.Intent.ATTACK_BUFF,this.damage.get(0).base,1,false));
                break;
            case 1:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF,this.damage.get(2).base,1,false));
                break;
            case 2:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(2), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)2, AbstractMonster.Intent.ATTACK_DEBUFF,this.damage.get(2).base,1,false));
                break;
            case 3:
                playSfx();
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new AnimateSlowAttackAction((AbstractCreature)this));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, this.damage
                        .get(3), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)this, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)this, 1), 1));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SetMoveAction(this,  (byte)3, AbstractMonster.Intent.ATTACK_DEBUFF,this.damage.get(3).base,1,false));
                break;

        }



        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(this));
    }


    protected void getMove(int num) {
        //setMove((byte)2, AbstractMonster.Intent.ATTACK_BUFF, 1);
        //Moves are set in constructor and thereafter in takeTurn

    }

    private void playSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_SLAVERLEADER_1A"));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("VO_SLAVERLEADER_1B"));
        }
    }

    private void playDeathSfx() {
        int roll = MathUtils.random(1);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_SLAVERLEADER_2A");
        } else {
            CardCrawlGame.sound.play("VO_SLAVERLEADER_2B");
        }
    }


    public void die() {
        super.die();
        playDeathSfx();
    }
}


