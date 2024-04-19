package BoardGame.powers;

import BoardGame.actions.BGDiscardCorpseExplosionAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import BoardGame.multicharacter.MultiCreature;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGCorpseExplosionPower extends AbstractBGPower {
    public static final String POWER_ID = "BoardGame:BGCorpseExplosionPower";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGCorpseExplosionPower");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private AbstractCard originalcard;

    public BGCorpseExplosionPower(AbstractCreature owner, AbstractCreature player, int amount, AbstractCard originalcard) {
        this.name = NAME;
        this.ID = "BoardGame:BGCorpseExplosionPower";
        this.owner = owner;
        this.amount = amount;
        this.type = AbstractPower.PowerType.DEBUFF;
        this.originalcard=originalcard;
        updateDescription();
        loadRegion("cExplosion");
    }

    public void onDeath() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead() &&
                this.owner.currentHealth <= 0) {
            addToBot((AbstractGameAction) new DamageAllEnemiesAction(
                    AbstractDungeon.player,
                    DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE
                    ));
            AbstractCard card=originalcard.makeStatEquivalentCopy();
            addToBot((AbstractGameAction) new BGDiscardCorpseExplosionAction(card));
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}



