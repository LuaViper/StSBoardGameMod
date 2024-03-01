package BoardGame.orbs;

import BoardGame.actions.BGLightningOrbEvokeAction;
import BoardGame.actions.BGLightningOrbPassiveAction;
import basemod.abstracts.CustomOrb;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningOrbActivateEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningOrbPassiveEffect;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

public class BGLightning extends CustomOrb {
    public static final String ORB_ID = "BoardGame:BGLightning";

    private static final OrbStrings orbString = CardCrawlGame.languagePack.getOrbString("BoardGame:BGLightning");
    public static final String[] DESCRIPTIONS = orbString.DESCRIPTION;

    private static final int PASSIVE_AMOUNT = 1;
    private static final int EVOKE_AMOUNT = 2;

    private float vfxTimer = 1.0F;

    private static final float PI_DIV_16 = 0.19634955F;

    private static final float ORB_WAVY_DIST = 0.05F;

    private static final float PI_4 = 12.566371F;

    private static final float ORB_BORDER_SCALE = 1.2F;


    public BGLightning() {
        super(ORB_ID, orbString.NAME, PASSIVE_AMOUNT, EVOKE_AMOUNT, DESCRIPTIONS[0], DESCRIPTIONS[1], "images/orbs/lightning.png");

        updateDescription();
        angle = MathUtils.random(360.0f); // More Animation-related Numbers
        channelAnimTimer = 0.5f;
    }

    public void applyFocus() {
        {
            AbstractPower power = AbstractDungeon.player.getPower("BGOrbEvokePower");
            if (power != null) {
                this.evokeAmount = Math.max(0, this.baseEvokeAmount + power.amount);
            } else {
                this.evokeAmount = this.baseEvokeAmount;
            }
        }
        this.passiveAmount = this.basePassiveAmount;
        {
            AbstractPower power = AbstractDungeon.player.getPower("BGOrbEndOfTurnPower");
            if (power != null) {
                this.passiveAmount += power.amount;
            }
        }
        {
            AbstractPower power = AbstractDungeon.player.getPower("BGStaticDischargePower");
            if (power != null) {
                this.passiveAmount += power.amount;
            }
        }
    }


    public void updateDescription() {
        applyFocus();
        this.description = orbString.DESCRIPTION[0] + this.passiveAmount + orbString.DESCRIPTION[1] + this.evokeAmount + orbString.DESCRIPTION[2];
    }

    public void onEvoke() {
        if (AbstractDungeon.player.hasPower("BGElectroPower")) {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction)new BGLightningOrbEvokeAction(new DamageInfo((AbstractCreature)AbstractDungeon.player, this.evokeAmount, DamageInfo.DamageType.THORNS), true));
        } else {
            AbstractDungeon.actionManager.addToTop((AbstractGameAction)new BGLightningOrbEvokeAction(new DamageInfo((AbstractCreature)AbstractDungeon.player, this.evokeAmount, DamageInfo.DamageType.THORNS), false));
        }
    }

    public void onEndOfTurn() {
        if (AbstractDungeon.player.hasPower("BGElectroPower")) {
            float speedTime = 0.2F / AbstractDungeon.player.orbs.size();
            if (Settings.FAST_MODE)
                speedTime = 0.0F;
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.LIGHTNING), speedTime));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BGLightningOrbEvokeAction(new DamageInfo((AbstractCreature)AbstractDungeon.player, this.passiveAmount, DamageInfo.DamageType.THORNS), true));
        } else {
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new BGLightningOrbPassiveAction(new DamageInfo((AbstractCreature)AbstractDungeon.player, this.passiveAmount, DamageInfo.DamageType.THORNS), this, false));
        }
    }

    private void triggerPassiveEffect(DamageInfo info, boolean hitAll) {
        if (!hitAll) {
            AbstractMonster abstractMonster = AbstractDungeon.getRandomMonster();
            if (abstractMonster != null) {
                float speedTime = 0.2F / AbstractDungeon.player.orbs.size();
                if (Settings.FAST_MODE)
                    speedTime = 0.0F;
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)abstractMonster, info, AbstractGameAction.AttackEffect.NONE, true));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.LIGHTNING), speedTime));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new LightningEffect(((AbstractCreature)abstractMonster).drawX, ((AbstractCreature)abstractMonster).drawY), speedTime));
                AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("ORB_LIGHTNING_EVOKE"));
            }
        } else {
            float speedTime = 0.2F / AbstractDungeon.player.orbs.size();
            if (Settings.FAST_MODE)
                speedTime = 0.0F;
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.LIGHTNING), speedTime));
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)AbstractDungeon.player,

                    DamageInfo.createDamageMatrix(info.base, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
            for (AbstractMonster m3 : (AbstractDungeon.getMonsters()).monsters) {
                if (!m3.isDeadOrEscaped() && !m3.halfDead)
                    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new VFXAction((AbstractGameEffect)new LightningEffect(m3.drawX, m3.drawY), speedTime));
            }
            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new SFXAction("ORB_LIGHTNING_EVOKE"));
        }
    }

    public void triggerEvokeAnimation() {
        CardCrawlGame.sound.play("ORB_LIGHTNING_EVOKE", 0.1F);
        AbstractDungeon.effectsQueue.add(new LightningOrbActivateEffect(this.cX, this.cY));
    }

    public void updateAnimation() {
        super.updateAnimation();
        this.angle += Gdx.graphics.getDeltaTime() * 180.0F;
        this.vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0F) {
            AbstractDungeon.effectList.add(new LightningOrbPassiveEffect(this.cX, this.cY));
            if (MathUtils.randomBoolean())
                AbstractDungeon.effectList.add(new LightningOrbPassiveEffect(this.cX, this.cY));
            this.vfxTimer = MathUtils.random(0.15F, 0.8F);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        this.shineColor.a = this.c.a / 2.0F;    //source code is unreliable; check intellij classes
        sb.setColor(this.shineColor);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale +
                MathUtils.sin(this.angle / 12.566371F) * 0.05F + 0.19634955F, this.scale * 1.2F, this.angle, 0, 0, 96, 96, false, false);
        sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 1.2F, this.scale +
                MathUtils.sin(this.angle / 12.566371F) * 0.05F + 0.19634955F, -this.angle, 0, 0, 96, 96, false, false);
        sb.setBlendFunction(770, 771);
        sb.setColor(this.c);
        sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, this.angle / 12.0F, 0, 0, 96, 96, false, false);
        renderText(sb);
        this.hb.render(sb);
    }

    public void playChannelSFX() {
        CardCrawlGame.sound.play("ORB_LIGHTNING_CHANNEL", 0.1F);
    }

    public CustomOrb makeCopy() {
        return new BGLightning();
    }
}

