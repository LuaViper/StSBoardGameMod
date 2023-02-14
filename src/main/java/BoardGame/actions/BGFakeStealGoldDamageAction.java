package BoardGame.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class BGFakeStealGoldDamageAction
        extends AbstractGameAction {
    private DamageInfo info;
    private int goldAmount = 0;
    private static final float DURATION = 0.1F;
    private static final float POST_ATTACK_WAIT_DUR = 0.1F;
    private boolean skipWait = false, muteSfx = false;

    public BGFakeStealGoldDamageAction(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        this.info = info;
        setValues(target, info);
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = effect;
        this.duration = 0.1F;
    }

    public BGFakeStealGoldDamageAction(AbstractCreature target, DamageInfo info, int stealGoldAmount) {
        this(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        this.goldAmount = stealGoldAmount;
    }





    public void update() {
        if (shouldCancelAction() && this.info.type != DamageInfo.DamageType.THORNS) {
            this.isDone = true;

            return;
        }
        if (this.duration == 0.1F) {

            if (this.info.type != DamageInfo.DamageType.THORNS && (
                    this.info.owner.isDying || this.info.owner.halfDead)) {
                this.isDone = true;

                return;
            }
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, this.attackEffect, this.muteSfx));


            stealGold();

        }

        tickDuration();

        if (this.isDone) {
            if (this.attackEffect == AbstractGameAction.AttackEffect.POISON) {
                this.target.tint.color.set(Color.CHARTREUSE.cpy());
                this.target.tint.changeColor(Color.WHITE.cpy());
            } else if (this.attackEffect == AbstractGameAction.AttackEffect.FIRE) {
                this.target.tint.color.set(Color.RED);
                this.target.tint.changeColor(Color.WHITE.cpy());
            }
            this.target.damage(this.info);

            if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
            if (!this.skipWait && !Settings.FAST_MODE) {
                addToTop((AbstractGameAction)new WaitAction(0.1F));
            }
        }
    }




    private void stealGold() {

        CardCrawlGame.sound.play("GOLD_JINGLE");

        for (int i = 0; i < this.goldAmount; i++) {
            if (this.source.isPlayer) {
                AbstractDungeon.effectList.add(new GainPennyEffect(this.target.hb.cX, this.target.hb.cY));
            } else {
                AbstractDungeon.effectList.add(new GainPennyEffect(this.source, this.target.hb.cX, this.target.hb.cY, this.source.hb.cX, this.source.hb.cY, false));
            }
        }
    }
}


