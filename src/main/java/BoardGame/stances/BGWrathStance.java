package BoardGame.stances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.stance.StanceAuraEffect;
import com.megacrit.cardcrawl.vfx.stance.StanceChangeParticleGenerator;
import com.megacrit.cardcrawl.vfx.stance.WrathParticleEffect;

public class BGWrathStance extends AbstractStance {
    public static final String STANCE_ID = "BGWrath";
    private static long sfxId = -1L;

    public BGWrathStance() {
        this.ID = "BGWrath";
        //TODO: localization
        this.name = "Wrath";
        updateDescription();
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type != DamageInfo.DamageType.NORMAL) return damage;

        float bonus=1.0F;
        AbstractPower p = AbstractDungeon.player.getPower("BGSimmeringFuryPower");
        if(p!=null) bonus+=p.amount;
        return damage + bonus;
    }

    public void onEndOfTurn() {
        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new DamageAction((AbstractCreature)AbstractDungeon.player, new DamageInfo((AbstractCreature)AbstractDungeon.player, 1, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
    }

    public void updateAnimation() {
        if (!Settings.DISABLE_EFFECTS) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.05F;
                AbstractDungeon.effectsQueue.add(new WrathParticleEffect());
            }
        }
        this.particleTimer2 -= Gdx.graphics.getDeltaTime();
        if (this.particleTimer2 < 0.0F) {
            this.particleTimer2 = MathUtils.random(0.3F, 0.4F);
            AbstractDungeon.effectsQueue.add(new StanceAuraEffect("Wrath"));
        }
    }

    public void updateDescription() {
        //TODO: localization
        this.description = "+1 to your [BoardGame:HitIcon] . NL #yEnd #yof #yturn: NL Take 1 damage.";
    }

    public void onEnterStance() {
        if (sfxId != -1L)
            stopIdleSfx();
        CardCrawlGame.sound.play("STANCE_ENTER_WRATH");
        sfxId = CardCrawlGame.sound.playAndLoop("STANCE_LOOP_WRATH");
        AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.SCARLET, true));
        AbstractDungeon.effectsQueue.add(new StanceChangeParticleGenerator(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, "Wrath"));
    }

    public void onExitStance() {
        stopIdleSfx();
    }

    public void stopIdleSfx() {
        if (sfxId != -1L) {
            CardCrawlGame.sound.stop("STANCE_LOOP_WRATH", sfxId);
            sfxId = -1L;
        }
    }



    @SpirePatch2(clz= AbstractStance.class, method="getStanceFromName",
            paramtypez={String.class})
    public static class StancesPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractStance> Prefix( String ___name){
            if (___name.equals("BGWrath")) {
                return SpireReturn.Return(new BGWrathStance());
            }else if(___name.equals("BGCalm")){
                return SpireReturn.Return(new BGCalmStance());
            }
            return SpireReturn.Continue();
        }
    }

}

