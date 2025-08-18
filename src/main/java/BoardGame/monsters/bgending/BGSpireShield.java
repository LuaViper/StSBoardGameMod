
package BoardGame.monsters.bgending;

import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.BGDamageIcons;
import BoardGame.powers.BGDifferentRowsPower;
import BoardGame.powers.BGSurroundedPower;
import BoardGame.powers.BGZeroDamagePower;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.Collections;

public class BGSpireShield extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGSpireShield";
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("SpireShield");
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private int moveCount = 0;
    private int strAmt=2;
    private static final byte BASH = 1;
    private static final byte FORTIFY = 2;
    private static final byte SMASH = 3;
    private static final int BASH_DEBUFF = -1;
    private static final int FORTIFY_BLOCK = 30;


    public BGSpireShield(float offsetx, float offsety) {
        super(NAME, "BGSpireShield", AbstractDungeon.monsterHpRng.random(180, 190), 0.0F, -30.0F, 220.0F, 320.0F, null, -20.0F + offsetx, 10.0F + offsety);
        this.type = AbstractMonster.EnemyType.ELITE;
        loadAnimation("images/monsters/theEnding/shield/skeleton.atlas", "images/monsters/theEnding/shield/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.1F);
        setHp(30);
        this.damage.add(new DamageInfo(this, 8));

        this.flipHorizontal = true;
    }

    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction( AbstractDungeon.player,  this,
                new BGSurroundedPower( AbstractDungeon.player)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction( this,  this,
                new BGDifferentRowsPower( this)));
    }

    public void takeTurn(){
        switch (this.nextMove) {
            case 0:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "OLD_ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.5F));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, 20));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(
                        this,  (byte)1, AbstractMonster.Intent.ATTACK, 8));
                break;
            case 1:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.35F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(
                        this,  (byte)2, AbstractMonster.Intent.BUFF));
                break;
            case 2:
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)this, (AbstractPower)new StrengthPower((AbstractCreature)m, this.strAmt), this.strAmt));
                    }
                }
                AbstractDungeon.actionManager.addToBottom(new SetMoveAction(
                        this, (byte) 0, AbstractMonster.Intent.DEFEND));

                break;
        }


    }

    public void getMove(int i){
        setMove((byte)0, AbstractMonster.Intent.DEFEND);
    }


    public void facingAttack(){
        if(isDying || isDead || halfDead)return;
        if(this.nextMove==0){
            addToTop(new LoseEnergyAction(1));
        }else if(this.nextMove==1){
            addToTop(new ApplyPowerAction(AbstractDungeon.player,this,
                    new NoDrawPower(AbstractDungeon.player)));
        }else if(this.nextMove==2){
            addToTop(new ApplyPowerAction(AbstractDungeon.player,this,
                    new BGZeroDamagePower(AbstractDungeon.player)));

        }else{
            addToTop(new LoseEnergyAction(1));
        }
    }

    public void changeState(String key) {
        switch (key) {
            case "OLD_ATTACK":
                this.state.setAnimation(0, "old_attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
                break;
            case "ATTACK":
                this.state.setAnimation(0, "Attack", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
        }

    }

//    public void damage(DamageInfo info) {
//        super.damage(info);
//        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
//            this.state.setAnimation(0, "Hit", false);
//            this.state.addAnimation(0, "Idle", true, 0.0F);
//        }
//    }



    @SpirePatch(clz = MonsterGroup.class, method = "update")
    public static class HitboxOrderPatch1 {
        @SpirePrefixPatch
        private static void Foo(MonsterGroup __instance) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                Collections.reverse(__instance.monsters);
            }
        }
    }
    @SpirePatch(clz = MonsterGroup.class, method = "update")
    public static class HitboxOrderPatch2 {
        @SpirePostfixPatch
        private static void Foo(MonsterGroup __instance) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                Collections.reverse(__instance.monsters);
            }
        }
    }


    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
    public static class HitboxOrderPatch3 {
        @SpirePrefixPatch
        private static void Foo(AbstractPlayer __instance) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                if(AbstractDungeon.getMonsters()!=null){
                    Collections.reverse(AbstractDungeon.getMonsters().monsters);
                }
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
    public static class HitboxOrderPatch4 {
        @SpirePostfixPatch
        private static void Foo(AbstractPlayer __instance) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                if(AbstractDungeon.getMonsters()!=null){
                    Collections.reverse(AbstractDungeon.getMonsters().monsters);
                }
            }
        }
    }
}

