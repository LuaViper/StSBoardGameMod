package BoardGame.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BGConjureBladePower
        extends AbstractBGPower {
    public static final String POWER_ID = "BGConjureBladePower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGConjureBladePower");

    public BGConjureBladePower(AbstractCreature owner, int amt) {
        this.name = powerStrings.NAME;
        this.ID = "BGConjureBladePower";
        this.owner = owner;
        this.amount = amt;
        updateDescription();
        loadRegion("accuracy");
    }


    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }


    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (card.hasTag(AbstractCard.CardTags.STARTER_STRIKE))
            return damage + this.amount;
        return damage;
    }

}



