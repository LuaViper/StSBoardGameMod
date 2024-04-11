package BoardGame.actions;

import BoardGame.monsters.AbstractBGMonster;
import BoardGame.monsters.MixedAttacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class BGFlameBarrierAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("OpeningAction");
    public static final String[] TEXT = uiStrings.TEXT;

    private int thornsDamage;
    private AbstractPlayer player;

    public BGFlameBarrierAction(int thornsDamage, AbstractPlayer player) {
        this.duration = 0.0F;
        this.actionType = AbstractGameAction.ActionType.WAIT;
        this.thornsDamage = thornsDamage;
        this.player=player;
        //this.targetMonster = m;
    }


    public void update() {
        for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            //avoid attacking dead monsters (and corresponding visual fx)
            if (!mo.isDying && !mo.isDead) {
                if (mo.getIntentBaseDmg() >= 0) {
                    //p.triggerMarks(this.card);
                    EnemyMoveInfo move = AbstractBGMonster.Field.publicMove.get(mo);
                    if (move != null) {
                        if (move.isMultiDamage && move.multiplier > 1) {
                            for (int i = 1; i <= move.multiplier; i += 1) {
                                addToBot((AbstractGameAction) new DamageAction((AbstractCreature) mo, new DamageInfo((AbstractCreature) this.player, this.thornsDamage, DamageInfo.DamageType.THORNS), AttackEffect.FIRE));
                            }
                        } else if(mo instanceof MixedAttacks && ((MixedAttacks)mo).isMixedAttacking()){
                            for (int i = 1; i <= 2; i += 1) {
                                addToBot((AbstractGameAction) new DamageAction((AbstractCreature) mo, new DamageInfo((AbstractCreature) this.player, this.thornsDamage, DamageInfo.DamageType.THORNS), AttackEffect.FIRE));
                            }
                        } else {
                            addToBot((AbstractGameAction) new DamageAction((AbstractCreature) mo, new DamageInfo((AbstractCreature) this.player, this.thornsDamage, DamageInfo.DamageType.THORNS), AttackEffect.FIRE));
                        }
                    }
                }
            }
        }
        this.isDone = true;


    }
}


