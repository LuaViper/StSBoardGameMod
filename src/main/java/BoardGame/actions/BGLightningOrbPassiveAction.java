package BoardGame.actions;

import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

public class BGLightningOrbPassiveAction extends AbstractGameAction {
    private DamageInfo info;

    private AbstractOrb orb;

    private boolean hitAll;

    public BGLightningOrbPassiveAction(DamageInfo info, AbstractOrb orb, boolean hitAll) {
        this.info = info;
        this.orb = orb;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = AbstractGameAction.AttackEffect.NONE;
        this.hitAll = hitAll;
    }

    public void update() {
        if (!this.hitAll) {
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                AbstractMonster abstractMonster = target;

                if (abstractMonster != null) {
                    float speedTime = 0.2F / AbstractDungeon.player.orbs.size();
                    if (Settings.FAST_MODE)
                        speedTime = 0.0F;
                    this.info.output = AbstractOrb.applyLockOn((AbstractCreature) abstractMonster, this.info.base);
                    addToTop((AbstractGameAction) new DamageAction((AbstractCreature) abstractMonster, this.info, AbstractGameAction.AttackEffect.NONE, true));
                    addToTop((AbstractGameAction) new VFXAction((AbstractGameEffect) new LightningEffect(((AbstractCreature) abstractMonster).drawX, ((AbstractCreature) abstractMonster).drawY), speedTime));
                    addToTop((AbstractGameAction) new SFXAction("ORB_LIGHTNING_EVOKE"));
                    if (this.orb != null)
                        addToTop((AbstractGameAction) new VFXAction((AbstractGameEffect) new OrbFlareEffect(this.orb, OrbFlareEffect.OrbFlareColor.LIGHTNING), speedTime));
                }
            };
            addToTop((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Lightning Orb."));
        } else {
            float speedTime = 0.2F / AbstractDungeon.player.orbs.size();
            if (Settings.FAST_MODE)
                speedTime = 0.0F;
            addToTop((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)AbstractDungeon.player,
                    DamageInfo.createDamageMatrix(this.info.base, true, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
            for (AbstractMonster m3 : (AbstractDungeon.getMonsters()).monsters) {
                if (!m3.isDeadOrEscaped() && !m3.halfDead)
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new LightningEffect(m3.drawX, m3.drawY), speedTime));
            }
            addToTop((AbstractGameAction)new SFXAction("ORB_LIGHTNING_EVOKE"));
            if (this.orb != null)
                addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new OrbFlareEffect(this.orb, OrbFlareEffect.OrbFlareColor.LIGHTNING), speedTime));
        }
        this.isDone = true;
    }
}

