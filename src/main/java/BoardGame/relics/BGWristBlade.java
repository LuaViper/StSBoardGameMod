package BoardGame.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGWristBlade extends AbstractBGRelic  {
    public static final String ID = "BGWristBlade";

    //TODO: in BG, does wrist blade affect X cost cards played for 0?  (relevant for Whirlwind+)  VG says no btw.
    public BGWristBlade() {
        super("BGWristBlade", "wBlade.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.FLAT);
    }


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public AbstractRelic makeCopy() {
        return new BGWristBlade();
    }



    public float atDamageModify(float damage, AbstractCard c) {
        if (c.costForTurn == 0 || (c.freeToPlayOnce && c.cost != -1)) {
            return damage + 1.0F;
        }
        return damage;
    }
}


