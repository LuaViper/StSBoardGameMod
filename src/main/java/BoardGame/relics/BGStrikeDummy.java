package BoardGame.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.cards.AbstractCard;

public class BGStrikeDummy extends AbstractBGRelic  {
    public static final String ID = "BGStrikeDummy";

    public BGStrikeDummy() {
        super("BGStrikeDummy", "dummy.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.HEAVY);
    }

    public int getPrice() {return 8;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0] + 1 + this.DESCRIPTIONS[1];
    }


    public float atDamageModify(float damage, AbstractCard c) {
        // if (c.hasTag(AbstractCard.CardTags.STRIKE)) {
        if (c.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
            return damage + 1.0F;
        }
        return damage;
    }



    public AbstractRelic makeCopy() {
        return new BGStrikeDummy();
    }
}


