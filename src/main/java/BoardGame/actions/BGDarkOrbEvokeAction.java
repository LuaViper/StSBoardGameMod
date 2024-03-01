package BoardGame.actions;

import BoardGame.screen.TargetSelectScreen;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

public class BGDarkOrbEvokeAction extends AbstractGameAction {
    private DamageInfo info;

    private static final float DURATION = 0.1F;

    private static final float POST_ATTACK_WAIT_DUR = 0.1F;

    private boolean muteSfx = false;

    public BGDarkOrbEvokeAction(DamageInfo info, AbstractGameAction.AttackEffect effect) {
        this.info = info;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = effect;
        this.duration = 0.1F;
    }

    public void update() {
        if (this.duration == 0.1F) {

            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                if (target != null) {
                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, this.attackEffect, this.muteSfx));
                    if (this.attackEffect == AbstractGameAction.AttackEffect.POISON) {
                        target.tint.color = Color.CHARTREUSE.cpy();
                        target.tint.changeColor(Color.WHITE.cpy());
                    } else if (this.attackEffect == AbstractGameAction.AttackEffect.FIRE) {
                        target.tint.color = Color.RED.cpy();
                        target.tint.changeColor(Color.WHITE.cpy());
                    }
                    target.damage(this.info);
                    if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead())
                        AbstractDungeon.actionManager.clearPostCombatActions();
                    if (!Settings.FAST_MODE)
                        addToTop((AbstractGameAction)new WaitAction(0.1F));
                }
            };
            addToTop((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Dark Orb."));
        }

        tickDuration();
    }
}
