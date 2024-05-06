package BoardGame.powers;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import com.megacrit.cardcrawl.vfx.combat.OmegaFlashEffect;

public class BGOmegaPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGOmegaPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGOmegaPower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BGOmegaPower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = "OmegaPower";
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        loadRegion("omega");
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }

    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (isPlayer) {
            flash();
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m != null && !m.isDeadOrEscaped()) {
                    if (Settings.FAST_MODE) {
                        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new OmegaFlashEffect(m.hb.cX, m.hb.cY)));
                        continue;
                    }
                    addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new OmegaFlashEffect(m.hb.cX, m.hb.cY), 0.2F));
                }
            }
            addToBot((AbstractGameAction)new DamageAllEnemiesAction(AbstractDungeon.player,

                    DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE, true));
        }
    }
}
