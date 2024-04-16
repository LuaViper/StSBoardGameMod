package BoardGame.monsters.bgending;

import BoardGame.cards.BGStatus.BGBurn;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.BGDamageIcons;
import BoardGame.multicharacter.BGMultiCreature;
import BoardGame.powers.BGDifferentRowsPower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSpireSpear extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGSpireSpear";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SpireSpear");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private int moveCount = 0;

    private static final byte BURN_STRIKE = 1;

    private static final byte PIERCER = 2;

    private static final byte SKEWER = 3;

    private static final int BURN_STRIKE_COUNT = 2;

    private int skewerCount;

    public BGSpireSpear(float offsetx,float offsety) {
        super(NAME, "BGSpireSpear", 160, 0.0F, -15.0F, 380.0F, 290.0F, null, 70.0F+offsetx, 10.0F+offsety);
        this.type = AbstractMonster.EnemyType.ELITE;
        loadAnimation("images/monsters/theEnding/spear/skeleton.atlas", "images/monsters/theEnding/spear/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.7F);

        BGMultiCreature.Field.currentRow.set(this,1);

        setHp(42);

        this.skewerCount = 2;
        this.damage.add(new DamageInfo(this, 2));
        this.damage.add(new DamageInfo(this, 9));

    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction( this,  this,
                new BGDifferentRowsPower( this)));
    }

    public void takeTurn(){
        int i;
        switch (this.nextMove) {
            case 0:
                for (i = 0; i < 2; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.15F));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player,  this.damage.get(0), AbstractGameAction.AttackEffect.FIRE));
                }

                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(
                        this,  (byte)1, AbstractMonster.Intent.DEBUFF));
                break;
            case 1:
                addToBot(new MakeTempCardInDrawPileAction(new BGDazed(), 2, false, true));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(
                        this,  (byte)2, AbstractMonster.Intent.ATTACK,9));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.05F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL, true));

                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(
                        this,  (byte)0, AbstractMonster.Intent.ATTACK,2,2,true));

                break;
        }


    }

    public void getMove(int i){
        setMove( (byte)0, AbstractMonster.Intent.ATTACK,2,2,true);
    }

    public void facingAttack(){
        if(isDying || isDead || halfDead)return;
        AbstractCard c = new BGBurn();
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDiscardAction((AbstractCard)c, 2));
    }


    public void changeState(String key) {
        AnimationState.TrackEntry e = null;
        switch (key) {
            case "SLOW_ATTACK":
                this.state.setAnimation(0, "Attack_1", false);
                e = this.state.addAnimation(0, "Idle", true, 0.0F);
                e.setTimeScale(0.5F);
                break;
            case "ATTACK":
                this.state.setAnimation(0, "Attack_2", false);
                e = this.state.addAnimation(0, "Idle", true, 0.0F);
                e.setTimeScale(0.7F);
        }
    }

//    public void damage(DamageInfo info) {
//        super.damage(info);
//        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
//            this.state.setAnimation(0, "Hit", false);
//            AnimationState.TrackEntry e = this.state.addAnimation(0, "Idle", true, 0.0F);
//            e.setTimeScale(0.7F);
//        }
//    }

}
