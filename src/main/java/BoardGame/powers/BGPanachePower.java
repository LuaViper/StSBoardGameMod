package BoardGame.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGPanachePower
        extends AbstractBGPower {
    public static final String POWER_ID = "BGPanachePower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGPanachePower");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int CARD_AMT = 5;
    private int damage;

    public BGPanachePower(AbstractCreature owner, int damage) {
        this.name = NAME;
        this.ID = "BGPanachePower";
        this.owner = owner;
        this.amount=this.damage;
        this.damage = damage;
        updateDescription();
        loadRegion("panache");
    }


    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.damage + DESCRIPTIONS[1];
    }


    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.damage += stackAmount;
        updateDescription();
    }


    public void atEndOfTurnPreEndTurnCards(boolean isPlayer){
        if (AbstractDungeon.player.hand.isEmpty()) {
            addToBot((AbstractGameAction) new DamageAllEnemiesAction(AbstractDungeon.player,
                    DamageInfo.createDamageMatrix(this.damage, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
    }
}


